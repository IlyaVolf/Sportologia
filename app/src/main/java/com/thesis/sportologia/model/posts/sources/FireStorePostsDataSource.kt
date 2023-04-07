package com.thesis.sportologia.model.posts.sources

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.model.posts.entities.PostFireStoreEntity
import com.thesis.sportologia.model.users.entities.UserFireStoreEntity
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

// TODO вообще нужно проверять внимательно на сущестсование документов с указанным айди. При тестировании!
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
                    authorId = posts[i].authorId!!,
                    authorName = posts[i].authorName!!,
                    profilePictureUrl = posts[i].profilePictureUrl,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    userType = when (posts[i].userType) {
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

        val userDocument = database.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    throw Exception("no such user")
                }
            }.await()

        val user = userDocument.toObject(UserFireStoreEntity::class.java) ?: return emptyList()

        if (lastMarker == null) {
            if (userType == null) {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", user.followersIds)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            } else {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", user.followersIds)
                    .whereEqualTo("userType", userType)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            }
        } else {
            if (userType == null) {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", user.followersIds)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .await()
            } else {
                currentPageDocuments = database.collection("posts")
                    .whereIn("authorId", user.followersIds)
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
            res.add(
                PostDataEntity(
                    id = currentPageIds[i],
                    authorId = posts[i].authorId!!,
                    authorName = posts[i].authorName!!,
                    profilePictureUrl = posts[i].profilePictureUrl,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    userType = when (posts[i].userType) {
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
        val usersFavsPostsIds = mutableListOf<String>()

        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        val userFavsPostsIdsDocuments = database.collection("users")
            .document(userId)
            .collection("favsPosts")
            .get()
            .await()

        userFavsPostsIdsDocuments.forEach {
            usersFavsPostsIds.add(it.id)
        }

        if (lastMarker == null) {
            if (userType == null) {
                currentPageDocuments = database.collection("posts")
                    .orderBy(FieldPath.documentId())
                    .whereIn(FieldPath.documentId(), usersFavsPostsIds)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("posts")
                    .orderBy(FieldPath.documentId())
                    .whereIn(FieldPath.documentId(), usersFavsPostsIds)
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
                    .orderBy(FieldPath.documentId())
                    .whereIn(FieldPath.documentId(), usersFavsPostsIds)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get()
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection("posts")
                    .orderBy(FieldPath.documentId())
                    .whereIn(FieldPath.documentId(), usersFavsPostsIds)
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
                    authorId = posts[i].authorId!!,
                    authorName = posts[i].authorName!!,
                    profilePictureUrl = posts[i].profilePictureUrl,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    userType = when (posts[i].userType) {
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
        val document = database.collection("posts")
            .document(postId)
            .get()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val post = document.toObject(PostFireStoreEntity::class.java) ?: return null

        val res = PostDataEntity(
            id = document.id,
            authorId = post.authorId!!,
            authorName = post.authorName!!,
            profilePictureUrl = post.profilePictureUrl,
            text = post.text!!,
            likesCount = post.likesCount!!,
            userType = when (post.userType) {
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
            "authorName" to postDataEntity.authorName,
            "profilePictureUrl" to postDataEntity.profilePictureUrl,
            "text" to postDataEntity.text,
            "likesCount" to postDataEntity.likesCount,
            "userType" to postDataEntity.userType,
            "postedDate" to Calendar.getInstance().timeInMillis,
            "photosUrls" to postDataEntity.photosUrls,
            "usersIdsLiked" to listOf<String>(),
            "usersIdsFavs" to listOf<String>()
        )

        database.collection("posts")
            .add(postFireStoreEntity)
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
            .addOnSuccessListener {
                // TODO удалить данные из пользователя
            }
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
                .await()
            // TODO в пользователе добавить id поста
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
            // TODO в пользователе добавить id поста
        }
    }
}