package com.thesis.sportologia.model.posts.sources

import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.model.users.entities.UserType

interface PostsDataSource {

    suspend fun getPagedUserPosts(
        userId: String,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity>

    suspend fun getPagedUserSubscribedOnPosts(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity>

    suspend fun getPagedUserFavouritePosts(
        userId: String,
        userType: UserType?,
        lastMarker: Long?,
        pageSize: Int
    ): List<PostDataEntity>

    suspend fun getPost(postId: String, userId: String): PostDataEntity

    suspend fun createPost(postDataEntity: PostDataEntity)

    suspend fun updatePost(postDataEntity: PostDataEntity)

    suspend fun deletePost(postId: String)

    suspend fun setIsLiked(userId: String, postDataEntity: PostDataEntity, isLiked: Boolean)

    suspend fun setIsFavourite(userId: String, postDataEntity: PostDataEntity, isFavourite: Boolean)

}