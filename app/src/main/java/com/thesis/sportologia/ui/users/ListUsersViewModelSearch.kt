package com.thesis.sportologia.ui.users


import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.model.users.InMemoryUsersRepository
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.model.users.entities.UserSnippet
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class ListUsersViewModelSearch @AssistedInject constructor(
    @Assisted private val userId: String,
    private val usersRepository: UsersRepository,
    logger: Logger
) : ListUsersViewModel(userId, usersRepository, logger) {

    val userType = UsersRepository.UserTypes.ATHLETES

    override fun getDataFlow(): Flow<PagingData<UserSnippet>> {
        return search.asFlow()
            .flatMapLatest {
                usersRepository.getPagedUsers(
                    UsersRepository.UsersFilter(it, userType)
                )
            }.cachedIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ListUsersViewModelSearch
    }
}