package com.thesis.sportologia.ui.events


import android.util.Log
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.entities.EventDataEntity
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class ListEventsViewModelProfile @AssistedInject constructor(
    @Assisted filterParams: FilterParamsEvents,
    @Assisted private val userId: String,
    private val eventsRepository: EventsRepository,
    logger: Logger
) : ListEventsViewModel(filterParams, userId, eventsRepository, logger) {

    override fun getDataFlow(): Flow<PagingData<EventDataEntity>> {
        return searchLive.asFlow()
            .flatMapLatest {
                Log.d("abcdef", "getDataFlow ${isUpcomingOnlyLiveData.value!!}")
                eventsRepository.getPagedUserEvents(userId, isUpcomingOnlyLiveData.value!!)
            }.cachedIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {
        fun create(filterParams: FilterParamsEvents, userId: String): ListEventsViewModelProfile
    }

}