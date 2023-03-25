package com.thesis.sportologia.ui


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.posts.CreateEditPostViewModel
import com.thesis.sportologia.ui.posts.ListPostsViewModel
import com.thesis.sportologia.ui.posts.entities.PostListItem
import com.thesis.sportologia.ui.services.entities.ServiceFullItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

/**class ServiceNotAcquiredViewModel2 @AssistedInject constructor(
    @Assisted private val serviceId: Long,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {
    
    private val _serviceHolder = ObservableHolder<ServiceFullItem>(DataHolder.loading())
    val serviceHolder = _serviceHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ErrorType>()
    val toastMessageEvent = _toastMessageEvent.share()

    init {
        init()
    }

    private fun init() = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            _serviceHolder.value = DataHolder.loading()
        }
        getService()
    }

    private suspend fun getService() {
        try {
            val service = servicesRepository.getService(serviceId)
            withContext(Dispatchers.Main) {
                if (service != null) {
                    _serviceHolder.value =
                        DataHolder.ready(ServiceFullItem(service.copy(), false))
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
        _serviceHolder.value?.onReady { servicerFullEvent ->

            if (servicerFullEvent.isLoading) return

            viewModelScope.launch {
                try {
                    withContext(Dispatchers.Main) {
                        servicerFullEvent.isLoading = true
                    }
                    servicesRepository.setIsFavourite(
                        CurrentAccount().id,
                        serviceId,
                        !servicerFullEvent.isFavourite
                    )
                    withContext(Dispatchers.Main) {
                        servicerFullEvent.isFavourite = true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _toastMessageEvent.publishEvent(ErrorType.FAVS_ERROR)
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        servicerFullEvent.isLoading = false
                    }
                }
            }

            //_serviceHolder.value = DataHolder.ready(servicerFullEvent.copy(is))
        }
    }

    enum class ErrorType {
        ACQUIRE_ERROR, FAVS_ERROR
    }


    @AssistedFactory
    interface Factory {
        fun create(serviceId: Long): ServiceNotAcquiredViewModel2
    }

}*/