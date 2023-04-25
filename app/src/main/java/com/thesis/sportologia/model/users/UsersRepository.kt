package com.thesis.sportologia.model.users

import androidx.paging.PagingData
import com.thesis.sportologia.model.users.entities.*
import com.thesis.sportologia.utils.Container
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    fun getAccount(): Flow<Container<AccountDataEntity>>

    fun reload()

    suspend fun getUser(currentUserId: String, userId: String): User

    suspend fun setIsSubscribed(followerId: String, followingId: String, isSubscribed: Boolean)

    suspend fun getPagedFollowers(userId: String): Flow<PagingData<UserSnippet>>

    suspend fun getPagedFollowings(userId: String): Flow<PagingData<UserSnippet>>

    suspend fun getPagedUsers(searchQuery: String, filter: FilterParamsUsers): Flow<PagingData<UserSnippet>>

    suspend fun signIn(email: String, password: String): String

    suspend fun signUp(userCreateDataEntity: UserCreateDataEntity): String

    suspend fun updateUser(userEditDataEntity: UserEditDataEntity)

    suspend fun checkEmailExists(email: String): Boolean

}