package com.thesis.sportologia.model.posts

import androidx.paging.PagingData
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.posts.entities.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface PostsRepository {

    val localChanges: PostsLocalChanges
    val localChangesFlow: MutableStateFlow<OnChange<PostsLocalChanges>>

    suspend fun getPagedUserPosts(userId: String): Flow<PagingData<Post>>

    suspend fun getPagedUserSubscribedOnPosts(userId: String, athTorgF: Boolean?): Flow<PagingData<Post>>

    suspend fun getPagedUserFavouritePosts(athTorgF: Boolean?): Flow<PagingData<Post>>

    suspend fun getPost(postId: Long): Post?

    suspend fun createPost(post: Post)

    suspend fun updatePost(post: Post)

    suspend fun deletePost(postId: Long)

    suspend fun setIsLiked(userId: String, post: Post, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, post: Post, isFavourite: Boolean)

}