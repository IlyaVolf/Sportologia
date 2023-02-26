package com.thesis.sportologia.ui


import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.adapters.PostsPagerAdapter
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.entities.PostListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class ListPostsViewModel @AssistedInject constructor(
    @Assisted private val mode: ListPostsMode,
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger), PostsPagerAdapter.Listener {

    private val search = MutableLiveData("")

    private val athTorgFLiveData = MutableLiveData<Boolean?>(null)
    var athTorgF: Boolean?
        get() = athTorgFLiveData.value
        set(value) {
            athTorgFLiveData.value = value
        }

    private val localChanges = LocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    private val _errorEvents = MutableLiveEvent<Int>()
    val errorEvents = _errorEvents.share()

    private val _scrollEvents = MutableLiveEvent<Unit>()
    val scrollEvents = _scrollEvents.share()

    private var _invalidateEvents = MutableLiveEvent<Unit>()
    val invalidateEvents = _invalidateEvents.share()

    val postsFlow: Flow<PagingData<PostListItem>>

    init {
        val originPostsFlow = when (mode) {
            ListPostsMode.PROFILE_OWN_PAGE -> {
                search.asFlow()
                    .flatMapLatest {
                        postsRepository.getPagedUserPosts(CurrentAccount().id)
                    }.cachedIn(viewModelScope)
            }
            ListPostsMode.HOME_PAGE -> {
                search.asFlow()
                    .flatMapLatest {
                        postsRepository.getPagedUserSubscribedOnPosts(CurrentAccount().id, athTorgF)
                    }.cachedIn(viewModelScope)
            }
        }

        postsFlow = combine(
            originPostsFlow,
            localChangesFlow.debounce(50),
            this::merge
        )
    }

    override fun onPostDelete(postListItem: PostListItem) {
        if (isInProgress(postListItem)) return

        viewModelScope.launch {
            try {
                setProgress(postListItem, true)
                delete(postListItem)
            } catch (e: Exception) {
                showError(R.string.error)
            } finally {
                setProgress(postListItem, false)
            }
        }
    }

    override fun onToggleLike(postListItem: PostListItem) {
        if (isInProgress(postListItem)) return

        viewModelScope.launch {
            try {
                setProgress(postListItem, true)
                setLike(postListItem)
            } catch (e: Exception) {
                showError(R.string.error)
            } finally {
                setProgress(postListItem, false)
            }
        }
    }

    override fun onToggleFavouriteFlag(postListItem: PostListItem) {
        if (isInProgress(postListItem)) return

        viewModelScope.launch {
            try {
                setProgress(postListItem, true)
                setFavoriteFlag(postListItem)
            } catch (e: Exception) {
                showError(R.string.error)
            } finally {
                setProgress(postListItem, false)
            }
        }
    }

    fun refresh() {
        this.search.postValue(this.search.value)
    }

    private suspend fun setLike(postListItem: PostListItem) {
        val newFlagValue = !postListItem.isLiked
        postsRepository.setIsLiked(CurrentAccount().id, postListItem.post, newFlagValue)
        localChanges.isLikedFlags[postListItem.id] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun setFavoriteFlag(postListItem: PostListItem) {
        val newFlagValue = !postListItem.isFavourite
        postsRepository.setIsFavourite(CurrentAccount().id, postListItem.post, newFlagValue)
        localChanges.isFavouriteFlags[postListItem.id] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun delete(postListItem: PostListItem) {
        postsRepository.deletePost(postListItem.id)
        invalidateList()
    }

    fun onPostCreated() {
        invalidateList()
    }

    private fun setProgress(postListItem: PostListItem, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(postListItem.id)
        } else {
            localChanges.idsInProgress.remove(postListItem.id)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(postListItem: PostListItem) =
        localChanges.idsInProgress.contains(postListItem.id)

    private fun showError(@StringRes errorMessage: Int) {
        _errorEvents.publishEvent(errorMessage)
    }

    private fun scrollListToTop() {
        _scrollEvents.publishEvent(Unit)
    }

    private fun invalidateList() {
        _invalidateEvents.publishEvent(Unit)
    }

    private fun merge(posts: PagingData<Post>, localChanges: OnChange<LocalChanges>): PagingData<PostListItem> {
        return posts
            .map { post ->
                val isInProgress = localChanges.value.idsInProgress.contains(post.id)
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[post.id]
                val localLikedFlag = localChanges.value.isLikedFlags[post.id]

                val userWithLocalChanges = post
                if (localFavoriteFlag != null) {
                    userWithLocalChanges.copy(isFavourite = localFavoriteFlag)
                }
                if (localLikedFlag != null) {
                    userWithLocalChanges.copy(isFavourite = localLikedFlag)
                }

                PostListItem(userWithLocalChanges, isInProgress)
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(mode: ListPostsMode): ListPostsViewModel
    }

    /**
     * Non-data class which allows passing the same reference to the
     * MutableStateFlow multiple times in a row.
     */
    class OnChange<T>(val value: T)

    /**
     * Contains:
     * 1) identifiers of items which are processed now (deleting or favorite
     * flag updating).
     * 2) local isLiked and isFavourite flag updates to avoid list reloading
     */
    class LocalChanges {
        val idsInProgress = mutableSetOf<Long>()
        val isLikedFlags = mutableMapOf<Long, Boolean>()
        val isFavouriteFlags = mutableMapOf<Long, Boolean>()
    }

}