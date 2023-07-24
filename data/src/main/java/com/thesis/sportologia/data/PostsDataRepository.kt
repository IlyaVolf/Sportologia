package com.thesis.sportologia.data

import androidx.paging.PagingData
import com.thesis.sportologia.core.entities.UserType
import com.thesis.sportologia.data.posts.entities.CreatePostDataEntity
import com.thesis.sportologia.data.posts.entities.PostDataEntity
import kotlinx.coroutines.flow.Flow

interface PostsDataRepository {

    suspend fun getPagedUserPosts(userId: Long): Flow<PagingData<PostDataEntity>>

    suspend fun getPagedSubscribedOnPosts(
        userType: UserType?
    ): Flow<PagingData<PostDataEntity>>

    suspend fun getPagedFavouritePosts(
        userType: UserType?
    ): Flow<PagingData<PostDataEntity>>

    suspend fun getPost(postId: Long, userId: Long): PostDataEntity?

    suspend fun createPost(createPostDataEntity: CreatePostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postId: Long)

    suspend fun setIsLiked(postId: Long, isLiked: Boolean)

    suspend fun setIsFavourite(postId: Long, isFavourite: Boolean)

}