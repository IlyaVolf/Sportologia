package com.thesis.sportologia.model.posts

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.model.posts.sources.PostsDataSource
import com.thesis.sportologia.model.users.entities.UserType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryPostsRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val postsDataSource: PostsDataSource
) : PostsRepository {

    override val localChanges = PostsLocalChanges()
    override val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    var postSample: PostDataEntity
    var posts: MutableList<PostDataEntity>
    var followersIds: List<String>

    init {
        postSample = PostDataEntity(
            id = UUID.randomUUID().toString(),
            authorId = "i_chiesov",
            authorName = "Игорь Чиёсов",
            profilePictureUrl = null,
            text = "Hello!",
            likesCount = 5,
            userType = UserType.ATHLETE,
            isLiked = true,
            isFavourite = true,
            postedDate = Calendar.getInstance().timeInMillis,
            photosUrls = mutableListOf(
                "https://cdn.5280.com/2014/03/ss_skis.jpg"
            )
        )

        posts = mutableListOf(
            postSample,
            PostDataEntity(
                id = UUID.randomUUID().toString(),
                authorId = "stroitel",
                authorName = "Тренажёрный зал Строитель",
                profilePictureUrl = null,
                text = "Построй тело свой мечты!",
                likesCount = 0,
                userType = UserType.ORGANIZATION,
                isLiked = false,
                isFavourite = false,
                postedDate = Calendar.getInstance().timeInMillis,
                photosUrls = mutableListOf(
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                    "https://put-sily.ru/wp-content/uploads/3/c/2/3c2b97534a2a46911071431e4e519750.jpeg",
                )
            ),
            PostDataEntity(
                id = UUID.randomUUID().toString(),
                authorId = "nikita",
                authorName = "Никита Романов",
                profilePictureUrl = "https://i.imgur.com/tGbaZCY.jpg",
                text = "Люблю спорт!",
                likesCount = 1,
                userType = UserType.ATHLETE,
                isLiked = true,
                isFavourite = true,
                postedDate = Calendar.getInstance().timeInMillis,
                photosUrls = mutableListOf()
            ),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(
                authorName = "Антон Игорев",
                authorId = "best_mate",
                id = UUID.randomUUID().toString(),
                text = "abcdefghiklmnopqrstvuxwyz"
            ),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),
            postSample.copy(id = UUID.randomUUID().toString()),

            )

        followersIds = mutableListOf("i_chiesov", "stroitel", "nikita")
    }


    /* override suspend fun getUserPosts(userId: Int): List<Post> {
        delay(1000)
        return posts.filter { it.authorId == userId }
    } */


    private suspend fun getUserPosts(
        pageIndex: Int,
        pageSize: Int,
        userId: String,
        lastPostId: String?
    ): List<PostDataEntity> =
        withContext(
            ioDispatcher
        ) {

            //posts.forEach { postsDataSource.createPost(it) }

            //return@withContext postsDataSource.getPagedUserPosts(userId, lastPostId, pageSize)

            delay(1000)
            val offset = pageIndex * pageSize

            val filteredPosts =
                posts.filter { it.authorId == userId }.sortedByDescending { it.postedDate }

            // filteredPosts.sortedByDescending { it.postedDate }

            // TODO SORT BY DATE

            // TODO
            // throw Exception("a")

            if (offset >= filteredPosts.size) {
                return@withContext listOf<PostDataEntity>()
            } else if (offset + pageSize >= filteredPosts.size) {
                return@withContext filteredPosts.subList(offset, filteredPosts.size)
            } else {
                return@withContext filteredPosts.subList(offset, offset + pageSize)
            }
        }

    override suspend fun getPagedUserPosts(userId: String): Flow<PagingData<PostDataEntity>> {
        var lastMarker: Long? = null

        val loader: PostsPageLoader = { pageIndex, pageSize ->
            val cash = postsDataSource.getPagedUserPosts(userId, lastMarker, pageSize)
            lastMarker = cash.lastOrNull()?.postedDate
            cash
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedUserSubscribedOnPosts(
        userId: String,
        userType: UserType?
    ): Flow<PagingData<PostDataEntity>> {
        var lastId: String? = null

        var lastMarker: Long? = null

        val loader: PostsPageLoader = { pageIndex, pageSize ->
            val cash = postsDataSource.getPagedUserSubscribedOnPosts(userId, lastMarker, pageSize)
            lastMarker = cash.lastOrNull()?.postedDate
            cash
        }


        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostsPagingSource(loader) }
        ).flow
    }

    private suspend fun getUserSubscribedOnPosts(
        pageIndex: Int,
        pageSize: Int,
        userId: String,
        userType: UserType?,
    ): List<PostDataEntity> = withContext(
        ioDispatcher
    ) {
        val res = mutableListOf<PostDataEntity>()

        delay(1000)

        // TODO
        //throw Exception("a")

        val offset = pageIndex * pageSize

        followersIds.forEach { id ->
            if (userType == null) {
                res.addAll(posts.filter { it.authorId == id })
            } else {
                res.addAll(posts.filter { it.authorId == id && it.userType == userType })
            }
        }

        res.sortedByDescending { it.postedDate }

        // TODO МЕТОД ФИГНЯ

        // TODO SORT BY DATE

        if (offset >= res.size) {
            return@withContext listOf<PostDataEntity>()
        } else if (offset + pageSize >= res.size) {
            return@withContext res.subList(offset, res.size)
        } else {
            return@withContext res.subList(offset, offset + pageSize)
        }
    }

    override suspend fun getPagedUserFavouritePosts(userType: UserType?): Flow<PagingData<PostDataEntity>> {
        val loader: PostsPageLoader = { pageIndex, pageSize ->
            getUserFavouritePosts(pageIndex, pageSize, userType)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPost(postId: String, userId: String): PostDataEntity? =
        withContext(ioDispatcher) {
            return@withContext postsDataSource.getPost(postId, userId)
        }

    suspend fun getUserFavouritePosts(
        pageIndex: Int,
        pageSize: Int,
        userType: UserType?
    ): List<PostDataEntity> =
        withContext(ioDispatcher) {
            delay(1000)
            val offset = pageIndex * pageSize

            // временный и корявый метод! Ибо тут не учитыааются пользователи
            val filteredPosts = if (userType != null) {
                posts.filter { it.isFavourite && it.userType == userType }
                    .sortedByDescending { it.postedDate }
            } else {
                posts.filter { it.isFavourite }.sortedByDescending { it.postedDate }
            }
            // TODO SORT BY DATE

            // TODO
            //throw Exception("a")

            if (offset >= filteredPosts.size) {
                return@withContext listOf<PostDataEntity>()
            } else if (offset + pageSize >= filteredPosts.size) {
                return@withContext filteredPosts.subList(offset, filteredPosts.size)
            } else {
                return@withContext filteredPosts.subList(offset, offset + pageSize)
            }
        }

    /*override suspend fun getUserSubscribedOnPosts(userId: Int, athTorgF: Boolean?): List<Post> {
        delay(1000)
        val res = mutableListOf<Post>()

        followersIds.forEach { id ->
            if (athTorgF == null) {
                res.addAll(posts.filter { it.authorId == id })
            } else {
                res.addAll(posts.filter { it.authorId == id && it.isAuthorAthlete == athTorgF })
            }
        }

        return res
    }*/

    override suspend fun createPost(postDataEntity: PostDataEntity) {
        postsDataSource.createPost(postDataEntity)
        //posts.add(postDataEntity)

        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updatePost(postDataEntity: PostDataEntity) {
        //delay(1000)

        postsDataSource.updatePost(postDataEntity)

        /*posts.find { it.id == postDataEntity.id }.apply {
            this!!.text = postDataEntity.text
            this.photosUrls = postDataEntity.photosUrls
        }*/

    }

    override suspend fun deletePost(postId: String) {
        //delay(1000)
        postsDataSource.deletePost(postId)
        posts.removeIf { it.id == postId }
        localChanges.remove(postId)
    }

    override suspend fun setIsLiked(
        userId: String,
        postDataEntity: PostDataEntity,
        isLiked: Boolean
    ) = withContext(ioDispatcher) {
        postsDataSource.setIsLiked(userId, postDataEntity, isLiked)
        /*delay(1000)

        val postInList =
            posts.find { it.id == postDataEntity.id } ?: throw IllegalStateException()

        postInList.isLiked = isLiked

        if (isLiked) {
            postInList.likesCount++
        } else {
            postInList.likesCount--
        }*/
    }

    override suspend fun setIsFavourite(
        userId: String,
        postDataEntity: PostDataEntity,
        isFavourite: Boolean
    ) = withContext(ioDispatcher) {
        postsDataSource.setIsFavourite(userId, postDataEntity, isFavourite)
        /*delay(1000)

        // TODO
        //throw Exception("a")

        posts.find { it.id == postDataEntity.id }?.isFavourite = isFavourite*/
    }

/*override suspend fun likePost(userId: Int, post: Post) {
    delay(1000)
    updatePost(post.copy(isLiked = true, likesCount = post.likesCount + 1))
}

override suspend fun unlikePost(userId: Int, post: Post) {
    delay(1000)
    updatePost(post.copy(isLiked = false, likesCount = post.likesCount - 1))
}

override suspend fun addPostToFavourites(userId: Int, post: Post) {
    delay(1000)
    updatePost(post.copy(isAddedToFavourites = true))
}

override suspend fun removePostFromFavourites(userId: Int, post: Post) {
    delay(1000)
    updatePost(post.copy(isAddedToFavourites = false))
}*/

    // TODO увеличение числа PAGE_SIZE фиксит баг с отсуствием прокрутки (футер не вылезает) списка после обновления
    private companion object {
        const val PAGE_SIZE = 8
    }
}