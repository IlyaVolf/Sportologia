package com.thesis.sportologia.ui.posts


import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.R
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.posts.entities.Post
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

    // LiveData для Event (т.е. действий)

    internal val search = MutableLiveData("")

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
        val originPostsFlow = getDataFlow()

        postsFlow = combine(
            originPostsFlow,
            localChangesFlow.debounce(50),
            this::merge
        )
    }

    abstract fun getDataFlow(): Flow<PagingData<Post>>

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
        this.athTorgF = athTorgF
        refresh()
    }

    fun refresh() {
        this.search.postValue(this.search.value)
    }

    fun onPostCreated() {
        invalidateList()
    }

    fun onPostEdited() {
        invalidateList()
    }

    private suspend fun setLike(postListItem: PostListItem) {
        val newFlagValue = !postListItem.isLiked
        postsRepository.setIsLiked(userId, postListItem.post, newFlagValue)
        localChanges.isLikedFlags[postListItem.id] = newFlagValue
        //localChanges.isTextFlags[postListItem.id] = postListItem.text + "asgagasagag"
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun setFavoriteFlag(postListItem: PostListItem) {
        val newFlagValue = !postListItem.isFavourite
        postsRepository.setIsFavourite(userId, postListItem.post, newFlagValue)
        localChanges.isFavouriteFlags[postListItem.id] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun delete(postListItem: PostListItem) {
        postsRepository.deletePost(postListItem.id)
        invalidateList()
    }

    private fun setProgress(postListItemId: Long, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(postListItemId)
        } else {
            localChanges.idsInProgress.remove(postListItemId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(postListItemId: Long) =
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
        posts: PagingData<Post>,
        localChanges: OnChange<LocalChanges>
    ): PagingData<PostListItem> {
        return posts
            .map { post ->
                val isInProgress = localChanges.value.idsInProgress.contains(post.id)
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[post.id]
                val localLikedFlag = localChanges.value.isLikedFlags[post.id]
                val localTextFlag = localChanges.value.isTextFlags[post.id]

                val postWithLocalChanges = post
                if (localFavoriteFlag != null) {
                    postWithLocalChanges.copy(isFavourite = localFavoriteFlag)
                }
                if (localLikedFlag != null) {
                    postWithLocalChanges.copy(isFavourite = localLikedFlag)
                }
                if (localTextFlag != null) {
                    postWithLocalChanges.copy(text = localTextFlag)
                }


                PostListItem(postWithLocalChanges, isInProgress)
            }
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
        val isTextFlags = mutableMapOf<Long, String>()

        // TODO фото
        val isLikedFlags = mutableMapOf<Long, Boolean>()
        val isFavouriteFlags = mutableMapOf<Long, Boolean>()
    }

}