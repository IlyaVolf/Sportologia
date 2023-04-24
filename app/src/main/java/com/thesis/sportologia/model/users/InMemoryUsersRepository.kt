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

    val followersId = mutableListOf(
        "i_chiesov", "stroitel"
    )

    val followingsId = mutableListOf(
        "nikita"
    )

    val users = mutableListOf(
        Athlete(
            GenderType.MALE,
            null,
            "i_volf",
            "Илья Вольф",
            "Студент НГУ\nЛюблю лыжи, коньки, бег, велозаезды, фитнес и конфеты",
            "https://i.imgur.com/tGbaZCY.jpg",
            2,
            1,
            hashMapOf(
                Pair(Categories.MARTIAL_ARTS, true),
                Pair(Categories.RUNNING, true),
                Pair(Categories.MASTER_CLASS, false),
            ),
            false,
            23,
            listOf(
                "https://www.нотариат.рф/media/news/42/a4/42a4b9678f9443ff8cde96d59ae8d5ac.jpeg",
                "https://cdn.vashgorod.ru/r/1200x1200/img/90/ce/90ce47d83cdc047776e3e65ca9d4c171.jpg",
                "https://i.ytimg.com/vi/8x0LCuLTnQQ/maxresdefault.jpg",
                "https://cdn.vashgorod.ru/r/1200x1200/img/01/a4/01a4176fc724b441208c038af2c3317a.jpg",
            )
        ),
        Athlete(
            GenderType.MALE,
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
            listOf()
        ),
        Athlete(
            GenderType.MALE,
            Position(54.845109, 83.092407),
            "nikita",
            "Никита Романов",
            "-----",
            "https://catherineasquithgallery.com/uploads/posts/2021-03/1614558381_33-p-chelovechki-na-belom-fone-35.jpg",
            21,
            5,
            hashMapOf(
                Pair(Categories.MARTIAL_ARTS, false),
                Pair(Categories.RUNNING, false),
                Pair(Categories.MASTER_CLASS, false),
            ),
            true,
            1,
            listOf(
                "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
            )
        ),
        Organization(
            Position(55.072076, 82.965199),
            "stroitel",
            "Тренажёрный зал Строитель",
            "Построй тело своей мечты! Ежеднево с 8:00 до 22:00",
            null,
            2993,
            302,
            hashMapOf(
                Pair(Categories.MARTIAL_ARTS, true),
                Pair(Categories.RUNNING, true),
                Pair(Categories.MASTER_CLASS, true),
            ),
            false,
            0,
            listOf()
        ),

        )

    override suspend fun getUser(currentUserId: String, userId: String): User {
        val res: User
        try {
            res = usersDataSource.getUser(currentUserId, userId)
        } catch (e: Exception) {
            Log.d("abcdef", e.toString())
            throw Exception(e)
        }

        return res
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
            try {
                usersDataSource.getPagedFollowers(userId, lastUser, pageSize)
            } catch (e: Exception) {
                Log.d("abcdef", "$e")
            }
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
            try {
                usersDataSource.getPagedFollowings(userId, lastUser, pageSize)
            } catch (e: Exception) {
                Log.d("abcdef", "$e")
            }
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
            try {
                usersDataSource.getPagedUsers(searchQuery, filter, lastUser, pageSize)
            } catch (e: Exception) {
                Log.d("abcdef", e.toString())
                throw Exception(e)
            }
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

    override suspend fun signUp(signUpDataEntity: UserCreateEditDataEntity) {
        usersDataSource.signUp(signUpDataEntity)
    }

    override fun reload() {
        userLazyFlowSubject.newAsyncLoad()
    }

    companion object {
        const val PAGE_SIZE = 10
    }

}