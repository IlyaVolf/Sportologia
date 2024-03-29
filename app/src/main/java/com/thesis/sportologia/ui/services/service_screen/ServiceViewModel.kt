package com.thesis.sportologia.ui.services.service_screen


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
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
    @Assisted private val serviceId: String,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _serviceHolder = ObservableHolder<ServiceDetailedViewItem>(DataHolder.loading())
    val serviceHolder = _serviceHolder.share()

    private val _deleteHolder = ObservableHolder<Unit>(DataHolder.init())
    val deleteHolder = _deleteHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ResponseType>()
    val toastMessageEvent = _toastMessageEvent.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

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
                val serviceDetailed = servicesRepository.getServiceDetailed(serviceId, CurrentAccount().id)
                _serviceHolder.value =
                    DataHolder.ready(ServiceDetailedViewItem(serviceDetailed.copy()))
            } catch (e: Exception) {
                _serviceHolder.value = DataHolder.error(e)
            }
        }
    }

    fun deleteService() {
        if (_serviceHolder.value?.isNotReady == true) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    _deleteHolder.value = DataHolder.loading()
                }
                servicesRepository.deleteService(serviceId)
                withContext(Dispatchers.Main) {
                    _deleteHolder.value = DataHolder.ready(Unit)
                    goBack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _deleteHolder.value = DataHolder.error(e)
                }
            }
        }
    }

    private fun initMergeChanges() {
        viewModelScope.launch {
            localChangesFlow.collectLatest { localChanges ->

                _serviceHolder.value?.onReady { service ->
                    _serviceHolder.value = DataHolder.READY(
                        service.copy(
                            serviceDetailedDataEntity = service.serviceDetailedDataEntity.copy(
                                isAcquired = localChanges.value.isAcquiredFlag[serviceId]
                                    ?: service.isAcquired
                            )
                        )
                    )
                }

                _serviceHolder.value?.onReady { service ->
                    _serviceHolder.value = DataHolder.READY(
                        service.copy(
                            serviceDetailedDataEntity = service.serviceDetailedDataEntity.copy(
                                isFavourite = localChanges.value.isFavouriteFlags[serviceId]
                                    ?: service.isFavourite
                            )
                        )
                    )
                }
            }
        }
    }

    private fun setProgress(serviceId: String, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(serviceId)
        } else {
            localChanges.idsInProgress.remove(serviceId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun setAcquiredFlag(serviceId: String, isAcquired: Boolean) {
        localChanges.isAcquiredFlag[serviceId] = isAcquired
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun setFavouriteFlag(serviceId: String, isFavourite: Boolean) {
        localChanges.isFavouriteFlags[serviceId] = isFavourite
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(serviceId: String) =
        localChanges.idsInProgress.contains(serviceId)


    fun acquireService() {
        if (isInProgress(serviceId)) return

        viewModelScope.launch {
            try {
                setProgress(serviceId, true)
                withContext(Dispatchers.IO) {
                    servicesRepository.acquireService(CurrentAccount().id, serviceId)
                }
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

    private fun goBack() = _goBackEvent.publishEvent()

    @AssistedFactory
    interface Factory {
        fun create(serviceId: String): ServiceViewModel
    }

}