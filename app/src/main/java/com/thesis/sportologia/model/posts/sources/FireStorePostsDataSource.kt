package com.thesis.sportologia.model.posts.sources

import android.util.Log
import com.google.firebase.firestore.*
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.model.posts.entities.PostFireStoreEntity
import com.thesis.sportologia.model.users.entities.UserFireStoreEntity
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

// TODO вообще нужно проверять внимательно на сущестсование документов с указанным айди. При тестировании!
// TODO вообще храним только id автора, по которому получим пользователяя и данные: аватар, имя и т.п
class FireStorePostsDataSource @Inject constructor() : PostsDataSource {

    /** т.к. версия пробная и отсутствуют Google Cloud Functions, для ускорения загрузки во вред
    оптимизации размера данных, мы получаем сразу все id пользователей, которыйп оставили лайк. По-началу
    ничего страшно, когда лайков мало. Зато потом размер получаемых клиентом докмуентов будет
    огромных, если лайков, например, миллион.
    Поэтому вообще это нужно на сервере проверять, лайкнул или нет. И не получать эти списки ни
    в коем случае. Но пока техническо мозможно только такая реализация */

    private val database = FirebaseFirestore.getInstance()

    override suspend fun getPagedUserPosts(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        if (lastMarker == null) {
            currentPageDocuments = database.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get()
                .await()
        } else {
            currentPageDocuments = database.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get()
                .await()
        }

        val posts = currentPageDocuments.toObjects(PostFireStoreEntity::class.java)

        val userDocument = database.collection("users")
            .document(userId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val user = userDocument.toObject(UserFireStoreEntity::class.java)
            ?: throw Exception("error user loading")

        currentPageDocuments.forEach {
            currentPageIds.add(it.id)
        }
        posts.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<PostDataEntity>()
        for (i in posts.indices) {
            res.add(
                PostDataEntity(
                    id = currentPageIds[i],
                    authorId = user.id!!,
                    authorName = user.name!!,
                    profilePictureUrl = user.profilePhotoURI,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    userType = when (user.userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    postedDate = posts[i].postedDate!!,
                    photosUrls = posts[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserSubscribedOnPosts(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        val followersIdsDocument = database.collection("users")
            .document(userId)
            .collection("followersIds")
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }.await()

        val followersIds = followersIdsDocument.map { it.id }

        val usersDocuments = database.collection("users")
            .whereIn(FieldPath.documentId(), followersIds)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val users = usersDocuments.toObjects(UserFireStoreEntity::class.java)
        val usersMap = hashMapOf<String, UserFireStoreEntity>()
        users.forEach { usersMap[it.id!!] = it }

        if (lastMarker == null) {
            if (userType == null) {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", followersIds)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            } else {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", followersIds)
                    .whereEqualTo("userType", userType)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            }
        } else {
            if (userType == null) {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", followersIds)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .await()
            } else {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", followersIds)
                    .whereEqualTo("userType", userType)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .await()
            }
        }

        val posts = currentPageDocuments.toObjects(PostFireStoreEntity::class.java)

        currentPageDocuments.forEach {
            currentPageIds.add(it.id)
        }
        posts.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))
        }

        val res = mutableListOf<PostDataEntity>()
        for (i in posts.indices) {
            val authorId = posts[i].authorId!!
            res.add(
                PostDataEntity(
                    id = currentPageIds[i],
                    authorId = authorId,
                    authorName = usersMap[authorId]!!.name!!,
                    profilePictureUrl = usersMap[authorId]!!.profilePhotoURI,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    userType = when (usersMap[authorId]!!.userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    postedDate = posts[i].postedDate!!,
                    photosUrls = posts[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getPagedUserFavouritePosts(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()
        val currentPageAuthors = mutableListOf<UserFireStoreEntity>()

        if (lastMarker == null) {
            if (userType == null) {
                currentPageDocuments = database.collection("posts")
                    .whereArrayContains("usersIdsFavs", userId)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { Log.d("abcdef", "$it"); throw Exception(it) }
                    .await()
                Log.d("abcdef", "111")
            } else {
                currentPageDocuments = database.collection("posts")
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereEqualTo("userType", userType)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
        } else {
            if (userType == null) {
                currentPageDocuments = database.collection("posts")
                    .whereArrayContains("usersIdsFavs", userId)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("posts")
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereEqualTo("userType", userType)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
        }

        val posts = currentPageDocuments.toObjects(PostFireStoreEntity::class.java)
        Log.d("abcdef", "222")

        posts.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))

            val authorDocument = database.collection("users")
                .document(it.authorId!!)
                .get()
                .addOnFailureListener {
                    Log.d("abcdef", "$it")
                }
                .await()

            Log.d("abcdef", "333")

            val author =
                authorDocument.toObject(UserFireStoreEntity::class.java) ?: throw Exception()

            Log.d("abcdef", "444 $author")

            currentPageAuthors.add(author)
        }

        Log.d("abcdef", "555")

        val res = mutableListOf<PostDataEntity>()
        for (i in posts.indices) {
            res.add(
                PostDataEntity(
                    id = posts[i].id,
                    authorId = currentPageAuthors[i].id!!,
                    authorName = currentPageAuthors[i].name!!,
                    profilePictureUrl = currentPageAuthors[i].profilePhotoURI,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    userType = when (currentPageAuthors[i].userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    postedDate = posts[i].postedDate!!,
                    photosUrls = posts[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getPost(postId: String, userId: String): PostDataEntity? {
        val userDocument = database.collection("users")
            .document(userId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val user = userDocument.toObject(UserFireStoreEntity::class.java)
            ?: throw Exception("error user loading")

        val postDocument = database.collection("posts")
            .document(postId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val post = postDocument.toObject(PostFireStoreEntity::class.java) ?: return null

        val res = PostDataEntity(
            id = post.id,
            authorId = user.id!!,
            authorName = user.name!!,
            profilePictureUrl = user.profilePhotoURI,
            text = post.text!!,
            likesCount = post.likesCount!!,
            userType = when (user.userType) {
                "ATHLETE" -> UserType.ATHLETE
                "ORGANIZATION" -> UserType.ORGANIZATION
                else -> throw Exception()
            },
            isLiked = post.usersIdsLiked.contains(userId),
            isFavourite = post.usersIdsFavs.contains(userId),
            postedDate = post.postedDate!!,
            photosUrls = post.photosUrls,
        )

        return res
    }

    override suspend fun createPost(postDataEntity: PostDataEntity) {
        val postFireStoreEntity = hashMapOf(
            "authorId" to postDataEntity.authorId,
            "text" to postDataEntity.text,
            "likesCount" to postDataEntity.likesCount,
            "postedDate" to Calendar.getInstance().timeInMillis,
            "photosUrls" to postDataEntity.photosUrls,
            "usersIdsLiked" to listOf<String>(),
            "usersIdsFavs" to listOf<String>()
        )

        // TODO должна быть атомарной

        val documentRef = database.collection("posts").document()

        documentRef
            .set(postFireStoreEntity)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        database.collection("posts")
            .add(postFireStoreEntity)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        documentRef
            .update(hashMapOf<String, Any>("id" to documentRef.id))
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

    }

    override suspend fun updatePost(postDataEntity: PostDataEntity) {
        val postFireStoreEntity = hashMapOf(
            "text" to postDataEntity.text,
            "photosUrls" to postDataEntity.photosUrls,
        )

        database.collection("posts")
            .document(postDataEntity.id!!)
            .update(postFireStoreEntity)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
    }

    override suspend fun deletePost(postId: String) {
        database.collection("posts")
            .document(postId)
            .delete()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
    }

    override suspend fun setIsLiked(
        userId: String,
        postDataEntity: PostDataEntity,
        isLiked: Boolean
    ) {
        Log.d("abcdef", "setIsLiked")
        if (isLiked) {
            database.collection("posts")
                .document(postDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "likesCount" to FieldValue.increment(1L),
                        "usersIdsLiked" to FieldValue.arrayUnion(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        } else {
            database.collection("posts")
                .document(postDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "likesCount" to FieldValue.increment(-1L),
                        "usersIdsLiked" to FieldValue.arrayRemove(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        }
    }

    override suspend fun setIsFavourite(
        userId: String,
        postDataEntity: PostDataEntity,
        isFavourite: Boolean
    ) {
        if (isFavourite) {
            database.collection("posts")
                .document(postDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayUnion(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }

            database.collection("users")
                .document(userId)
                .collection("favsPosts")
                .document(postDataEntity.id)
                .set(hashMapOf<String, Any>())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
        } else {
            database.collection("posts")
                .document(postDataEntity.id!!)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayRemove(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()

            database.collection("users")
                .document(userId)
                .collection("favsPosts")
                .document(postDataEntity.id)
                .delete()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        }
    }
}