package com.thesis.sportologia.ui.services.list_services_screen


import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceDataEntity
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class ListServicesViewModelAcquired @AssistedInject constructor(
    @Assisted filterParams: FilterParamsServices,
    @Assisted private val userId: String,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : ListServicesViewModel(filterParams, userId, servicesRepository, logger) {

    override fun getDataFlow(): Flow<PagingData<ServiceDataEntity>> {
        return searchLive.asFlow()
            .flatMapLatest {
                servicesRepository.getPagedUserAcquiredServices(userId, serviceType)
            }.cachedIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {
        fun create(filterParams: FilterParamsServices, userId: String): ListServicesViewModelAcquired
    }

}