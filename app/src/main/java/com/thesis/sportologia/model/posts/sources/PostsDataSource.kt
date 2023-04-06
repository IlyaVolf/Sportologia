package com.thesis.sportologia.model.posts.sources

import com.thesis.sportologia.model.posts.entities.PostDataEntity

interface PostsDataSource {

    suspend fun createPost(postDataEntity: PostDataEntity)

    suspend fun setIsLiked(userId: String, postId: String, isLiked: Boolean)

}