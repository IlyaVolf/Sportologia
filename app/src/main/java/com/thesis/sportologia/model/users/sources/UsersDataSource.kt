package com.thesis.sportologia.model.users.sources

import com.thesis.sportologia.model.users.entities.*

interface UsersDataSource {

    suspend fun getAccount(): AccountDataEntity

    suspend fun getUser(currentUserId: String, userId: String): User

    suspend fun signIn(email: String, password: String): String

    suspend fun signUp(userCreateEditDataEntity: UserCreateEditDataEntity)

    suspend fun updateUser(userCreateEditDataEntity: UserCreateEditDataEntity)

    suspend fun deleteUser(userId: String)

    suspend fun setIsSubscribed(followerId: String, followingId: String, isLiked: Boolean)

    suspend fun getPagedUsers(
        searchQuery: String,
        filter: FilterParamsUsers,
        lastMarker: String?,
        pageSize: Int
    ): List<UserSnippet>

    suspend fun getPagedFollowers(
        userId: String,
        lastMarker: String?,
        pageSize: Int
    ): List<UserSnippet>

    suspend fun getPagedFollowings(
        userId: String,
        lastMarker: String?,
        pageSize: Int
    ): List<UserSnippet>

    suspend fun checkEmailExists(email: String): Boolean

}