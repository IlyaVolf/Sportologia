package com.thesis.sportologia.sources

import androidx.paging.PagingData
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import kotlinx.coroutines.flow.Flow

interface PostsSource {

    // suspend fun getUserPosts(userId: Int): List<Post>

    suspend fun getPagedUserPosts(userId: Int): Flow<PagingData<PostDataEntity>>

    // suspend fun getUserSubscribedOnPosts(userId: Int, athTorgF: Boolean?): List<Post>

    suspend fun getPagedUserSubscribedOnPosts(userId: Int, athTorgF: Boolean?): Flow<PagingData<PostDataEntity>>

    suspend fun getUserFavouritePosts(userId: Int): List<PostDataEntity>

    suspend fun createPost(postDataEntity: PostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postDataEntity: PostDataEntity)

    suspend fun setIfLiked(userId: Int, postDataEntity: PostDataEntity)

    suspend fun setIfFavourite(userId: Int, postDataEntity: PostDataEntity, isFavourite: Boolean)

}