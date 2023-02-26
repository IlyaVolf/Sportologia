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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ListPostsViewModel @AssistedInject constructor(
    @Assisted private val mode: ListPostsMode,
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger) {

    /*private val _posts = ObservableHolder<List<Post>>(DataHolder.loading())
    val posts = _posts.share()*/

    private val search = MutableLiveData("")

    private val athTorgFLiveData = MutableLiveData<Boolean?>(null)
    var athTorgF: Boolean?
        get() = athTorgFLiveData.value
        set(value) {
            athTorgFLiveData.value = value
        }

    val postsFlow: Flow<PagingData<Post>> = when (mode) {
        ListPostsMode.PROFILE_OWN_PAGE -> {
            athTorgFLiveData.asFlow()
                .flatMapLatest {
                    postsRepository.getPagedUserPosts(CurrentAccount().id)
                }.cachedIn(viewModelScope)
        }
        ListPostsMode.HOME_PAGE -> {
            athTorgFLiveData.asFlow()
                .flatMapLatest {
                    postsRepository.getPagedUserSubscribedOnPosts(CurrentAccount().id, athTorgF)
                }.cachedIn(viewModelScope)
        }
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

    @AssistedFactory
    interface Factory {
        fun create(mode: ListPostsMode): ListPostsViewModel
    }

}