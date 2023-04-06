package com.thesis.sportologia.model.posts.sources

import android.util.Log
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.model.posts.entities.PostFireStoreEntity
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

// TODO вообще нужно проверять внимательно на сущестсование документов с указанным айди. При тестировании!
class FireStorePostsDataSource @Inject constructor() : PostsDataSource {

    private val database = FirebaseFirestore.getInstance()

    override suspend fun getPagedUserPosts(
        userId: String,
        lastPostId: String?,
        pageSize: Int
    ): List<PostDataEntity> {

        val currentPagePosts: QuerySnapshot?
        val currentPageLikes = mutableListOf<Boolean>()
        val currentPageFavs = mutableListOf<Boolean>()

        if (lastPostId == null) {
            currentPagePosts = database.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get()
                .await()
        } else {
            currentPagePosts = database.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .startAfter(lastPostId)
                .get()
                .await()
        }

        Log.d("abcdef", "currentPagePosts")

        val currentPageIds = currentPagePosts.map { it.id }

        currentPagePosts.documents.forEach {
            it.reference.collection("likes").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    currentPageLikes.add(documentSnapshot.exists())
                }
                .await()
        }

        currentPagePosts.documents.forEach {
            it.reference.collection("favs").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    currentPageFavs.add(documentSnapshot.exists())
                }
                .await()
        }

        val posts = currentPagePosts.toObjects(PostFireStoreEntity::class.java)

        Log.d("abcdef", "sjkslgsgsg $posts $currentPageLikes $currentPageFavs")

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
                    photosUrls = posts[i].photosUrls!!,
                )
            )
        }

        Log.d("abcdef", "finish $res")

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
        )

        var postId: String
        database.collection("posts")
            .add(postFireStoreEntity)
            .addOnSuccessListener { documentReference ->
                postId = documentReference.id

                database.collection("posts").document(postId)
                    .collection("likes")

                database.collection("posts").document(postId)
                    .collection("favs")

            }
            .addOnFailureListener { e ->
                throw Exception(e)
            }

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
    }

    override suspend fun setIsLiked(
        userId: String,
        postId: String,
        isLiked: Boolean
    ) {
        Log.d("abcdef", "$isLiked")
        if (!isLiked) {
            database.collection("posts").document(postId)
                .collection("likes")
                .document(userId)
                .set(hashMapOf<String, String>())
                .addOnSuccessListener {
                    Log.d("abcdef", "adaaddadaad")
                }
                .addOnFailureListener { e ->
                    Log.d("abcdef", "$e")
                    throw Exception(e)
                }
                .await()
        } else {
            database.collection("posts").document(postId)
                .collection("likes")
                .document(userId)
                .delete()
                .addOnFailureListener { e ->
                    Log.d("abcdef", "$e")
                    throw Exception(e)
                }
                .await()
        }
    }

    override suspend fun setIsFavourite(userId: String, postId: String, isFavourite: Boolean) {
        if (!isFavourite) {
            database.collection("posts").document(postId)
                .collection("favs")
                .document(userId)
            // TODO в пользователе добавить id поста
        } else {
            database.collection("posts").document(postId)
                .collection("favs")
                .document(userId)
                .delete()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
                .await()
            // TODO в пользователе удалить id поста
        }
    }

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }
}