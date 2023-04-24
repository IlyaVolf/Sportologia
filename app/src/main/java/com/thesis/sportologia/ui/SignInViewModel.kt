package com.thesis.sportologia.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.model.users.AuthTokenRepository
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.utils.MutableUnitLiveEvent
import com.thesis.sportologia.utils.publishEvent
import com.thesis.sportologia.utils.requireValue
import com.thesis.sportologia.utils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authTokenRepository: AuthTokenRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _showAuthErrorToastEvent = MutableUnitLiveEvent()
    val showAuthErrorToastEvent = _showAuthErrorToastEvent.share()

    private val _navigateToTabsEvent = MutableUnitLiveEvent()
    val navigateToTabsEvent = _navigateToTabsEvent.share()

    fun signIn(email: String, password: String) = viewModelScope.launch {
        showProgress()
        try {
            val token = usersRepository.signIn(email, password)
           // TODO в другом месте
            authTokenRepository.setToken(token)
            launchTabsScreen()
        } catch (e: Exception) {
            processAuthException()
        }
    }

    private fun processAuthException() {
        _state.value = _state.requireValue().copy(
            signInInProgress = false
        )
        showAuthErrorToast()
    }

    private fun showProgress() {
        _state.value = State(signInInProgress = true)
    }

    private fun showAuthErrorToast() = _showAuthErrorToastEvent.publishEvent()

    private fun launchTabsScreen() = _navigateToTabsEvent.publishEvent()

    data class State(
        val emptyEmailError: Boolean = false,
        val emptyPasswordError: Boolean = false,
        val signInInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = signInInProgress
        val enableViews: Boolean get() = !signInInProgress
    }

}