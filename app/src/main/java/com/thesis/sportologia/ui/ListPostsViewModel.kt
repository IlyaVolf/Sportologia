package com.thesis.sportologia.ui


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@FlowPreview
@ExperimentalCoroutinesApi
class ListPostsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger) {

    /*private val _posts = ObservableHolder<List<Post>>(DataHolder.loading())
    val posts = _posts.share()*/

    private val currentAccountId = MutableLiveData(CurrentAccount().id)

    val postsFlow: Flow<PagingData<Post>>

    init {
        postsFlow = currentAccountId.asFlow()
            .flatMapLatest {
                postsRepository.getPagedUserPosts(it)
            }.cachedIn(viewModelScope)
    }

    /*fun load() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val data = postsRepository.getUserPosts(currentAccount.id)
            withContext(Dispatchers.Main) {
                _posts.value = DataHolder.ready(data)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _posts.value = DataHolder.error(e)
            }
        }
    }*/

}