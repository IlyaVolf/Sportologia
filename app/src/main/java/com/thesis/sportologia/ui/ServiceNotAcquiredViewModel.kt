package com.thesis.sportologia.ui


import android.util.Log
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

class ServiceNotAcquiredViewModel @AssistedInject constructor(
    @Assisted private val serviceId: Long,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _serviceHolder = ObservableHolder<ServiceFullItem>(DataHolder.loading())
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

    class LocalChanges(val holder: ObservableHolder<ServiceFullItem>) {
        var isLoading = false

        var isFavouriteFlag: Boolean? = null
            set(value) {
                holder.value?.onReady {
                    holder.value = DataHolder.READY(it.copy(isFavourite = value ?: it.isFavourite))
                }
                field = value
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(serviceId: Long): ServiceNotAcquiredViewModel
    }

}