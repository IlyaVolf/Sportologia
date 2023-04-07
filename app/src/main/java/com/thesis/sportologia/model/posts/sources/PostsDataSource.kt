package com.thesis.sportologia.model.posts.sources

import androidx.paging.PagingData
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import kotlinx.coroutines.flow.Flow

interface PostsDataSource {

    suspend fun getPagedUserPosts(
        userId: String,
        lastPostId: String?,
        pageSize: Int
    ): List<PostDataEntity>

    suspend fun getPost(postId: String, userId: String): PostDataEntity?

    suspend fun createPost(postDataEntity: PostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postId: String)

    suspend fun setIsLiked(userId: String, postDataEntity: PostDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, postDataEntity: PostDataEntity, isFavourite: Boolean)

}