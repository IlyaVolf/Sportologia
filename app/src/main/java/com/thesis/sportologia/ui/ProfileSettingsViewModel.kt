package com.thesis.sportologia.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.model.users.entities.*
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
class ProfileSettingsViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _profileSettingsHolder =
        ObservableHolder<ProfileSettingsViewItem>(DataHolder.init())
    val profileSettingsHolder = _profileSettingsHolder.share()

    private val _saveHolder = ObservableHolder<Unit>(DataHolder.init())
    val saveHolder = _saveHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ExceptionType>()
    val toastMessageEvent = _toastMessageEvent.share()

    init {
        getProfileSettings()
    }

    fun getProfileSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    _profileSettingsHolder.value = DataHolder.loading()
                }

                val user = usersRepository.getUser(CurrentAccount().id, CurrentAccount().id)
                val profileSettings = when (user) {
                    is Athlete -> {
                        ProfileSettingsViewItem(
                            accountType = UserType.ATHLETE,
                            name = user.name,
                            nickname = user.id,
                            description = user.description,
                            gender = user.gender,
                            birthDate = user.birthDate,
                            profilePhotoUri = user.profilePhotoURI,
                            categories = user.categories,
                            position = user.position
                        )
                    }
                    is Organization -> {
                        ProfileSettingsViewItem(
                            accountType = UserType.ORGANIZATION,
                            name = user.name,
                            nickname = user.id,
                            description = user.description,
                            gender = null,
                            birthDate = null,
                            profilePhotoUri = user.profilePhotoURI,
                            categories = user.categories,
                            position = user.position
                        )
                    }
                    else -> throw Exception()
                }

                withContext(Dispatchers.Main) {
                    _profileSettingsHolder.value = DataHolder.ready(profileSettings)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _profileSettingsHolder.value = DataHolder.error(e)
                }
            }
        }
    }

    fun onSaveButtonPressed(profileSettingsViewItem: ProfileSettingsViewItem) {

        viewModelScope.launch {
            try {
                _saveHolder.value = DataHolder.loading()

                if (!validateData(profileSettingsViewItem)) {
                    _saveHolder.value = DataHolder.error(Exception())
                    return@launch
                }

                val reformattedDescription =
                    reformatText(profileSettingsViewItem.description ?: "")

                withContext(Dispatchers.IO) {
                    usersRepository.updateUser(
                        UserEditDataEntity(
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
                }
                _saveHolder.value = DataHolder.ready(Unit)
            } catch (e: Exception) {
                _toastMessageEvent.publishEvent(ExceptionType.OTHER)
                _saveHolder.value = DataHolder.error(e)
            }
        }
    }

    private fun validateData(profileSettingsViewItem: ProfileSettingsViewItem): Boolean {
        Log.d("abcdef", profileSettingsViewItem.toString())

        if (!validateName(profileSettingsViewItem.name)) {
            return false
        }

        if (!validateNickname(profileSettingsViewItem.nickname)) {
            return false
        }

        if (!validateGender(
                profileSettingsViewItem.accountType,
                profileSettingsViewItem.gender
            )
        ) {
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

    enum class ExceptionType {
        EMPTY_NAME,
        EMPTY_NICKNAME,
        EMPTY_GENDER,
        EMPTY_BIRTHDATE,
        OTHER,
    }

}