package com.thesis.sportologia.ui


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.services.entities.ServiceDetailedViewItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceViewModel @AssistedInject constructor(
    @Assisted private val serviceId: Long,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _serviceHolder = ObservableHolder<ServiceDetailedViewItem>(DataHolder.loading())
    val serviceHolder = _serviceHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ResponseType>()
    val toastMessageEvent = _toastMessageEvent.share()

    val localChanges = servicesRepository.localChanges
    val localChangesFlow = servicesRepository.localChangesFlow

    init {
        getService()
        initMergeChanges()
    }

    fun getService() {
        viewModelScope.launch {
            try {
                _serviceHolder.value = DataHolder.loading()
                val serviceDetailed = servicesRepository.getServiceDetailed(serviceId)
                if (serviceDetailed != null) {
                    _serviceHolder.value =
                        DataHolder.ready(ServiceDetailedViewItem(serviceDetailed.copy()))
                } else {
                    _serviceHolder.value = DataHolder.error(Exception("no such service"))
                }
            } catch (e: Exception) {
                _serviceHolder.value = DataHolder.error(e)
            }
        }
    }


    private fun initMergeChanges() {
        viewModelScope.launch {
            localChangesFlow.collectLatest { localChanges ->

                _serviceHolder.value?.onReady { service ->
                    _serviceHolder.value = DataHolder.READY(
                        service.copy(
                            serviceDetailed = service.serviceDetailed.copy(
                                isAcquired = localChanges.value.isAcquiredFlag[serviceId]
                                    ?: service.isAcquired
                            )
                        )
                    )
                }

                _serviceHolder.value?.onReady { service ->
                    _serviceHolder.value = DataHolder.READY(
                        service.copy(
                            serviceDetailed = service.serviceDetailed.copy(
                                isFavourite = localChanges.value.isFavouriteFlags[serviceId]
                                    ?: service.isFavourite
                            )
                        )
                    )
                }
            }
        }
    }

    private fun setProgress(serviceId: Long, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(serviceId)
        } else {
            localChanges.idsInProgress.remove(serviceId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun setAcquiredFlag(serviceId: Long, isAcquired: Boolean) {
        localChanges.isAcquiredFlag[serviceId] = isAcquired
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun setFavouriteFlag(serviceId: Long, isFavourite: Boolean) {
        localChanges.isFavouriteFlags[serviceId] = isFavourite
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(serviceListItemId: Long) =
        localChanges.idsInProgress.contains(serviceListItemId)


    fun acquireService() {
        if (isInProgress(serviceId)) return

        viewModelScope.launch {
            try {
                setProgress(serviceId, true)
                servicesRepository.acquireService(serviceId)
                _toastMessageEvent.publishEvent(ResponseType.ACQUIRED_SUCCESSFULLY)
                setAcquiredFlag(serviceId, true)
            } catch (e: Exception) {
                _toastMessageEvent.publishEvent(ResponseType.ACQUIRE_ERROR)
            } finally {
                setProgress(serviceId, false)
            }
        }
    }

    fun onToggleFavouriteFlag() {
        _serviceHolder.value?.onReady { serviceFullEvent ->

            if (isInProgress(serviceId)) return

            viewModelScope.launch {
                try {
                    setProgress(serviceId, true)
                    val newIsFavourite =
                        !(localChanges.isFavouriteFlags[serviceId]
                            ?: serviceFullEvent.isFavourite)
                    servicesRepository.setIsFavourite(
                        CurrentAccount().id,
                        serviceId,
                        newIsFavourite
                    )
                    setFavouriteFlag(serviceId, newIsFavourite)
                } catch (e: Exception) {
                    _toastMessageEvent.publishEvent(ResponseType.FAVS_ERROR)
                } finally {
                    setProgress(serviceId, false)
                }
            }
        }
    }

    enum class ResponseType {
        ACQUIRE_ERROR, FAVS_ERROR, ACQUIRED_SUCCESSFULLY
    }

/*class LocalChanges(val holder: ObservableHolder<ServiceDetailedViewItem>) {
    var isLoading = false

    var isAcquiredFlag: Boolean? = null
        set(value) {
            holder.value?.onReady {
                holder.value = DataHolder.READY(
                    it.copy(
                        serviceDetailed = it.serviceDetailed.copy(
                            isAcquired = value ?: it.isAcquired
                        )
                    )
                )
            }
            field = value
        }

    var isFavouriteFlag: Boolean? = null
        set(value) {
            holder.value?.onReady {
                holder.value = DataHolder.READY(
                    it.copy(
                        serviceDetailed = it.serviceDetailed.copy(
                            isFavourite = value ?: it.isFavourite
                        )
                    )
                )
            }
            field = value
        }
}*/

    @AssistedFactory
    interface Factory {
        fun create(serviceId: Long): ServiceViewModel
    }

}