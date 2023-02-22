package com.thesis.sportologia.model.posts

import com.thesis.sportologia.model.posts.entities.Post

interface PostsRepository {

    suspend fun getUserPosts(userId: Int): List<Post>

    suspend fun getUserSubscribedOnPosts(userId: Int): List<Post>

    suspend fun getUserFavouritePosts(userId: Int): List<Post>

    suspend fun createPost(post: Post)

    suspend fun updatePost(post: Post)

    suspend fun deletePost(post: Post)

    suspend fun likePost(userId: Int, post: Post)

    suspend fun unlikePost(userId: Int, post: Post)

    suspend fun addPostToFavourites(userId: Int, post: Post)

    suspend fun removePostFromFavourites(userId: Int, post: Post)

}