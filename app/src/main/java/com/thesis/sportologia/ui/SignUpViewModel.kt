package com.thesis.sportologia.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.utils.MutableUnitLiveEvent
import com.thesis.sportologia.utils.publishEvent
import com.thesis.sportologia.utils.requireValue
import com.thesis.sportologia.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _showEmailErrorToastEvent = MutableUnitLiveEvent()
    val showEmailErrorToastEvent = _showEmailErrorToastEvent.share()

    private val _showNicknameErrorToastEvent = MutableUnitLiveEvent()
    val showNicknameErrorToastEvent = _showNicknameErrorToastEvent.share()

    private val _navigateToTabsEvent = MutableUnitLiveEvent()
    val navigateToTabsEvent = _navigateToTabsEvent.share()

    fun signUp(email: String, password: String) = viewModelScope.launch {
        showProgress()
        try {
            //usersRepository.signUp(email, password)
            launchTabsScreen()
            /*} catch (e: NickNameAlreadyExistsException) {
            processNicknameException()
        }
        catch (e: EmailAlreadyExistsException) {
            processEmailException()
        }*/
        } catch (e: Exception) {
            showEmailErrorToast()
        }
    }

    private fun processNicknameException() {
        _state.value = _state.requireValue().copy(
            signInInProgress = false
        )
        showNicknameErrorToast()
    }

    private fun processEmailException() {
        _state.value = _state.requireValue().copy(
            signInInProgress = false
        )
        showEmailErrorToast()
    }

    private fun showProgress() {
        _state.value = State(signInInProgress = true)
    }

    private fun showNicknameErrorToast() = _showNicknameErrorToastEvent.publishEvent()

    private fun showEmailErrorToast() = _showEmailErrorToastEvent.publishEvent()

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