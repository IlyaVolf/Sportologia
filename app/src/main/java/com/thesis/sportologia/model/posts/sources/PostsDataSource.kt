package com.thesis.sportologia.model.posts.sources

import androidx.paging.PagingData
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import kotlinx.coroutines.flow.Flow

interface PostsDataSource {

    suspend fun getPagedUserPosts(userId: String, lastPostId: String?, pageSize: Int):  List<PostDataEntity>

    suspend fun createPost(postDataEntity: PostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postId: String)

    suspend fun setIsLiked(userId: String, postId: String, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, postId: String, isFavourite: Boolean)

}