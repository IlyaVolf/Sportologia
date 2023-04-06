package com.thesis.sportologia.ui.posts

import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.posts.PostsRepository
import com.thesis.sportologia.model.posts.entities.PostDataEntity
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.posts.entities.PostCreateEditItem
import com.thesis.sportologia.ui.posts.entities.toCreateEditItem
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
    @Assisted private val postId: String?,
    private val postsRepository: PostsRepository,
    logger: Logger
) : BaseViewModel(logger) {

    // TODO
    private val currentAccount = CurrentAccount()

    private var mode: Mode

    private val _postHolder = ObservableHolder<PostDataEntity?>(DataHolder.init())
    val postHolder = _postHolder.share()

    private val _saveHolder = ObservableHolder<Unit>(DataHolder.init())
    val saveHolder = _saveHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ErrorType>()
    val toastMessageEvent = _toastMessageEvent.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    init {
        mode = if (postId == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }
        getPost()
    }

    fun onSaveButtonPressed(post: PostCreateEditItem) {
        if (!validateData(post)) {
            return
        }

        if (mode == Mode.EDIT) {
            checkIfTheSame(post)
        }

        val reformattedText = reformatText(post.text!!)

        lateinit var newPost: PostDataEntity
        when (mode) {
            Mode.CREATE ->
                newPost = PostDataEntity(
                    null,
                    authorName = currentAccount.userName,
                    authorId = currentAccount.id,
                    profilePictureUrl = currentAccount.profilePictureUrl,
                    text = reformattedText,
                    likesCount = 0,
                    isAuthorAthlete = currentAccount.isAthlete,
                    isLiked = false,
                    isFavourite = false,
                    postedDate = Calendar.getInstance().timeInMillis, // по идее в самом коцне надо создавать!
                    photosUrls = post.photosUrls
                )
            Mode.EDIT ->
                _postHolder.value!!.onReady {
                    newPost = it!!.copy(text = reformattedText, photosUrls = post.photosUrls)
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
                    _saveHolder.value = DataHolder.ready(Unit)
                    goBack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.error(e)
                }
            }
        }
    }

    private fun getPost() {
        if (mode == Mode.CREATE) {
            _postHolder.value = DataHolder.ready(null)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
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
    }

    private fun validateData(post: PostCreateEditItem): Boolean {
        if (!validateText(post.text)) {
            return false
        }

        return true
    }

    private fun validateText(text: String?): Boolean {
        // check whether the text is empty
        if (text == null || text == "") {
            _toastMessageEvent.publishEvent(ErrorType.EMPTY_TEXT)
            return false
        }

        return true
    }

    private fun reformatText(text: String): String {
        val newText = StringBuilder()

        // remove empty strings at the start and at the end of the text
        newText.append(removeEmptyStrings(text))

        return newText.toString()
    }

    private fun checkIfTheSame(post: PostCreateEditItem): Boolean {
        var savedPost: PostDataEntity? = null
        _postHolder.value!!.onReady {
            savedPost = it
        }

        if (savedPost!!.toCreateEditItem() == post) {
            return false
        }

        return true
    }

    private fun goBack() = _goBackEvent.publishEvent()

    enum class ErrorType {
        EMPTY_TEXT
    }

    enum class Mode {
        CREATE,
        EDIT
    }

    @AssistedFactory
    interface Factory {
        fun create(postId: String?): CreateEditPostViewModel
    }


}