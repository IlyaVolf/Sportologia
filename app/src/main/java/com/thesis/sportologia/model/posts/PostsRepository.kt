package com.thesis.sportologia.model.posts

import androidx.paging.PagingData
import com.thesis.sportologia.model.posts.entities.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    suspend fun getUserPosts(userId: Int): List<Post>

    suspend fun getPagedUserPosts(userId: Int): Flow<PagingData<Post>>

    suspend fun getUserSubscribedOnPosts(userId: Int, athTorgF: Boolean?): List<Post>

    suspend fun getUserFavouritePosts(userId: Int): List<Post>

    suspend fun createPost(post: Post)

    suspend fun updatePost(post: Post)

    suspend fun deletePost(post: Post)

    suspend fun likePost(userId: Int, post: Post)

    suspend fun unlikePost(userId: Int, post: Post)

    suspend fun addPostToFavourites(userId: Int, post: Post)

    suspend fun removePostFromFavourites(userId: Int, post: Post)

}