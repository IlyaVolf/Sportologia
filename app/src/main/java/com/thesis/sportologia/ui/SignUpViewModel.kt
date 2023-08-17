package com.thesis.sportologia.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.ui.events.CreateEditEventViewModel
import com.thesis.sportologia.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _toastMessageEvent = MutableLiveEvent<ExceptionType>()
    val toastMessageEvent = _toastMessageEvent.share()
    private val _navigateToProfileSettingsSignUpEvent = MutableUnitLiveEvent()
    val navigateToProfileSettingsSignUpEvent = _navigateToProfileSettingsSignUpEvent.share()

    fun onSignUpButtonPressed(email: String, password: String) = viewModelScope.launch {
        showProgress()
        
        if (email == "") {
            _toastMessageEvent.publishEvent(ExceptionType.EMPTY_EMAIL)
            return@launch
        }
        
        if (password == "") {
            _toastMessageEvent.publishEvent(ExceptionType.EMPTY_PASSWORD)
            return@launch
        }

        if (password.length < 6) {
            _toastMessageEvent.publishEvent(ExceptionType.SHORT_PASSWORD)
            return@launch
        }
        
        try {
            val emailExists = usersRepository.checkEmailExists(email)
            if (emailExists) {
                processEmailException()
            } else {
                navigateToProfileSettingsSignUp()
            }
        } catch (e: Exception) {
            _toastMessageEvent.publishEvent(ExceptionType.OTHER)
        }
    }

    /*fun signUp(email: String, password: String) = viewModelScope.launch {
        showProgress()
        try {
            //usersRepository.signUp(email, password)
            navigateToProfileSettingsSignUp()
            /*} catch (e: NickNameAlreadyExistsException) {
            processNicknameException()
        }
        catch (e: EmailAlreadyExistsException) {
            processEmailException()
        }*/
        } catch (e: Exception) {
            showEmailErrorToast()
        }
    }*/

    private fun processEmailException() {
        _state.value = _state.requireValue().copy(
            signInInProgress = false
        )
        showEmailErrorToast()
    }

    private fun showProgress() {
        _state.value = State(signInInProgress = true)
    }

    private fun showEmailErrorToast() = _toastMessageEvent.publishEvent(ExceptionType.EMAIL_ALREADY_EXISTS)

    private fun navigateToProfileSettingsSignUp() = _navigateToProfileSettingsSignUpEvent.publishEvent()

    enum class ExceptionType {
        EMPTY_EMAIL,
        EMPTY_PASSWORD,
        EMAIL_ALREADY_EXISTS,
        SHORT_PASSWORD,
        OTHER
    }

    data class State(
        val emptyEmailError: Boolean = false,
        val emptyPasswordError: Boolean = false,
        val signInInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = signInInProgress
        val enableViews: Boolean get() = !signInInProgress
    }

}