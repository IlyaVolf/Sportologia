package com.thesis.sportologia.model.posts

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.model.posts.sources.PostsDataSource
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

    lateinit var postSample: PostDataEntity
    lateinit var posts: MutableList<PostDataEntity>
    lateinit var followersIds: List<String>

    init {
        postSample = PostDataEntity(
            id = java.util.UUID.randomUUID().toString(),
            authorId = "i_chiesov",
            authorName = "Игорь Чиёсов",
            profilePictureUrl = null,
            text = "Hello!",
            likesCount = 5,
            isAuthorAthlete = true,
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
                id = java.util.UUID.randomUUID().toString(),
                authorId = "stroitel",
                authorName = "Тренажёрный зал Строитель",
                profilePictureUrl = null,
                text = "Построй тело свой мечты!",
                likesCount = 0,
                isAuthorAthlete = false,
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
                id = java.util.UUID.randomUUID().toString(),
                authorId = "nikita",
                authorName = "Никита Романов",
                profilePictureUrl = "https://i.imgur.com/tGbaZCY.jpg",
                text = "Люблю спорт!",
                likesCount = 1,
                isAuthorAthlete = true,
                isLiked = true,
                isFavourite = true,
                postedDate = Calendar.getInstance().timeInMillis,
                photosUrls = mutableListOf()
            ),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(
                authorName = "Антон Игорев",
                authorId = "best_mate",
                id = java.util.UUID.randomUUID().toString(),
                text = "abcdefghiklmnopqrstvuxwyz"
            ),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),
            postSample.copy(id = java.util.UUID.randomUUID().toString()),

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
        userId: String
    ): List<PostDataEntity> =
        withContext(
            ioDispatcher
        ) {

            //posts.forEach { postsDataSource.createPost(it) }

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
        val loader: PostsPageLoader = { pageIndex, pageSize ->
            getUserPosts(pageIndex, pageSize, userId)
        }

        //delay(2000)

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
        athTorgF: Boolean?
    ): Flow<PagingData<PostDataEntity>> {
        val loader: PostsPageLoader = { pageIndex, pageSize ->
            getUserSubscribedOnPosts(pageIndex, pageSize, userId, athTorgF)
        }

        //delay(2000)

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
        athTorgF: Boolean?
    ): List<PostDataEntity> = withContext(
        ioDispatcher
    ) {
        val res = mutableListOf<PostDataEntity>()

        delay(1000)

        // TODO
        //throw Exception("a")

        val offset = pageIndex * pageSize

        followersIds.forEach { id ->
            if (athTorgF == null) {
                res.addAll(posts.filter { it.authorId == id })
            } else {
                res.addAll(posts.filter { it.authorId == id && it.isAuthorAthlete == athTorgF })
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

    override suspend fun getPagedUserFavouritePosts(athTorgF: Boolean?): Flow<PagingData<PostDataEntity>> {
        val loader: PostsPageLoader = { pageIndex, pageSize ->
            getUserFavouritePosts(pageIndex, pageSize, athTorgF)
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

    override suspend fun getPost(postId: String): PostDataEntity? = withContext(ioDispatcher) {
        delay(1000)

        return@withContext if (posts.none { it.id == postId }) null else posts.filter { it.id == postId }[0]
    }

    suspend fun getUserFavouritePosts(
        pageIndex: Int,
        pageSize: Int,
        athTorgF: Boolean?
    ): List<PostDataEntity> =
        withContext(ioDispatcher) {
            delay(1000)
            val offset = pageIndex * pageSize

            // временный и корявый метод! Ибо тут не учитыааются пользователи
            val filteredPosts = if (athTorgF != null) {
                posts.filter { it.isFavourite && it.isAuthorAthlete == athTorgF }
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
        delay(1000)
        postsDataSource.createPost(postDataEntity)
        posts.add(postDataEntity)

        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updatePost(postDataEntity: PostDataEntity) {
        delay(1000)

        posts.find { it.id == postDataEntity.id }.apply {
            this!!.text = postDataEntity.text
            this.photosUrls = postDataEntity.photosUrls
        }

    }

    override suspend fun deletePost(postId: String) {
        delay(1000)
        postsDataSource.deletePost(postId)
        posts.removeIf { it.id == postId }
        localChanges.remove(postId)
    }

    override suspend fun setIsLiked(
        userId: String,
        postDataEntity: PostDataEntity,
        isLiked: Boolean
    ) {
        withContext(ioDispatcher) {
            delay(1000)

            val postInList =
                posts.find { it.id == postDataEntity.id } ?: throw IllegalStateException()

            postInList.isLiked = isLiked

            if (isLiked) {
                postInList.likesCount++
            } else {
                postInList.likesCount--
            }
        }
    }

    override suspend fun setIsFavourite(
        userId: String,
        postDataEntity: PostDataEntity,
        isFavourite: Boolean
    ) =
        withContext(ioDispatcher) {
            delay(1000)

            // TODO
            //throw Exception("a")

            posts.find { it.id == postDataEntity.id }?.isFavourite = isFavourite
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