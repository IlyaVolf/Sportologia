package com.thesis.sportologia.model.users

import androidx.paging.PagingData
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.model.users.entities.UserSnippet
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    suspend fun getUser(userId: String): User?

    suspend fun setIsSubscribe(followerId: String, followingId: String, isSubscribed: Boolean)

    suspend fun getPagedFollowers(userId: String): Flow<PagingData<UserSnippet>>

    suspend fun getPagedFollowings(userId: String): Flow<PagingData<UserSnippet>>

    suspend fun getPagedUsers(searchQuery: String, filter: FilterParamsUsers): Flow<PagingData<UserSnippet>>

    suspend fun getPagedAthletes(searchQuery: String, filter: FilterParamsUsers): Flow<PagingData<UserSnippet>>

    suspend fun getPagedOrganizations(searchQuery: String, filter: FilterParamsUsers): Flow<PagingData<UserSnippet>>

}