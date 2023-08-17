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
import kotlinx.coroutines.withTimeout
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

    override suspend fun getPagedUserPosts(userId: String): Flow<PagingData<PostDataEntity>> {
        val loader: PostsPageLoader = { lastTimestamp, _, pageSize ->
            postsDataSource.getPagedUserPosts(userId, lastTimestamp, pageSize)
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
        val loader: PostsPageLoader = { lastTimestamp, pageIndex, pageSize ->
            postsDataSource.getPagedUserSubscribedOnPosts(userId, userType, lastTimestamp, pageSize)
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

    override suspend fun getPagedUserFavouritePosts(
        userId: String,
        userType: UserType?
    ): Flow<PagingData<PostDataEntity>> {
        val loader: PostsPageLoader = { lastTimestamp, pageIndex, pageSize ->
            postsDataSource.getPagedUserFavouritePosts(userId, userType, lastTimestamp, pageSize)
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

    override suspend fun getPost(postId: String, userId: String): PostDataEntity {
        return postsDataSource.getPost(postId, userId)
    }

    override suspend fun createPost(postDataEntity: PostDataEntity) {
        postsDataSource.createPost(postDataEntity)
    }

    override suspend fun updatePost(postDataEntity: PostDataEntity) {
        postsDataSource.updatePost(postDataEntity)
    }

    override suspend fun deletePost(postId: String) {
        postsDataSource.deletePost(postId)
        localChanges.remove(postId)
    }

    override suspend fun setIsLiked(
        userId: String,
        postDataEntity: PostDataEntity,
        isLiked: Boolean
    ) = withContext(ioDispatcher) {
        withTimeout(AWAITING_TIME) {
            postsDataSource.setIsLiked(userId, postDataEntity, isLiked)
        }
    }

    override suspend fun setIsFavourite(
        userId: String,
        postDataEntity: PostDataEntity,
        isFavourite: Boolean
    ) = withContext(ioDispatcher) {
        withTimeout(AWAITING_TIME) {
            postsDataSource.setIsFavourite(userId, postDataEntity, isFavourite)
        }
    }

    // TODO увеличение числа PAGE_SIZE фиксит баг с отсуствием прокрутки (футер не вылезает) списка после обновления
    private companion object {
        const val PAGE_SIZE = 8
        const val AWAITING_TIME = 5000L
    }
}