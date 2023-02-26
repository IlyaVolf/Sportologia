package com.thesis.sportologia.model.posts

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.posts.entities.Post
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryPostsRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
    PostsRepository {

    val postSample = Post(
        id = 0L,
        authorId = 1,
        authorName = "Игорь Чиёсов",
        profilePictureUrl = "https://i.imgur.com/tGbaZCY.jpg",
        text = "Hello!",
        likesCount = 5,
        isAuthorAthlete = true,
        isLiked = true,
        isFavourite = true,
        postedDate = Calendar.getInstance(),
        photosUrls = null
    )

    private val posts = mutableListOf(
        postSample,
        Post(
            id = 1L,
            authorId = 2,
            authorName = "Андрей Вайс",
            profilePictureUrl = null,
            text = "Игорь любит Андрея",
            likesCount = 0,
            isAuthorAthlete = false,
            isLiked = false,
            isFavourite = false,
            postedDate = Calendar.getInstance(),
            photosUrls = null
        ),
        Post(
            id = 2L,
            authorId = 5,
            authorName = "Никита Романов",
            profilePictureUrl = null,
            text = "Как же вы зодолбали",
            likesCount = 1,
            isAuthorAthlete = true,
            isLiked = true,
            isFavourite = true,
            postedDate = Calendar.getInstance(),
            photosUrls = null
        ),
        postSample.copy(id = 3L),
        postSample.copy(id = 4L),
        postSample.copy(id = 5L),
        postSample.copy(id = 6L),
        postSample.copy(id = 7L),
        postSample.copy(id = 8L),
        postSample.copy(id = 9L),
        postSample.copy(id = 10L),
        postSample.copy(id = 11L),
        postSample.copy(id = 12L),
        postSample.copy(authorName = "Антон Игорев", id = 13L, text = "abcdefghiklmnopqrstvuxwyz"),
        postSample.copy(id = 14L),
        postSample.copy(id = 15L),
        postSample.copy(id = 16L),
        postSample.copy(id = 17L),
        postSample.copy(id = 18L),
        postSample.copy(id = 19L),
        postSample.copy(id = 20L),
    )

    private val followersIds = mutableListOf(2, 3, 5)

    /* override suspend fun getUserPosts(userId: Int): List<Post> {
        delay(1000)
        return posts.filter { it.authorId == userId }
    } */

    suspend fun getUserPosts2(pageIndex: Int, pageSize: Int, userId: Int): List<Post> =
        withContext(
            ioDispatcher
        ) {
            delay(1000)
            val offset = pageIndex * pageSize

            val filteredPosts = posts.filter { it.authorId == userId }.reversed()

            // TODO SORT BY DATE

            // TODO
            //throw Exception("a")

            if (offset >= filteredPosts.size) {
                return@withContext listOf<Post>()
            } else if (offset + pageSize >= filteredPosts.size) {
                return@withContext filteredPosts.subList(offset, filteredPosts.size)
            } else {
                return@withContext filteredPosts.subList(offset, offset + pageSize)
            }
        }

    override suspend fun getPagedUserPosts(userId: Int): Flow<PagingData<Post>> {
        val loader: PostsPageLoader = { pageIndex, pageSize ->
            getUserPosts2(pageIndex, pageSize, userId)
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
        userId: Int,
        athTorgF: Boolean?
    ): Flow<PagingData<Post>> {
        val loader: PostsPageLoader = { pageIndex, pageSize ->
            getUserSubscribedOnPosts2(pageIndex, pageSize, userId, athTorgF)
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

    suspend fun getUserSubscribedOnPosts2(
        pageIndex: Int,
        pageSize: Int,
        userId: Int,
        athTorgF: Boolean?
    ): List<Post> = withContext(
        ioDispatcher
    ) {
        val res = mutableListOf<Post>()

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

        // TODO МЕТОД ФИГНЯ

        // TODO SORT BY DATE

        if (offset >= res.size) {
            return@withContext listOf<Post>()
        } else if (offset + pageSize >= res.size) {
            return@withContext res.subList(offset, res.size)
        } else {
            return@withContext res.subList(offset, offset + pageSize)
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

    override suspend fun getUserFavouritePosts(userId: Int): List<Post> {
        delay(1000)
        return posts.filter { it.authorId == userId && it.isFavourite }
    }

    override suspend fun createPost(post: Post) {
        delay(1000)
        posts.add(post)

        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updatePost(post: Post) {
        delay(1000)
        deletePost(post.id)
        createPost(post)
    }

    override suspend fun deletePost(postId: Long) {
        delay(1000)
        Log.d("BUGFIX", "before ${posts.size} $postId")
        posts.removeIf { it.id == postId }
        Log.d("BUGFIX", "after ${posts.size}")
    }

    override suspend fun setIsLiked(userId: Int, post: Post, isLiked: Boolean) {
        withContext(ioDispatcher) {
            delay(1000)

            val postInList = posts.find { it.id == post.id } ?: throw IllegalStateException()

            postInList.isLiked = isLiked

            if (isLiked) {
                postInList.likesCount++
            } else {
                postInList.likesCount--
            }
        }
    }

    override suspend fun setIsFavourite(userId: Int, post: Post, isFavourite: Boolean) =
        withContext(ioDispatcher) {
            delay(1000)

            // TODO
            //throw Exception("a")

            posts.find { it.id == post.id }?.isFavourite = isFavourite
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

    private companion object {
        const val PAGE_SIZE = 6
    }
}