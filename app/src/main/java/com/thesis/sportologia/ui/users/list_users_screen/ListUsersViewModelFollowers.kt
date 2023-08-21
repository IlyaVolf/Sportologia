package com.thesis.sportologia.ui.users.list_users_screen


import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.model.users.entities.UserSnippet
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class ListUsersViewModelFollowers @AssistedInject constructor(
    @Assisted filterParams: FilterParamsUsers,
    @Assisted private val userId: String,
    private val usersRepository: UsersRepository,
    logger: Logger
) : ListUsersViewModel(filterParams, userId, usersRepository, logger) {

    override fun getDataFlow(): Flow<PagingData<UserSnippet>> {
        return searchLive.asFlow()
            .flatMapLatest {
                usersRepository.getPagedFollowers(userId)
            }.cachedIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {
        fun create(filterParams: FilterParamsUsers, userId: String): ListUsersViewModelFollowers
    }
}