package com.thesis.sportologia.data.posts.sources

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.thesis.sportologia.core.entities.UserType
import com.thesis.sportologia.data.posts.entities.CreatePostDataEntity
import com.thesis.sportologia.data.posts.entities.PostDataEntity
import com.thesis.sportologia.data.posts.entities.PostFirestoreEntity
import com.thesis.sportologia.data.users.entities.UserDataEntity
import com.thesis.sportologia.data.users.entities.UserFirestoreEntity
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestorePostsDataSource : PostsDataSource {

    private val database = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()


    override suspend fun getPagedUserPosts(
        userId: Long,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageIds = mutableListOf<String>()
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        if (lastMarker == null) {
            currentPageDocuments = database.collection(POSTS_PATH)
                .whereEqualTo("authorId", userId)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get(Source.SERVER)
                .await()
        } else {
            currentPageDocuments = database.collection(POSTS_PATH)
                .whereEqualTo("authorId", userId)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastMarker)
                .get(Source.SERVER)
                .await()
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val posts = currentPageDocuments.toObjects(PostFirestoreEntity::class.java)

        val userDocument = database.collection(USERS_PATH)
            .document(userId)
            .get(Source.SERVER)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val user = userDocument.toObject(UserFirestoreEntity::class.java)
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
                    authorProfilePictureUrl = user.profilePhotoURI,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    authorUserType = when (user.userType) {
                        "ATHLETE" -> UserType.ATHLETE
                        "ORGANIZATION" -> UserType.ORGANIZATION
                        else -> throw Exception()
                    },
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    dateCreated = posts[i].postedDate!!,
                    photosUrls = posts[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getPagedSubscribedOnPosts(
        userId: Long,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity> {
        try {

            val currentPageDocuments: QuerySnapshot?
            val currentPageIds = mutableListOf<String>()
            val currentPageLikes = mutableListOf<Boolean>()
            val currentPageFavs = mutableListOf<Boolean>()

            val followersIdsDocument = database.collection(USERS_PATH)
                .document(userId)
                .collection("followersIds")
                .get(Source.SERVER)
                .await()

            val followersIds = followersIdsDocument.map { it.id }

            val usersDocuments = database.collection(USERS_PATH)
                .whereIn(FieldPath.documentId(), followersIds)
                .get(Source.SERVER)
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()

            val users = usersDocuments.toObjects(UserFirestoreEntity::class.java)
            val usersMap = hashMapOf<String, UserFirestoreEntity>()
            users.forEach { usersMap[it.id!!] = it }

            if (lastMarker == null) {
                if (userType == null) {
                    currentPageDocuments = database.collection(POSTS_PATH)
                        .whereIn("authorId", followersIds)
                        .orderBy("postedDate", Query.Direction.DESCENDING)
                        .limit(pageSize.toLong())
                        .get(Source.SERVER)
                        .await()
                } else {
                    currentPageDocuments = database.collection(POSTS_PATH)
                        .whereIn("authorId", followersIds)
                        .whereEqualTo("userType", userType)
                        .orderBy("postedDate", Query.Direction.DESCENDING)
                        .limit(pageSize.toLong())
                        .get(Source.SERVER)
                        .await()
                }
            } else {
                if (userType == null) {
                    currentPageDocuments = database.collection(POSTS_PATH)
                        .whereIn("authorId", followersIds)
                        .orderBy("postedDate", Query.Direction.DESCENDING)
                        .limit(pageSize.toLong())
                        .startAfter(lastMarker)
                        .get(Source.SERVER)
                        .await()
                } else {
                    currentPageDocuments = database.collection(POSTS_PATH)
                        .whereIn("authorId", followersIds)
                        .whereEqualTo("userType", userType)
                        .orderBy("postedDate", Query.Direction.DESCENDING)
                        .limit(pageSize.toLong())
                        .startAfter(lastMarker)
                        .get(Source.SERVER)
                        .await()
                }
            }

            val posts = currentPageDocuments.toObjects(PostFirestoreEntity::class.java)

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
                        authorProfilePictureUrl = usersMap[authorId]!!.profilePhotoURI,
                        text = posts[i].text!!,
                        likesCount = posts[i].likesCount!!,
                        authorUserType = when (usersMap[authorId]!!.userType) {
                            "ATHLETE" -> UserType.ATHLETE
                            "ORGANIZATION" -> UserType.ORGANIZATION
                            else -> throw Exception()
                        },
                        isLiked = currentPageLikes[i],
                        isFavourite = currentPageFavs[i],
                        dateCreated = posts[i].postedDate!!,
                        photosUrls = posts[i].photosUrls,
                    )
                )
            }

            return res
        } catch (e: Exception) {
            if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                Log.d("abcdef", "ConnectionException")
                throw ConnectionException(e)
            } else {
                throw AppException(e)
            }
        }
    }

    override suspend fun getPagedFavouritePosts(
        userId: Long,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity> {
        val currentPageDocuments: QuerySnapshot?
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()
        val currentPageAuthors = mutableListOf<UserFirestoreEntity>()

        if (lastMarker == null) {
            if (userType == null) {
                currentPageDocuments = database.collection(POSTS_PATH)
                    .whereArrayContains("usersIdsFavs", userId)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get(Source.SERVER)
                    .addOnFailureListener { Log.d("abcdef", "$it"); throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection(POSTS_PATH)
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereEqualTo("userType", userType)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get(Source.SERVER)
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
        } else {
            if (userType == null) {
                currentPageDocuments = database.collection(POSTS_PATH)
                    .whereArrayContains("usersIdsFavs", userId)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get(Source.SERVER)
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            } else {
                currentPageDocuments = database.collection(POSTS_PATH)
                    .whereArrayContains("usersIdsFavs", userId)
                    .whereEqualTo("userType", userType)
                    .orderBy("postedDate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .startAfter(lastMarker)
                    .get(Source.SERVER)
                    .addOnFailureListener { throw Exception(it) }
                    .await()
            }
        }

        if (currentPageDocuments.isEmpty) {
            return emptyList()
        }

        val posts = currentPageDocuments.toObjects(PostFirestoreEntity::class.java)

        posts.forEach {
            currentPageLikes.add(it.usersIdsLiked.contains(userId))
            currentPageFavs.add(it.usersIdsFavs.contains(userId))

            val authorDocument = database.collection(USERS_PATH)
                .document(it.authorId!!)
                .get(Source.SERVER)
                .await()

            val author =
                authorDocument.toObject(UserFirestoreEntity::class.java) ?: throw Exception()

            currentPageAuthors.add(author)
        }

        val res = mutableListOf<PostDataEntity>()
        for (i in posts.indices) {
            res.add(
                PostDataEntity(
                    id = posts[i].id,
                    authorId = currentPageAuthors[i].id!!,
                    authorName = currentPageAuthors[i].name!!,
                    authorProfilePictureUrl = currentPageAuthors[i].profilePhotoURI,
                    text = posts[i].text!!,
                    likesCount = posts[i].likesCount!!,
                    authorUserType = currentPageAuthors[i].userType,
                    isLiked = currentPageLikes[i],
                    isFavourite = currentPageFavs[i],
                    dateCreated = posts[i].postedDate!!,
                    photosUrls = posts[i].photosUrls,
                )
            )
        }

        return res
    }

    override suspend fun getPost(postId: Long, userId: Long): PostDataEntity {
        val postDocument = database.collection(POSTS_PATH)
            .document(postId)
            .get(Source.SERVER)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()

        val post = postDocument.toObject(PostFirestoreEntity::class.java) ?: throw Exception()

        val userDocument = database.collection(USERS_PATH)
            .document(post.authorId!!)
            .get(Source.SERVER)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
        val user = userDocument.toObject(UserFirestoreEntity::class.java)
            ?: throw Exception("error user loading")

        val res = PostDataEntity(
            id = post.id,
            authorId = user.id!!,
            authorName = user.name!!,
            authorProfilePictureUrl = user.profilePhotoURI,
            authorUserType = user.userType,
            text = post.text!!,
            likesCount = post.likesCount!!,
            isLiked = post.usersIdsLiked.contains(userId),
            isFavourite = post.usersIdsFavs.contains(userId),
            dateCreated = post.postedDate!!,
            photosUrls = post.photosUrls,
        )

        return res
    }

    override suspend fun createPost(author: UserDataEntity, createPostDataEntity: CreatePostDataEntity) {

        val photosFirestore = mutableListOf<Uri>()
        createPostDataEntity.photosUrls.forEach {
            val photosRef = storage.reference.child("images/${UUID.randomUUID()}")
            val downloadUrl = photosRef.putFile(it.toUri())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            photosFirestore.add(downloadUrl)
        }

        val postFirestoreEntity = hashMapOf(
            "authorId" to author.id,
            "text" to createPostDataEntity.text,
            "likesCount" to 0,
            "postedDate" to Calendar.getInstance().timeInMillis,
            "photosUrls" to photosFirestore,
            "usersIdsLiked" to listOf<String>(),
            "usersIdsFavs" to listOf<String>()
        )

        // TODO должна быть атомарной


        val documentRef = database.collection(POSTS_PATH).document()

        documentRef
            .set(postFirestoreEntity)
            .addOnFailureListener { e ->
                Log.d("abcdef", "$e")
                throw Exception(e)
            }
            .await()

        documentRef
            .update(hashMapOf<String, Any>("id" to documentRef.id))
            .addOnFailureListener { e ->
                Log.d("abcdef", "$e")
                throw Exception(e)
            }
            .await()

    }

    private fun uploadPhotos(postPhotos: List<String>): List<String> {
        return emptyList()
    }

    override suspend fun updatePost(postDataEntity: PostDataEntity) {
        val firestorePhotosUrls = uploadPhotos(postDataEntity.photosUrls)
        val photosFirestore = postDataEntity.photosUrls
        /*val photosFirestore = mutableListOf<Uri>()
        postDataEntity.photosUrls.forEach {
            val photosRef = storage.reference.child("images/posts/${UUID.randomUUID()}")
            val fileUri = it.toUri()
            val downloadUrl = photosRef.putFile(it.toUri())
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
                .storage.downloadUrl.await()
            photosFirestore.add(downloadUrl)
        }*/

        val postFirestoreEntity = hashMapOf(
            "text" to postDataEntity.text,
            "photosUrls" to photosFirestore,
        )

        database.collection(POSTS_PATH)
            .document(postDataEntity.id!!)
            .update(postFirestoreEntity)
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
    }

    override suspend fun deletePost(postId: Long) {
        database.collection(POSTS_PATH)
            .document(postId)
            .delete()
            .addOnFailureListener { e ->
                throw Exception(e)
            }
            .await()
    }

    override suspend fun setIsLiked(
        userId: Long,
        postId: Long,
        isLiked: Boolean
    ) {
        if (isLiked) {
            database.collection(POSTS_PATH)
                .document(postId)
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
            database.collection(POSTS_PATH)
                .document(postId)
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
        userId: Long,
        postId: Long,
        isFavourite: Boolean
    ) {
        if (isFavourite) {
            database.collection(POSTS_PATH)
                .document(postId)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayUnion(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
        } else {
            database.collection(POSTS_PATH)
                .document(postId)
                .update(
                    hashMapOf<String, Any>(
                        "usersIdsFavs" to FieldValue.arrayRemove(userId)
                    )
                )
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
        }
    }

    companion object {
        const val POSTS_PATH = "posts"
        const val USERS_PATH = "users"
    }
}