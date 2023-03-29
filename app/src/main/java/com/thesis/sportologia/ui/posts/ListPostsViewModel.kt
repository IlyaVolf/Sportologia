package com.thesis.sportologia.ui.posts


import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.R
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.posts.PostsLocalChanges
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

    protected val search = MutableLiveData("")

    private val athTorgFLiveData = MutableLiveData<Boolean?>(null)
    var athTorgF: Boolean?
        get() = athTorgFLiveData.value
        set(value) {
            athTorgFLiveData.value = value
        }

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
        if (this.athTorgF == athTorgF) return

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
        //localChanges.isFavouriteFlags[postListItem.id] = newFlagValue
        //localChangesFlow.value = OnChange(localChanges)
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
        localChanges: OnChange<PostsLocalChanges>
    ): PagingData<PostListItem> {
        Log.d("LSVM", "1 ${posts}")
        return posts
            .map { post ->
                val isInProgress = localChanges.value.idsInProgress.contains(post.id)
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[post.id]
                val localLikedFlag = localChanges.value.isLikedFlags[post.id]

                val postWithLocalChanges = post.copy()
                if (localFavoriteFlag != null) {
                    postWithLocalChanges.copy(isFavourite = localFavoriteFlag)
                }
                if (localLikedFlag != null) {
                    postWithLocalChanges.copy(isFavourite = localLikedFlag)
                }
                Log.d("LSVM", "Post2 $localLikedFlag ${post.hashCode()} ${postWithLocalChanges.hashCode()}")

                PostListItem(postWithLocalChanges, isInProgress)
            }
    }

}