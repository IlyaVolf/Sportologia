package com.thesis.sportologia.model.users

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.model.users.entities.Athlete
import com.thesis.sportologia.model.users.entities.Organization
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.model.users.entities.UserSnippet
import com.thesis.sportologia.utils.Categories
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUsersRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UsersRepository {

    val followersId = mutableListOf(
        "i_chiesov", "stroitel"
    )

    val followingsId = mutableListOf(
        "nikita"
    )

    val users = mutableListOf(
        Athlete(
            true,
            null,
            "i_volf",
            "Илья Вольф",
            "-----",
            "https://i.imgur.com/tGbaZCY.jpg",
            2,
            1,
            hashMapOf(
                Pair(Categories.MARTIAL_ARTS, true),
                Pair(Categories.RUNNING, true),
                Pair(Categories.MASTER_CLASS, false),
            ),
            false,
        ),
        Athlete(
            true,
            null,
            "i_chiesov",
            "Игорь Чиёсов",
            "-----",
            null,
            1,
            0,
            hashMapOf(
                Pair(Categories.MARTIAL_ARTS, false),
                Pair(Categories.RUNNING, true),
                Pair(Categories.MASTER_CLASS, false),
            ),
            true,
        ),
        Athlete(
            true,
            null,
            "nikita",
            "Никита Романов",
            "-----",
            "https://i.imgur.com/tGbaZCY.jpg",
            21,
            5,
            hashMapOf(
                Pair(Categories.MARTIAL_ARTS, false),
                Pair(Categories.RUNNING, false),
                Pair(Categories.MASTER_CLASS, false),
            ),
            true,
        ),
        Organization(
            null,
            "stroitel",
            "Тренажёрный зал Строитель",
            "=====",
            null,
            2993,
            302,
            hashMapOf(
                Pair(Categories.MARTIAL_ARTS, true),
                Pair(Categories.RUNNING, true),
                Pair(Categories.MASTER_CLASS, true),
            ),
            false,
        ),

        )

    override suspend fun getUser(userId: String): User? {
        delay(1000)

        Log.d("BUGFIX", "got user")

        // throw Exception()

        return if (users.none { it.id == userId }) return null else users.filter { it.id == userId }[0]
    }

    override suspend fun setIsSubscribe(
        followerId: String,
        followingId: String,
        isSubscribed: Boolean
    ) {
        delay(1000)

        //throw Exception()

        Log.d("BUGFIX", "$followerId $followingId ${users.filter { it.id == followingId }}")

        val follower = users.filter { it.id == followerId }
        val following = users.filter { it.id == followingId }
        if (isSubscribed) {
            follower.forEach { it.followingsCount++ }
            following.forEach { it.followersCount++ }
        } else {
            follower.forEach { it.followingsCount-- }
            following.forEach { it.followersCount-- }
        }
        following.forEach { it.isSubscribed = isSubscribed }

        if (isSubscribed) {
            followingsId.add(followingId)
        } else {
            followingsId.remove(followingId)
        }

    }

    override suspend fun getPagedFollowers(userId: String): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getFollowers(pageIndex, pageSize, userId)
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

    private suspend fun getFollowers(
        pageIndex: Int,
        pageSize: Int,
        userId: String
    ): List<UserSnippet> = withContext(ioDispatcher) {
        delay(1000)

        val offset = pageIndex * pageSize

        val followers = mutableListOf<UserSnippet>()
        followersId.forEach { followerId ->
            users.filter { it.id == followerId }.map { it.toUserSnippet() }
                .forEach { followers.add(it) }
        }
        if (offset >= followers.size) {
            return@withContext listOf<UserSnippet>()
        } else if (offset + pageSize >= followers.size) {
            return@withContext followers.subList(offset, followers.size)
        } else {
            return@withContext followers.subList(offset, offset + pageSize)
        }
    }

    override suspend fun getPagedFollowings(userId: String): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getFollowings(pageIndex, pageSize, userId)
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

    private suspend fun getFollowings(
        pageIndex: Int,
        pageSize: Int,
        userId: String
    ): List<UserSnippet> = withContext(ioDispatcher) {
        delay(1000)

        val offset = pageIndex * pageSize

        val followings = mutableListOf<UserSnippet>()
        followingsId.forEach { followingId ->
            users.filter { it.id == followingId }.map { it.toUserSnippet() }
                .forEach { followings.add(it) }
        }
        if (offset >= followings.size) {
            return@withContext listOf<UserSnippet>()
        } else if (offset + pageSize >= followings.size) {
            return@withContext followings.subList(offset, followings.size)
        } else {
            return@withContext followings.subList(offset, offset + pageSize)
        }
    }

    override suspend fun getPagedUsers(filter: UsersFilter): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, filter)
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

    override suspend fun getPagedAthletes(filter: UsersFilter): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, filter)
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

    override suspend fun getPagedOrganizations(filter: UsersFilter): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, filter)
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

    private suspend fun getUsers(
        pageIndex: Int,
        pageSize: Int,
        filter: UsersFilter,
    ): List<UserSnippet> = withContext(ioDispatcher) {
        delay(1000)

        //Log.d("SEARCHUSER", filter.userName)

        val offset = pageIndex * pageSize

        val usersFound = when (filter.userType) {
            UserTypes.ATHLETES ->
                users.filter { it is Athlete && containsName(it.name, filter.userName) }
            UserTypes.ORGANIZATIONS ->
                users.filter { it is Organization && containsName(it.name, filter.userName) }
            UserTypes.ALL -> users.filter { containsName(it.name, filter.userName) }
        }.map { it.toUserSnippet() }

        if (offset >= usersFound.size) {
            return@withContext listOf<UserSnippet>()
        } else if (offset + pageSize >= usersFound.size) {
            return@withContext usersFound.subList(offset, usersFound.size)
        } else {
            return@withContext usersFound.subList(offset, offset + pageSize)
        }
    }

    private fun containsName(userName: String, searchQuery: String): Boolean {
        return userName.lowercase() == searchQuery.lowercase()
    }

    data class UsersFilter(
        val userName: String,
        val userType: UserTypes,
    )

    enum class UserTypes {
        ATHLETES, ORGANIZATIONS, ALL
    }

    companion object {
        const val PAGE_SIZE = 12
    }

}