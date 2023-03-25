package com.thesis.sportologia.ui.services

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.thesis.sportologia.R
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.services.entities.Service
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

    private val serviceTypeLiveData = MutableLiveData<ServiceType?>(null)
    var serviceType: ServiceType?
        get() = serviceTypeLiveData.value
        set(value) {
            serviceTypeLiveData.value = value
        }

    private val localChanges = LocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

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

    abstract fun getDataFlow(): Flow<PagingData<Service>>

    override fun onServiceDelete(serviceListItem: ServiceListItem) {
        if (isInProgress(serviceListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(serviceListItem.id, true)
                delete(serviceListItem)
            } catch (e: Exception) {
                showError(R.string.error_loading_title)
            } finally {
                setProgress(serviceListItem.id, false)
            }
        }
    }

    override fun onToggleFavouriteFlag(serviceListItem: ServiceListItem) {
        if (isInProgress(serviceListItem.id)) return

        viewModelScope.launch {
            try {
                setProgress(serviceListItem.id, true)
                setFavoriteFlag(serviceListItem)
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
        this.searchLive.postValue(this.searchLive.value)
    }

    fun onServiceCreated() {
        invalidateList()
    }

    fun onServiceEdited() {
        invalidateList()
    }

    private suspend fun setFavoriteFlag(serviceListItem: ServiceListItem) {
        val newFlagValue = !serviceListItem.isFavourite
        servicesRepository.setIsFavourite(userId, serviceListItem.service.id, newFlagValue)
        localChanges.isFavouriteFlags[serviceListItem.id] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun delete(serviceListItem: ServiceListItem) {
        servicesRepository.deleteService(serviceListItem.id)
        invalidateList()
    }

    private fun setProgress(serviceListItemId: Long, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(serviceListItemId)
        } else {
            localChanges.idsInProgress.remove(serviceListItemId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(serviceListItemId: Long) =
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
        services: PagingData<Service>,
        localChanges: OnChange<LocalChanges>
    ): PagingData<ServiceListItem> {
        return services
            .map { service ->
                val isInProgress = localChanges.value.idsInProgress.contains(service.id)
                val localFavoriteFlag = localChanges.value.isFavouriteFlags[service.id]

                val serviceWithLocalChanges = service
                if (localFavoriteFlag != null) {
                    serviceWithLocalChanges.copy(isFavourite = localFavoriteFlag)
                }

                ServiceListItem(serviceWithLocalChanges, isInProgress)
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
        val isFavouriteFlags = mutableMapOf<Long, Boolean>()
    }

}