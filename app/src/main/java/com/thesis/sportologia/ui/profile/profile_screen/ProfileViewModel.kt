package com.thesis.sportologia.ui.profile.profile_screen


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.model.users.entities.Athlete
import com.thesis.sportologia.model.users.entities.Organization
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.users.entities.AthleteListItem
import com.thesis.sportologia.ui.users.entities.OrganizationListItem
import com.thesis.sportologia.ui.users.entities.UserListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*

// TODO можно создать интерфейс или абстрактный, где все кроме поведения - идентично. Ибо перегружено
class ProfileViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val usersRepository: UsersRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _subscribeHolder = ObservableHolder(DataHolder.ready(null))
    val subscribeHolder = _subscribeHolder.share()

    private val _userHolder = ObservableHolder<UserListItem>(DataHolder.init())
    val userHolder = _userHolder.share()

    init {
        init()
    }

    fun init() = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            _userHolder.value = DataHolder.init()
        }
        getUser()
    }

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            _userHolder.value = DataHolder.loading()
        }
        getUser()
    }

    private suspend fun getUser() {
        try {
            val user = usersRepository.getUser(CurrentAccount().id, userId)
            withContext(Dispatchers.Main) {
                when (user) {
                    is Athlete -> {
                        _userHolder.value =
                            DataHolder.ready(AthleteListItem(user.copy()))

                    }
                    is Organization -> {
                        _userHolder.value = DataHolder.ready(OrganizationListItem(user.copy()))
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _userHolder.value = DataHolder.error(e)
            }
        }
    }

    // TODO правильно реализовать локальные изменения. В частности - значение isSubscribed
    fun onSubscribeButtonPressed() = viewModelScope.launch(Dispatchers.IO) {
        if (_subscribeHolder.value!!.isLoading || _userHolder.value!!.isLoading) return@launch

        lateinit var userItem: UserListItem
        _userHolder.value!!.onReady { userItem = it }
        val newIsSubscribed = !userItem.isSubscribed
        Log.d("BUGFIX", "$newIsSubscribed, ${userItem.hashCode()}")

        try {
            withContext(Dispatchers.Main) {
                _subscribeHolder.value = DataHolder.loading()
            }

            usersRepository.setIsSubscribed(CurrentAccount().id, userId, newIsSubscribed)
            withContext(Dispatchers.Main) {
                _userHolder.value =
                    DataHolder.ready(getUserItemOnSubscriptionAction(userItem, newIsSubscribed))
                _subscribeHolder.value = DataHolder.ready(null)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _subscribeHolder.value = DataHolder.error(e)
            }
        }
    }

    private fun getUserItemOnSubscriptionAction(
        userItem: UserListItem,
        isSubscribed: Boolean
    ): UserListItem {
        val followersCount = when (isSubscribed) {
            true -> {
                userItem.followersCount + 1
            }
            false -> {
                userItem.followersCount - 1
            }
        }

        return when (userItem) {
            is AthleteListItem -> {
                AthleteListItem(
                    userItem.athlete.copy(
                        followersCount = followersCount,
                        isSubscribed = isSubscribed
                    ),
                )
            }
            is OrganizationListItem -> {
                OrganizationListItem(
                    userItem.organization.copy(
                        followersCount = followersCount,
                        isSubscribed = isSubscribed
                    ),
                )
            }
            else -> throw Exception()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ProfileViewModel
    }

}