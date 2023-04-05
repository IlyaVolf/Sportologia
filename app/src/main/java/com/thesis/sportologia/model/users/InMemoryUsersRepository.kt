package com.thesis.sportologia.model.users

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.photos.entities.Photo
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.model.users.entities.*
import com.thesis.sportologia.ui.FilterFragmentUsers
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.Position
import com.thesis.sportologia.utils.containsAnyCase
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
            50,
            23,
            listOf(
                Photo(
                    0,
                    "https://kartinkin.net/pics/uploads/posts/2022-07/1658445549_58-kartinkin-net-p-shveitsarskie-alpi-priroda-krasivo-foto-63.jpg"
                ),
                Photo(
                    1,
                    "https://mykaleidoscope.ru/x/uploads/posts/2022-10/1666403296_34-mykaleidoscope-ru-p-alpi-tsugshpittse-pinterest-40.jpg"
                ),
                Photo(
                    2,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    3,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
            )
        ),
        Athlete(
            true,
            Position(54.848450, 83.043547),
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
            0,
            0,
            listOf()
        ),
        Athlete(
            true,
            Position(54.845109, 83.092407),
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
            50,
            1,
            listOf(Photo(4, "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"))
        ),
        Organization(
            Position(55.072076, 82.965199),
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
            50,
            0,
            listOf()
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

    override suspend fun getPagedUsers(
        searchQuery: String,
        filter: FilterParamsUsers
    ): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, searchQuery, filter)
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

    override suspend fun getPagedAthletes(
        searchQuery: String,
        filter: FilterParamsUsers
    ): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, searchQuery, filter)
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

    override suspend fun getPagedOrganizations(
        searchQuery: String,
        filter: FilterParamsUsers
    ): Flow<PagingData<UserSnippet>> {
        val loader: UserSnippetsPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, searchQuery, filter)
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
        searchQuery: String,
        filter: FilterParamsUsers,
    ): List<UserSnippet> = withContext(ioDispatcher) {
        delay(1000)

        Log.d("SEARCHUSER", "$searchQuery $filter")

        val offset = pageIndex * pageSize

        val usersFound = when (filter.usersType) {
            FilterParamsUsers.UsersType.ATHLETES ->
                users.filter { it is Athlete && containsAnyCase(it.name, searchQuery) }
            FilterParamsUsers.UsersType.ORGANIZATIONS ->
                users.filter { it is Organization && containsAnyCase(it.name, searchQuery) }
            FilterParamsUsers.UsersType.ALL -> users.filter {
                containsAnyCase(it.name, searchQuery)
            }
        }.sortedByDescending { it.innerRating }.map { it.toUserSnippet() }

        if (offset >= usersFound.size) {
            return@withContext listOf<UserSnippet>()
        } else if (offset + pageSize >= usersFound.size) {
            return@withContext usersFound.subList(offset, usersFound.size)
        } else {
            return@withContext usersFound.subList(offset, offset + pageSize)
        }
    }

    companion object {
        const val PAGE_SIZE = 12
    }

}