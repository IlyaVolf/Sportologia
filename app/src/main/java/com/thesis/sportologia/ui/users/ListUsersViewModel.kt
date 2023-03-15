package com.thesis.sportologia.ui.users


import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.R
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.model.users.entities.UserSnippet
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapter
import com.thesis.sportologia.ui.users.adapters.UsersPagerAdapter
import com.thesis.sportologia.ui.users.entities.UserListItem
import com.thesis.sportologia.ui.users.entities.UserSnippetListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// TODO баг при отписки от пользователя, возврату на экран с подписками и послед обновлением страницы
abstract class ListUsersViewModel constructor(
    private val userId: String,
    private val usersRepository: UsersRepository,
    logger: Logger
) : BaseViewModel(logger) {

    internal val search = MutableLiveData("")

    private val localChanges = LocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    private val _errorEvents = MutableLiveEvent<Int>()
    val errorEvents = _errorEvents.share()

    private val _scrollEvents = MutableLiveEvent<Unit>()
    val scrollEvents = _scrollEvents.share()

    private var _invalidateEvents = MutableLiveEvent<Unit>()
    val invalidateEvents = _invalidateEvents.share()

    val usersFlow: Flow<PagingData<UserSnippetListItem>>

    init {
        val originUsersFlow = this.getDataFlow()

        usersFlow = combine(
            originUsersFlow,
            localChangesFlow.debounce(50),
            // TODO get rid of this!
            ::merge
        )
    }

    abstract fun getDataFlow(): Flow<PagingData<UserSnippet>>

    fun refresh() {
        this.search.postValue(this.search.value)
    }

    private fun setProgress(userListItemId: String, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(userListItemId)
        } else {
            localChanges.idsInProgress.remove(userListItemId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(userListItemId: String) =
        localChanges.idsInProgress.contains(userListItemId)

    private fun showError(@StringRes errorMessage: Int) {
        _errorEvents.publishEvent(errorMessage)
    }

    private fun scrollListToTop() {
        _scrollEvents.publishEvent(Unit)
    }

    private fun invalidateList() {
        _invalidateEvents.publishEvent(Unit)
    }

    private fun merge(
        users: PagingData<UserSnippet>,
        localChanges: OnChange<LocalChanges>
    ): PagingData<UserSnippetListItem> {
        return users
            .map { userSnippet ->
                val isInProgress = localChanges.value.idsInProgress.contains(userSnippet.id)

                UserSnippetListItem(userSnippet, isInProgress)
            }
    }

    /**
     * Non-data class which allows passing the same reference to the
     * MutableStateFlow multiple times in a row.
     */
    class OnChange<T>(val value: T)

    /**
     * Contains:
     * 1) identifiers of items which are processed now (deleting or favorite
     * flag updating).
     * 2) local isLiked and isFavourite flag updates to avoid list reloading
     */
    class LocalChanges {
        val idsInProgress = mutableSetOf<String>()
    }

}