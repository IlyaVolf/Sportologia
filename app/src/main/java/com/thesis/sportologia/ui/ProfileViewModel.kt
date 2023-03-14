package com.thesis.sportologia.ui


import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.model.users.entities.Athlete
import com.thesis.sportologia.model.users.entities.Organization
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.posts.entities.PostListItem
import com.thesis.sportologia.ui.users.entities.AthleteItem
import com.thesis.sportologia.ui.users.entities.OrganizationItem
import com.thesis.sportologia.ui.users.entities.UserItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.properties.Delegates

// TODO можно создать интерфейс или абстрактный, где все кроме поведения - идентично. Ибо перегружено
class ProfileViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val usersRepository: UsersRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _subscribeHolder = ObservableHolder(DataHolder.ready(null))
    val subscribeHolder = _subscribeHolder.share()

    private val _userHolder = ObservableHolder<UserItem>(DataHolder.init())
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
            val user = usersRepository.getUser(userId)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    when (user) {
                        is Athlete -> {
                            _userHolder.value =
                                DataHolder.ready(AthleteItem(user.copy()))

                        }
                        is Organization -> {
                            _userHolder.value = DataHolder.ready(OrganizationItem(user.copy()))
                        }
                    }
                } else {
                    _userHolder.value = DataHolder.error(Exception("no such user"))
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

        lateinit var userItem: UserItem
        _userHolder.value!!.onReady { userItem = it }
        val newIsSubscribed = !userItem.isSubscribed
        Log.d("BUGFIX", "$newIsSubscribed, ${userItem.hashCode()}")

        try {
            withContext(Dispatchers.Main) {
                _subscribeHolder.value = DataHolder.loading()
            }

            usersRepository.setIsSubscribe(CurrentAccount().id, userId, newIsSubscribed)
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
        userItem: UserItem,
        isSubscribed: Boolean
    ): UserItem {
        val followersCount = when (isSubscribed) {
            true -> {
                userItem.followersCount + 1
            }
            false -> {
                userItem.followersCount - 1
            }
        }

        return when (userItem) {
            is AthleteItem -> {
                AthleteItem(
                    userItem.athlete.copy(
                        followersCount = followersCount,
                        isSubscribed = isSubscribed
                    ),
                )
            }
            is OrganizationItem -> {
                OrganizationItem(
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