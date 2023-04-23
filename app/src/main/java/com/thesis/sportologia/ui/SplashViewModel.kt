package com.thesis.sportologia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.model.users.AuthTokenRepository
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.utils.MutableLiveEvent
import com.thesis.sportologia.utils.publishEvent
import com.thesis.sportologia.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authTokenRepository: AuthTokenRepository
) : ViewModel() {

    private val _launchMainScreenEvent = MutableLiveEvent<Boolean>()
    val launchMainScreenEvent = _launchMainScreenEvent.share()

    init {
        viewModelScope.launch {
            _launchMainScreenEvent.publishEvent(isSigneIn())
        }
    }

    private suspend fun isSigneIn(): Boolean {
        return authTokenRepository.getToken() != null
    }
}
