package com.thesis.sportologia.model.posts

import androidx.paging.PagingData
import com.thesis.sportologia.model.posts.entities.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    suspend fun getPagedUserPosts(userId: Int): Flow<PagingData<Post>>

    suspend fun getPagedUserSubscribedOnPosts(userId: Int, athTorgF: Boolean?): Flow<PagingData<Post>>

    suspend fun getPagedUserFavouritePosts(athTorgF: Boolean?): Flow<PagingData<Post>>

    suspend fun getPost(postId: Long): Post?

    suspend fun createPost(post: Post)

    suspend fun updatePost(post: Post)

    suspend fun deletePost(postId: Long)

    suspend fun setIsLiked(userId: Int, post: Post, isLiked: Boolean)

    suspend fun setIsFavourite(userId: Int, post: Post, isFavourite: Boolean)

}