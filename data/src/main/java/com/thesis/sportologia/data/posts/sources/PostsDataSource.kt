package com.thesis.sportologia.data.posts.sources

import com.thesis.sportologia.core.entities.UserType
import com.thesis.sportologia.data.posts.entities.CreatePostDataEntity
import com.thesis.sportologia.data.posts.entities.PostDataEntity
import com.thesis.sportologia.data.users.entities.UserDataEntity

interface PostsDataSource {

    suspend fun getPagedUserPosts(
        userId: Long,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity>

    suspend fun getPagedSubscribedOnPosts(
        userId: Long,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity>

    suspend fun getPagedFavouritePosts(
        userId: Long,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity>

    suspend fun getPost(postId: Long, userId: Long): PostDataEntity

    suspend fun createPost(author: UserDataEntity, createPostDataEntity: CreatePostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postId: Long)

    suspend fun setIsLiked(userId: Long, postId: Long, isLiked: Boolean)

    suspend fun setIsFavourite(userId: Long, postId: Long, isFavourite: Boolean)

}