package com.thesis.sportologia.ui._obsolete


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.posts.CreateEditPostViewModel
import com.thesis.sportologia.ui.posts.ListPostsViewModel
import com.thesis.sportologia.ui.posts.entities.PostListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

/** class ServiceNotAcquiredViewModel @AssistedInject constructor(
    @Assisted private val serviceId: Long,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val search = MutableLiveData("")

    private val localChanges = LocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    private val _serviceHolder = ObservableHolder<ServiceFullItem>(DataHolder.loading())
    val serviceHolder = _serviceHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ErrorType>()
    val toastMessageEvent = _toastMessageEvent.share()

    val servicesFlow: Flow<DataHolder<ServiceFullItem>>

    init {
        init()
    }

    private fun init() = viewModelScope.launch(Dispatchers.IO) {
        val originServicesFlow = getService()

        servicesFlow = combine(
            originServicesFlow,
            localChangesFlow.debounce(50),
            ::merge
        )
        getService()
    }

    private suspend fun getService(): Flow<Service?> {
        return servicesRepository.getService(serviceId).asFlow()
            .flatMapLatest {
                servicesRepository.getService(serviceId)
            }.cachedIn(viewModelScope)
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
        val isFavouriteFlag = Boolean
    }

    @AssistedFactory
    interface Factory {
        fun create(serviceId: Long): ServiceNotAcquiredViewModel3
    }

} */