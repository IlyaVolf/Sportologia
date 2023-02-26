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
class CreatePostViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger) {

    // TODO
    private val currentAccount = CurrentAccount()

    private val _holder = ObservableHolder(DataHolder.ready(null))
    val holder = _holder.share()

    private val _toastMessageEvent = MutableLiveEvent<ErrorType>()
    val toastMessageEvent = _toastMessageEvent.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    fun createPost(text: String, photosUrls: List<String>) {

        if (!validateText(text)) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    _holder.value = DataHolder.loading()
                }
                postsRepository.createPost(
                    Post(
                        id = 3, // не тут надо создавать!
                        authorName = currentAccount.userName,
                        authorId = currentAccount.id,
                        profilePictureUrl = currentAccount.profilePictureUrl,
                        text = text,
                        likesCount = 0,
                        isAuthorAthlete = currentAccount.isAthlete,
                        isLiked = false,
                        isFavourite = false,
                        postedDate = Calendar.getInstance(), // по идее в самом коцне надо создавать!
                        photosUrls = photosUrls
                    )
                )
                withContext(Dispatchers.Main) {
                    _holder.value = DataHolder.ready(null)
                    goBack(true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _holder.value = DataHolder.error(e)
                }
            }
        }

        return
    }

    private fun validateText(text: String) : Boolean {
        if (text == "") {
            _toastMessageEvent.publishEvent(ErrorType.EMPTY_POST)
            return false
        }
        return true
    }

    private fun goBack(isCreated: Boolean) = _goBackEvent.publishEvent()

}

enum class ErrorType {
    EMPTY_POST
}