package com.thesis.sportologia.ui.events


import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.R
import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapter
import com.thesis.sportologia.ui.events.adapters.EventsPagerAdapter
import com.thesis.sportologia.ui.events.entities.EventListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// TODO бесконечная загрузка при попытке подписаться через посты и через мероприятия
abstract class ListEventsViewModel constructor(
    private val userId: String,
    private val eventsRepository: EventsRepository,
    logger: Logger
) : BaseViewModel(logger), EventsPagerAdapter.MoreButtonListener, EventsHeaderAdapter.FilterListener {

    internal val search = MutableLiveData("")

    private val isUpcomingOnlyLiveData = MutableLiveData(true)
    var isUpcomingOnly: Boolean
        get() = isUpcomingOnlyLiveData.value!!
        set(value) {
            isUpcomingOnlyLiveData.value = value
        }

    private val localChanges = LocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    private val _errorEvents = MutableLiveEvent<Int>()
    val errorEvents = _errorEvents.share()

    private val _scrollEvents = MutableLiveEvent<Unit>()
    val scrollEvents = _scrollEvents.share()

    private var _invalidateEvents = MutableLiveEvent<Unit>()
    val invalidateEvents = _invalidateEvents.share()

    val eventsFlow: Flow<PagingData<EventListItem>>


    init {
        val originEventsFlow = this.getDataFlow()

        eventsFlow = combine(
            originEventsFlow,
            localChangesFlow.debounce(50),
            ::merge
        )
    }

    abstract fun getDataFlow(): Flow<PagingData<Event>>

    override fun onEventDelete(eventListItem: EventListItem) {
        if (isInProgress(eventListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(eventListItem.id, true)
                delete(eventListItem)
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(eventListItem.id, false)
            }
        }
    }

    override fun onToggleLike(eventListItem: EventListItem) {
        if (isInProgress(eventListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(eventListItem.id, true)
                setLike(eventListItem)
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(eventListItem.id, false)
            }
        }
    }

    override fun onToggleFavouriteFlag(eventListItem: EventListItem) {
        if (isInProgress(eventListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(eventListItem.id, true)
                setFavoriteFlag(eventListItem)
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(eventListItem.id, false)
            }
        }
    }

    override fun filterApply(isUpcomingOnly: Boolean) {
        if (this.isUpcomingOnly == isUpcomingOnly) return

        this.isUpcomingOnly = isUpcomingOnly
        refresh()
    }

    fun refresh() {
        this.search.postValue(this.search.value)
    }

    fun onEventCreated() {
        invalidateList()
    }

    fun onEventEdited() {
        invalidateList()
    }

    private suspend fun setLike(eventListItem: EventListItem) {
        val newFlagValue = !eventListItem.isLiked
        eventsRepository.setIsLiked(userId, eventListItem.event, newFlagValue)
        localChanges.isLikedFlags[eventListItem.id] = newFlagValue
        //localChanges.isTextFlags[eventListItem.id] = eventListItem.text + "asgagasagag"
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun setFavoriteFlag(eventListItem: EventListItem) {
        val newFlagValue = !eventListItem.isFavourite
        eventsRepository.setIsFavourite(userId, eventListItem.event, newFlagValue)
        localChanges.isFavouriteFlags[eventListItem.id] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun delete(eventListItem: EventListItem) {
        eventsRepository.deleteEvent(eventListItem.id)
        invalidateList()
    }

    private fun setProgress(eventListItemId: Long, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(eventListItemId)
        } else {
            localChanges.idsInProgress.remove(eventListItemId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(eventListItemId: Long) =
        localChanges.idsInProgress.contains(eventListItemId)

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
        events: PagingData<Event>,
        localChanges: OnChange<LocalChanges>
    ): PagingData<EventListItem> {
        return events
            .map { event ->
                val isInProgress = localChanges.value.idsInProgress.contains(event.id)
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[event.id]
                val localLikedFlag = localChanges.value.isLikedFlags[event.id]

                val eventWithLocalChanges = event
                if (localFavoriteFlag != null) {
                    eventWithLocalChanges.copy(isFavourite = localFavoriteFlag)
                }
                if (localLikedFlag != null) {
                    eventWithLocalChanges.copy(isFavourite = localLikedFlag)
                }

                EventListItem(eventWithLocalChanges, isInProgress)
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
        val idsInProgress = mutableSetOf<Long>()

        // TODO фото
        val isLikedFlags = mutableMapOf<Long, Boolean>()
        val isFavouriteFlags = mutableMapOf<Long, Boolean>()
    }

}