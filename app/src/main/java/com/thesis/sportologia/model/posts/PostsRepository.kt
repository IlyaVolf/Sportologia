package com.thesis.sportologia.model.posts

import androidx.paging.PagingData
import com.thesis.sportologia.model.posts.entities.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    // suspend fun getUserPosts(userId: Int): List<Post>

    suspend fun getPagedUserPosts(userId: Int): Flow<PagingData<Post>>

    // suspend fun getUserSubscribedOnPosts(userId: Int, athTorgF: Boolean?): List<Post>

    suspend fun getPagedUserSubscribedOnPosts(userId: Int, athTorgF: Boolean?): Flow<PagingData<Post>>

    suspend fun getUserFavouritePosts(userId: Int): List<Post>

    suspend fun createPost(post: Post)

    suspend fun updatePost(post: Post)

    suspend fun deletePost(postId: Long)

    suspend fun setIsLiked(userId: Int, post: Post, isLiked: Boolean)

    suspend fun setIsFavourite(userId: Int, post: Post, isFavourite: Boolean)

}