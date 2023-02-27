package com.thesis.sportologia.ui


import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CreateEditPostViewModel @AssistedInject constructor(
    @Assisted private val postId: Long?,
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger) {

    // TODO
    private val currentAccount = CurrentAccount()

    private var mode: Mode

    private val _postHolder = ObservableHolder<Post?>(DataHolder.ready(null))
    val postHolder = _postHolder.share()

    private val _saveHolder = ObservableHolder(DataHolder.ready(null))
    val saveHolder = _saveHolder.share()


    private val _toastMessageEvent = MutableLiveEvent<ErrorType>()
    val toastMessageEvent = _toastMessageEvent.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    init {
        if (postId == null) {
            mode = Mode.CREATE
        } else {
            mode = Mode.EDIT
            getPost()
        }
    }

    fun onSaveButtonPressed(text: String, photosUrls: List<String>) {

        if (!validateText(text)) {
            return
        }

        // check whether text has left the same
        var post: Post? = null
        _postHolder.value!!.onReady {
            post = it
        }
        if (text == post?.text) {
            goBack()
        }

        lateinit var newPost: Post
        when (mode) {
            Mode.CREATE ->
            newPost = Post(
                id = -1, // не тут надо создавать!
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
            Mode.EDIT -> _postHolder.value!!.onReady {
                newPost = it!!.copy(text = text, photosUrls = photosUrls)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.loading()
                }
                when (mode) {
                    Mode.CREATE -> postsRepository.createPost(newPost)
                    Mode.EDIT -> postsRepository.updatePost(newPost)
                }
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.ready(null)
                    goBack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.error(e)
                }
            }
        }

        return
    }

    private fun getPost() = viewModelScope.launch(Dispatchers.IO) {
        try {
            withContext(Dispatchers.Main) {
                _postHolder.value = DataHolder.loading()
            }
            val post = postsRepository.getPost(postId!!)
            withContext(Dispatchers.Main) {
                _postHolder.value = DataHolder.ready(post)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _postHolder.value = DataHolder.error(e)
            }
        }
    }

    private fun validateText(text: String): Boolean {
        // check whether text is empty
        if (text == "") {
            _toastMessageEvent.publishEvent(ErrorType.EMPTY_POST)
            return false
        }

        return true
    }

    private fun goBack() = _goBackEvent.publishEvent()

    enum class ErrorType {
        EMPTY_POST
    }

    enum class Mode {
        CREATE,
        EDIT
    }

    @AssistedFactory
    interface Factory {
        fun create(postId: Long?): CreateEditPostViewModel
    }


}