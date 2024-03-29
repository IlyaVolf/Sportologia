package com.thesis.sportologia.ui.signup

import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.AuthTokenRepository
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.model.users.entities.GenderType
import com.thesis.sportologia.model.users.entities.UserCreateDataEntity
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.entities.ProfileSettingsViewItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsSignUpViewModel @Inject constructor(
    private val authTokenRepository: AuthTokenRepository,
    private val usersRepository: UsersRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _saveHolder = ObservableHolder<Unit>(DataHolder.init())
    val saveHolder = _saveHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ExceptionType>()
    val toastMessageEvent = _toastMessageEvent.share()

    private val _navigateToTabsEvent = MutableUnitLiveEvent()
    val navigateToTabsEvent = _navigateToTabsEvent.share()

    fun signUp(email: String, password: String, profileSettingsViewItem: ProfileSettingsViewItem) {
        viewModelScope.launch {
            try {
                _saveHolder.value = DataHolder.loading()

                if (!validateData(email, password, profileSettingsViewItem)) {
                    _saveHolder.value = DataHolder.error(Exception())
                    return@launch
                }

                val reformattedDescription = reformatText(profileSettingsViewItem.description ?: "")

                withContext(Dispatchers.IO) {
                    val token = usersRepository.signUp(
                        UserCreateDataEntity(
                            email = email,
                            password = password,
                            userId = profileSettingsViewItem.nickname!!,
                            name = profileSettingsViewItem.name!!,
                            userType = profileSettingsViewItem.accountType!!,
                            gender = profileSettingsViewItem.gender,
                            birthDate = profileSettingsViewItem.birthDate,
                            description = reformattedDescription,
                            profilePhotoURI = profileSettingsViewItem.profilePhotoUri,
                            position = profileSettingsViewItem.position,
                            categories = profileSettingsViewItem.categories!!
                        )
                    )

                    authTokenRepository.setToken(token)
                }

                _saveHolder.value = DataHolder.ready(Unit)
                launchTabsScreen()
            } catch (e: Exception) {
                _toastMessageEvent.publishEvent(ExceptionType.OTHER)
                _saveHolder.value = DataHolder.error(e)
            }
            // TODO email и nickname exceptions
        }
    }

    private fun validateData(
        email: String,
        password: String,
        profileSettingsViewItem: ProfileSettingsViewItem
    ): Boolean {
        if (!validateName(profileSettingsViewItem.name)) {
            return false
        }

        if (!validateNickname(profileSettingsViewItem.nickname)) {
            return false
        }

        if (!validateGender(profileSettingsViewItem.accountType, profileSettingsViewItem.gender)) {
            return false
        }

        if (!validateBirthDate(
                profileSettingsViewItem.accountType,
                profileSettingsViewItem.birthDate
            )
        ) {
            return false
        }

        return true
    }

    private fun validateName(name: String?): Boolean {
        if (name == null || name == "") {
            _toastMessageEvent.publishEvent(ExceptionType.EMPTY_NAME)
            return false
        }

        return true
    }

    private fun validateNickname(nickname: String?): Boolean {
        if (nickname == null || nickname == "") {
            _toastMessageEvent.publishEvent(ExceptionType.EMPTY_NICKNAME)
            return false
        }

        return true
    }

    private fun validateGender(userType: UserType?, gender: GenderType?): Boolean {
        if (userType == UserType.ATHLETE && gender == null) {
            _toastMessageEvent.publishEvent(ExceptionType.EMPTY_GENDER)
            return false
        }

        return true
    }

    private fun validateBirthDate(userType: UserType?, birthDate: Long?): Boolean {
        if (userType == UserType.ATHLETE && birthDate == null) {
            _toastMessageEvent.publishEvent(ExceptionType.EMPTY_BIRTHDATE)
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

    private fun launchTabsScreen() = _navigateToTabsEvent.publishEvent()

    enum class ExceptionType {
        NICKNAME_ALREADY_EXISTS,
        EMAIL_ALREADY_EXISTS,
        EMPTY_NAME,
        EMPTY_NICKNAME,
        EMPTY_BIRTHDATE,
        EMPTY_GENDER,
        OTHER,
    }

}