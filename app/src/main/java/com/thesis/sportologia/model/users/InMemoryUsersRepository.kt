package com.thesis.sportologia.model.users

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.settings.sources.SettingsDataSource
import com.thesis.sportologia.model.users.entities.*
import com.thesis.sportologia.model.users.sources.UsersDataSource
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.flows.LazyFlowSubjectFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUsersRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val usersDataSource: UsersDataSource,
    private val settingsDataSource: SettingsDataSource,
    coroutineScope: CoroutineScope,
    lazyFlowSubjectFactory: LazyFlowSubjectFactory,
) : UsersRepository {

    private val userLazyFlowSubject = lazyFlowSubjectFactory.create {
        usersDataSource.getAccount()
    }

    init {
        coroutineScope.launch {
            settingsDataSource.listenToken().collect {
                if (it != null) {
                    userLazyFlowSubject.newAsyncLoad(silently = true)
                } else {
                    userLazyFlowSubject.updateWith(Container.Error(AuthException()))
                }
            }
        }
    }

    override fun getAccount(): Flow<Container<AccountDataEntity>> {
        return userLazyFlowSubject.listen()
    }

    override suspend fun getUser(currentUserId: String, userId: String): User {
        return usersDataSource.getUser(currentUserId, userId)
    }

    override suspend fun setIsSubscribed(
        followerId: String,
        followingId: String,
        isSubscribed: Boolean
    ) {
        usersDataSource.setIsSubscribed(followerId, followingId, isSubscribed)
    }

    override suspend fun getPagedFollowers(userId: String): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { lastUser, pageIndex, pageSize ->
            usersDataSource.getPagedFollowers(userId, lastUser, pageSize)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UsersPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedFollowings(userId: String): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { lastUser, pageIndex, pageSize ->
            usersDataSource.getPagedFollowings(userId, lastUser, pageSize)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UsersPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedUsers(
        searchQuery: String,
        filter: FilterParamsUsers
    ): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { lastUser, pageIndex, pageSize ->
            usersDataSource.getPagedUsers(searchQuery, filter, lastUser, pageSize)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UsersPagingSource(loader) }
        ).flow
    }

    override suspend fun signIn(email: String, password: String): String {
        return usersDataSource.signIn(email, password)
    }

    override suspend fun signUp(userCreateDataEntity: UserCreateDataEntity): String {
        usersDataSource.signUp(userCreateDataEntity)
        return usersDataSource.signIn(userCreateDataEntity.email, userCreateDataEntity.password)
    }

    override suspend fun updateUser(userEditDataEntity: UserEditDataEntity) {
        usersDataSource.updateUser(userEditDataEntity)
    }

    override suspend fun checkEmailExists(email: String): Boolean {
        return usersDataSource.checkEmailExists(email)
    }

    override fun reload() {
        userLazyFlowSubject.newAsyncLoad()
    }

    companion object {
        const val PAGE_SIZE = 10
    }

}