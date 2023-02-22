package com.thesis.sportologia.ui


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ListPostsViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger) {

    // TODO
    private val currentAccount = CurrentAccount()

    private val _affichePosts = ObservableHolder<List<Post>>(DataHolder.loading())
    val affichePosts = _affichePosts.share()

    init {
        load()
    }

    fun load() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val data = postsRepository.getUserPosts(currentAccount.id)
            withContext(Dispatchers.Main) {
                _affichePosts.value = DataHolder.ready(data)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _affichePosts.value = DataHolder.error(e)
            }
        }
    }

}