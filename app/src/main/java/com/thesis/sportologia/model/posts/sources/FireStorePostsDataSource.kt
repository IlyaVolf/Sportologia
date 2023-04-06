package com.thesis.sportologia.model.posts.sources

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.log

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

                val postLikesFireStoreEntity = hashMapOf<String, String>()
                database.collection("posts").document(postId)
                    .collection("likes")
                    .document("userIds")
                    .set(postLikesFireStoreEntity)
                    .addOnFailureListener { e ->
                        Log.d("abcdef", "$e")
                        throw Exception(e)
                    }

                val postFavsFireStoreEntity = hashMapOf<String, String>()
                database.collection("posts").document(postId)
                    .collection("favs")
                    .document("userIds")
                    .set(postFavsFireStoreEntity)
                    .addOnFailureListener { e ->
                        throw Exception(e)
                    }

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
        TODO("Not yet implemented")
    }

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }
}