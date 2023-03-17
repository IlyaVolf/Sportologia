package com.thesis.sportologia.model.users

import androidx.paging.PagingData
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.model.users.entities.UserSnippet
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    suspend fun getUser(userId: String): User?

    suspend fun setIsSubscribe(followerId: String, followingId: String, isSubscribed: Boolean)

    suspend fun getPagedFollowers(userId: String): Flow<PagingData<UserSnippet>>

    suspend fun getPagedFollowings(userId: String): Flow<PagingData<UserSnippet>>

    suspend fun getPagedUsers(filter: InMemoryUsersRepository.UsersFilter): Flow<PagingData<UserSnippet>>

    suspend fun getPagedAthletes(filter: InMemoryUsersRepository.UsersFilter): Flow<PagingData<UserSnippet>>

    suspend fun getPagedOrganizations(filter: InMemoryUsersRepository.UsersFilter): Flow<PagingData<UserSnippet>>

}