package com.thesis.sportologia.data.posts.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.core.entities.AuthException
import com.thesis.sportologia.core.entities.OnChange
import com.thesis.sportologia.core.entities.UserType
import com.thesis.sportologia.data.PostsDataRepository
import com.thesis.sportologia.data.accounts.sources.AccountsDataSource
import com.thesis.sportologia.data.posts.entities.CreatePostDataEntity
import com.thesis.sportologia.data.posts.entities.PostDataEntity
import com.thesis.sportologia.data.posts.sources.PostsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

class PostsDataRepositoryImpl @Inject constructor(
    private val postsDataSource: PostsDataSource,
    private val usersDataSource: UsersDataSource,
    private val accountsDataSource: AccountsDataSource,
) : PostsDataRepository {

    private val localChanges = PostsLocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    // TODO не забыть про слияние

    override suspend fun getPagedUserPosts(userId: Long): Flow<PagingData<PostDataEntity>> {
        val loader: PostsPageLoader = { lastTimestamp, _, pageSize ->
            postsDataSource.getPagedUserPosts(userId, lastTimestamp, pageSize)
        }

        return Pager(
            config = PAGING_CONFING,
            pagingSourceFactory = { PostsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedSubscribedOnPosts(
        userType: UserType?
    ): Flow<PagingData<PostDataEntity>> {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()

        val loader: PostsPageLoader = { lastTimestamp, pageIndex, pageSize ->
            postsDataSource.getPagedSubscribedOnPosts(
                currentUserId,
                userType,
                lastTimestamp,
                pageSize
            )
        }

        return Pager(
            config = PAGING_CONFING,
            pagingSourceFactory = { PostsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPagedFavouritePosts(
        userType: UserType?
    ): Flow<PagingData<PostDataEntity>> {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()

        val loader: PostsPageLoader = { lastTimestamp, pageIndex, pageSize ->
            postsDataSource.getPagedFavouritePosts(currentUserId, userType, lastTimestamp, pageSize)
        }

        return Pager(
            config = PAGING_CONFING,
            pagingSourceFactory = { PostsPagingSource(loader) }
        ).flow
    }

    override suspend fun getPost(postId: Long, userId: Long): PostDataEntity {
        return postsDataSource.getPost(postId, userId)
    }

    override suspend fun createPost(createPostDataEntity: CreatePostDataEntity) {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()
        val user = usersDataSource.getAccount(currentUserId)

        postsDataSource.createPost(user, createPostDataEntity)
    }

    override suspend fun updatePost(postDataEntity: PostDataEntity) {
        postsDataSource.updatePost(postDataEntity)
    }

    override suspend fun deletePost(postId: Long) {
        postsDataSource.deletePost(postId)
        localChanges.remove(postId)
    }

    override suspend fun setIsLiked(
        postId: Long,
        isLiked: Boolean
    ) {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()

        postsDataSource.setIsLiked(currentUserId, postId, isLiked)
    }

    override suspend fun setIsFavourite(
        postId: Long,
        isFavourite: Boolean
    ) {
        val currentUserId: Long = accountsDataSource.getCurrentUserId() ?: throw AuthException()

        postsDataSource.setIsFavourite(currentUserId, postId, isFavourite)
    }

    private suspend fun combinePostsFlows(originPostsFlow: Flow<PagingData<PostDataEntity>>):
            Flow<PagingData<PostDataEntity>> {
        originPostsFlow.collect {
            it.map { post ->
                localChanges.isLikedFlags[post.id] = post.isLiked
                localChanges.isFavouriteFlags[post.id] = post.isFavourite
                localChanges.likesCount[post.id] = post.likesCount
            }
        }

        return combine(
            originPostsFlow,
            localChangesFlow.debounce(50),
            ::merge
        )
    }

    private fun merge(
        posts: PagingData<PostDataEntity>,
        localChanges: OnChange<PostsLocalChanges>
    ): PagingData<PostDataEntity> {
        return posts
            .map { post ->
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[post.id]
                val localLikedFlag = localChanges.value.isLikedFlags[post.id]
                val localLikesCountFlag = localChanges.value.likesCount[post.id]

                var postWithLocalChanges = post.copy()
                if (localFavoriteFlag != null) {
                    postWithLocalChanges =
                        postWithLocalChanges.copy(isFavourite = localFavoriteFlag)
                }
                if (localLikedFlag != null) {
                    postWithLocalChanges = postWithLocalChanges.copy(isLiked = localLikedFlag)
                }
                if (localLikesCountFlag != null) {
                    postWithLocalChanges =
                        postWithLocalChanges.copy(likesCount = localLikesCountFlag)
                }

                postWithLocalChanges
            }
    }

    private companion object {
        const val PAGE_SIZE = 8

        val PAGING_CONFING = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = PAGE_SIZE / 2,
            enablePlaceholders = false
        )
    }
}