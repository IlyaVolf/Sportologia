package com.thesis.sportologia.model.posts.sources

import com.thesis.sportologia.model.posts.entities.PostDataEntity

interface PostsDataSource {

    suspend fun createPost(postDataEntity: PostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postId: String)

    suspend fun setIsLiked(userId: String, postId: String, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, postId: String, isFavourite: Boolean)

}