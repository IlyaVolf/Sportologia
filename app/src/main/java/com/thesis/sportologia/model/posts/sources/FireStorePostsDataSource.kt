package com.thesis.sportologia.model.posts.sources

import com.google.firebase.firestore.FirebaseFirestore
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import java.util.Calendar
import javax.inject.Inject

// TODO вообще нужно проверять внимательно на сущестсование документов с указанным айди. При тестировании!
class FireStorePostsDataSource @Inject constructor() : PostsDataSource {

    private val database = FirebaseFirestore.getInstance()

    override suspend fun createPost(postDataEntity: PostDataEntity) {
        val postFireStoreEntity = hashMapOf(
            "authorId" to postDataEntity.authorId,
            "authorName" to postDataEntity.authorName,
            "profilePictureUrl" to postDataEntity.profilePictureUrl,
            "text" to postDataEntity.text,
            "likesCount" to postDataEntity.likesCount,
            "isAuthorAthlete" to postDataEntity.isAuthorAthlete,
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
        if (!isLiked) {
            database.collection("posts").document(postId)
                .collection("likes")
                .document(userId)
        } else {
            database.collection("posts").document(postId)
                .collection("likes")
                .document(userId)
                .delete()
                .addOnFailureListener { e ->
                    throw Exception(e)
                }
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
            // TODO в пользователе удалить id поста
        }
    }

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }
}