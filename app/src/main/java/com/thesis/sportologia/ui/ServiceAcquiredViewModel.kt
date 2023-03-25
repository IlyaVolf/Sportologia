package com.thesis.sportologia.ui


/**import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.services.entities.ServiceDetailedViewItem
import com.thesis.sportologia.ui.services.entities.ServiceViewItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceAcquiredViewModel @AssistedInject constructor(
    @Assisted private val serviceId: Long,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _serviceHolder = ObservableHolder<ServiceDetailedViewItem>(DataHolder.loading())
    val serviceHolder = _serviceHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ErrorType>()
    val toastMessageEvent = _toastMessageEvent.share()

    private val localChanges = LocalChanges(_serviceHolder)

    init {
        getService()
    }

    fun getService() = viewModelScope.launch(Dispatchers.IO) {
        try {
            withContext(Dispatchers.Main) {
                _serviceHolder.value = DataHolder.loading()
            }
            val serviceDetailed = servicesRepository.getServiceDetailed(serviceId)
            withContext(Dispatchers.Main) {
                if (serviceDetailed != null) {
                    _serviceHolder.value =
                        DataHolder.ready(ServiceDetailedViewItem(serviceDetailed.copy()))
                } else {
                    _serviceHolder.value = DataHolder.error(Exception("no such service"))
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _serviceHolder.value = DataHolder.error(e)
            }
        }
    }

    fun acquireService() = viewModelScope.launch(Dispatchers.IO) {
        try {
            servicesRepository.acquireService(serviceId)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _toastMessageEvent.publishEvent(ErrorType.ACQUIRE_ERROR)
            }
        }
    }

    fun onToggleFavouriteFlag() {
        _serviceHolder.value?.onReady { serviceFullEvent ->

            if (localChanges.isLoading) return

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    withContext(Dispatchers.Main) {
                        localChanges.isLoading = true
                    }
                    val newIsFavourite =
                        !(localChanges.isFavouriteFlag ?: serviceFullEvent.isFavourite)
                    servicesRepository.setIsFavourite(
                        CurrentAccount().id,
                        serviceId,
                        newIsFavourite
                    )
                    withContext(Dispatchers.Main) {
                        localChanges.isFavouriteFlag = newIsFavourite
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _toastMessageEvent.publishEvent(ErrorType.FAVS_ERROR)
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        localChanges.isLoading = false
                    }
                }
            }
        }
    }

    enum class ErrorType {
        ACQUIRE_ERROR, FAVS_ERROR
    }

    class LocalChanges(val holder: ObservableHolder<ServiceDetailedViewItem>) {
        var isLoading = false

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
    }

    @AssistedFactory
    interface Factory {
        fun create(serviceId: Long): ServiceAcquiredViewModel
    }

}*/