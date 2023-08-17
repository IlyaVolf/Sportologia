package com.thesis.sportologia.ui.services

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.services.ServicesLocalChanges
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.services.entities.ServiceDataEntity
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.services.adapters.ServicesHeaderAdapter
import com.thesis.sportologia.ui.services.adapters.ServicesPagerAdapter
import com.thesis.sportologia.ui.services.entities.ServiceListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// TODO бесконечная загрузка при попытке подписаться через посты и через мероприятия
abstract class ListServicesViewModel constructor(
    filterParams: FilterParamsServices,
    private val userId: String,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger), ServicesPagerAdapter.MoreButtonListener,
    ServicesHeaderAdapter.FilterListener {

    protected val searchLive = MutableLiveData("")
    protected val filterParamsLive = MutableLiveData<FilterParamsServices>()

    val localChanges = servicesRepository.localChanges
    val localChangesFlow = servicesRepository.localChangesFlow

    private val serviceTypeLiveData = MutableLiveData<ServiceType?>(null)
    var serviceType: ServiceType?
        get() = serviceTypeLiveData.value
        set(value) {
            serviceTypeLiveData.value = value
        }

    private val _errorServices = MutableLiveEvent<Int>()
    val errorServices = _errorServices.share()

    private val _scrollServices = MutableLiveEvent<Unit>()
    val scrollServices = _scrollServices.share()

    private var _invalidateServices = MutableLiveEvent<Unit>()
    val invalidateServices = _invalidateServices.share()

    val servicesFlow: Flow<PagingData<ServiceListItem>>

    init {
        filterParamsLive.value = filterParams

        val originServicesFlow = this.getDataFlow()

        servicesFlow = combine(
            originServicesFlow,
            localChangesFlow.debounce(50),
            ::merge
        )
    }

    abstract fun getDataFlow(): Flow<PagingData<ServiceDataEntity>>

    override fun onToggleFavouriteFlag(serviceListItem: ServiceListItem) {
        if (isInProgress(serviceListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(serviceListItem.id, true)
                withContext(Dispatchers.IO) {
                    setFavoriteFlag(serviceListItem)
                }
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(serviceListItem.id, false)
            }
        }
    }

    override fun filterApply(serviceType: ServiceType?) {
        if (this.serviceType == serviceType) return

        this.serviceType = serviceType
        refresh()
    }

    fun setSearchBy(searchQuery: String, filterParams: FilterParamsServices) {
        this.filterParamsLive.value = filterParams
        this.searchLive.value = searchQuery
        scrollListToTop()
    }

    fun refresh() {
        this.searchLive.value = this.searchLive.value
    }

    fun onServiceCreated() {
        invalidateList()
    }

    fun onServiceEdited() {
        invalidateList()
    }

    fun onServiceDeleted() {
        invalidateList()
    }

    private suspend fun setFavoriteFlag(serviceListItem: ServiceListItem) {
        val newFlagValue = !serviceListItem.isFavourite
        servicesRepository.setIsFavourite(CurrentAccount().id, serviceListItem.serviceDataEntity.id!!, newFlagValue)
        localChanges.isFavouriteFlags[serviceListItem.id] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun setProgress(serviceListItemId: String, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(serviceListItemId)
        } else {
            localChanges.idsInProgress.remove(serviceListItemId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(serviceListItemId: String) =
        localChanges.idsInProgress.contains(serviceListItemId)

    private fun showError(@StringRes errorMessage: Int) {
        _errorServices.publishEvent(errorMessage)
    }

    private fun scrollListToTop() {
        _scrollServices.publishEvent(Unit)
    }

    private fun invalidateList() {
        _invalidateServices.publishEvent(Unit)
    }

    private fun merge(
        services: PagingData<ServiceDataEntity>,
        localChanges: OnChange<ServicesLocalChanges>
    ): PagingData<ServiceListItem> {
        return services
            .map { service ->
                val isInProgress = localChanges.value.idsInProgress.contains(service.id)
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[service.id]

                val serviceWithLocalChanges = if (localFavoriteFlag == null) {
                    service.copy()
                } else {
                    service.copy(isFavourite = localFavoriteFlag)
                }
                ServiceListItem(serviceWithLocalChanges, isInProgress)
            }
    }


}