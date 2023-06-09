package com.thesis.sportologia.ui.posts


import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.posts.PostsLocalChanges
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapter
import com.thesis.sportologia.ui.posts.adapters.PostsPagerAdapter
import com.thesis.sportologia.ui.posts.entities.PostListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class ListPostsViewModel constructor(
    private val userId: String,
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger), PostsPagerAdapter.MoreButtonListener, PostsHeaderAdapter.FilterListener {

    protected val searchLive = MutableLiveData("")

    private val _athTorgFLiveData = MutableLiveData<Boolean?>(null)
    val athTorgFLiveData = _athTorgFLiveData.share()

    private val localChanges = postsRepository.localChanges
    private val localChangesFlow = postsRepository.localChangesFlow

    private val _errorEvents = MutableLiveEvent<Int>()
    val errorEvents = _errorEvents.share()

    private val _scrollEvents = MutableLiveEvent<Unit>()
    val scrollEvents = _scrollEvents.share()

    private var _invalidateEvents = MutableLiveEvent<Unit>()
    val invalidateEvents = _invalidateEvents.share()

    val postsFlow: Flow<PagingData<PostListItem>>

    init {
        val originPostsFlow = this.getDataFlow()

        postsFlow = combine(
            originPostsFlow,
            localChangesFlow.debounce(50),
            ::merge
        )
    }

    abstract fun getDataFlow(): Flow<PagingData<PostDataEntity>>

    override fun onPostDelete(postListItem: PostListItem) {
        if (isInProgress(postListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(postListItem.id, true)
                delete(postListItem)
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(postListItem.id, false)
            }
        }
    }

    override fun onToggleLike(postListItem: PostListItem) {
        if (isInProgress(postListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(postListItem.id, true)
                setLike(postListItem)
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(postListItem.id, false)
            }
        }
    }

    override fun onToggleFavouriteFlag(postListItem: PostListItem) {
        if (isInProgress(postListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(postListItem.id, true)
                setFavoriteFlag(postListItem)
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(postListItem.id, false)
            }
        }
    }

    override fun filterApply(athTorgF: Boolean?) {
        if (_athTorgFLiveData.value == athTorgF) return

        _athTorgFLiveData.value = athTorgF
        refresh()
    }

    fun refresh() {
        this.searchLive.value = this.searchLive.value
    }

    fun onPostCreated() {
        invalidateList()
    }

    fun onPostEdited() {
        invalidateList()
    }

    private suspend fun setLike(postListItem: PostListItem) {
        try {
            val newFlagValue = !postListItem.isLiked
            postsRepository.setIsLiked(CurrentAccount().id, postListItem.postDataEntity, newFlagValue)
            localChanges.isLikedFlags[postListItem.id] = newFlagValue
            localChanges.likesCount[postListItem.id] =
                (localChanges.likesCount[postListItem.id]
                    ?: postListItem.likesCount) + (if (newFlagValue) 1 else -1)
            localChangesFlow.value = OnChange(localChanges)
        } catch (e: Exception) {
            showError(R.string.error_loading_title)
        }
    }

    private suspend fun setFavoriteFlag(postListItem: PostListItem) {
        try {
            val newFlagValue = !postListItem.isFavourite
            postsRepository.setIsFavourite(CurrentAccount().id, postListItem.postDataEntity, newFlagValue)
            localChanges.isFavouriteFlags[postListItem.id] = newFlagValue
            localChangesFlow.value = OnChange(localChanges)
        } catch (e: Exception) {
            showError(R.string.error_loading_title)
        }
    }

    private suspend fun delete(postListItem: PostListItem) {
        postsRepository.deletePost(postListItem.id)
        invalidateList()
    }

    private fun setProgress(postListItemId: String, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(postListItemId)
        } else {
            localChanges.idsInProgress.remove(postListItemId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(postListItemId: String) =
        localChanges.idsInProgress.contains(postListItemId)

    private fun showError(@StringRes errorMessage: Int) {
        _errorEvents.publishEvent(errorMessage)
    }

    private fun scrollListToTop() {
        _scrollEvents.publishEvent(Unit)
    }

    private fun invalidateList() {
        _invalidateEvents.publishEvent(Unit)
    }

    private fun merge(
        posts: PagingData<PostDataEntity>,
        localChanges: OnChange<PostsLocalChanges>
    ): PagingData<PostListItem> {
        return posts
            .map { post ->
                val isInProgress = localChanges.value.idsInProgress.contains(post.id)
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

                PostListItem(postWithLocalChanges, isInProgress)
            }
    }

}