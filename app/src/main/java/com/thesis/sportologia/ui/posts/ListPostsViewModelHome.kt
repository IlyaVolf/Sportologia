package com.thesis.sportologia.ui.posts


import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class ListPostsViewModelHome @AssistedInject constructor(
    @Assisted private val userId: String,
    private val postsRepository: PostsRepository,
    logger: Logger
) : ListPostsViewModel(userId, postsRepository, logger) {

    override fun getDataFlow(): Flow<PagingData<PostDataEntity>> {
        return searchLive.asFlow()
            .flatMapLatest {
                postsRepository.getPagedUserSubscribedOnPosts(userId, athTorgF)
            }.cachedIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ListPostsViewModelHome
    }

}