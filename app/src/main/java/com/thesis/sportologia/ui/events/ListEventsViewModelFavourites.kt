package com.thesis.sportologia.ui.events


import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class ListEventsViewModelFavourites @AssistedInject constructor(
    @Assisted private val userId: String,
    private val eventsRepository: EventsRepository,
    logger: Logger
) : ListEventsViewModel(userId, eventsRepository, logger) {

    override fun getDataFlow(): Flow<PagingData<Event>> {
        return search.asFlow()
            .flatMapLatest {
                eventsRepository.getPagedUserFavouriteEvents(isUpcomingOnly)
            }.cachedIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ListEventsViewModelFavourites
    }

}