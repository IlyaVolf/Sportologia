package com.thesis.sportologia.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.events.LogInUseCase
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

    private val _profilePhotoHolder = ObservableHolder<String?>(DataHolder.loading())
    val profilePhotoHolder = _profilePhotoHolder.share()

    init {
        init()
    }

    fun init() = viewModelScope.launch(Dispatchers.IO) {
       // Log.d("abcdef", "usecase1")
       // LogInUseCase(Dispatchers.IO)
        withContext(Dispatchers.Main) {
            _profilePhotoHolder.value = DataHolder.loading()
        }
        getUser()
    }

    private suspend fun getUser() {
        try {
            val user = usersRepository.getUser(CurrentAccount().id, userId)
            withContext(Dispatchers.Main) {
                _profilePhotoHolder.value = DataHolder.ready(user.profilePhotoURI)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _profilePhotoHolder.value = DataHolder.error(e)
            }
        }
    }

}