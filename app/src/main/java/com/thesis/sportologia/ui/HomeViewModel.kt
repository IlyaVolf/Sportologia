package com.thesis.sportologia.ui

import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val userId = CurrentAccount().id

    private val _avatarHolder = ObservableHolder<String?>(DataHolder.loading())
    val avatarHolder = _avatarHolder.share()

    init {
        init()
    }

    fun init() = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            _avatarHolder.value = DataHolder.loading()
        }
        getUser()
    }

    private suspend fun getUser() {
        try {
            val user = usersRepository.getUser(userId)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    _avatarHolder.value =
                        DataHolder.ready(user.profilePhotoURI)
                } else {
                    _avatarHolder.value = DataHolder.error(Exception("no such user"))
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _avatarHolder.value = DataHolder.error(e)
            }
        }
    }

}