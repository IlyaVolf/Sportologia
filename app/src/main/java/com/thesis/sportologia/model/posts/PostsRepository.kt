package com.thesis.sportologia.model.posts

import androidx.paging.PagingData
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface PostsRepository {

    val localChanges: PostsLocalChanges
    val localChangesFlow: MutableStateFlow<OnChange<PostsLocalChanges>>

    suspend fun getPagedUserPosts(userId: String): Flow<PagingData<PostDataEntity>>

    suspend fun getPagedUserSubscribedOnPosts(userId: String, athTorgF: Boolean?): Flow<PagingData<PostDataEntity>>

    suspend fun getPagedUserFavouritePosts(athTorgF: Boolean?): Flow<PagingData<PostDataEntity>>

    suspend fun getPost(postId: String): PostDataEntity?

    suspend fun createPost(postDataEntity: PostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postId: String)

    suspend fun setIsLiked(userId: String, postDataEntity: PostDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, postDataEntity: PostDataEntity, isFavourite: Boolean)

}