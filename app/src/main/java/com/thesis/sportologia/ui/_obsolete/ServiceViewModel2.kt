package com.thesis.sportologia.ui._obsolete


/**class ServiceViewModel2 @AssistedInject constructor(
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

    private fun setProgress(serviceId: Long, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(serviceId)
        } else {
            localChanges.idsInProgress.remove(serviceId)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun setFavouriteFlag(serviceId: Long, isFavourite: Boolean) {
        localChanges.isFavouriteFlags[serviceId] = isFavourite

        _serviceHolder.value?.onReady {
            _serviceHolder.value = DataHolder.READY(
                it.copy(
                    serviceDetailed = it.serviceDetailed.copy(
                        isFavourite = isFavourite
                    )
                )
            )
        }
    }

    private fun isInProgress(serviceListItemId: Long) =
        localChanges.idsInProgress.contains(serviceListItemId)

    fun acquireService() {
        if (isInProgress(serviceId)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    setProgress(serviceId, true)
                }
                servicesRepository.acquireService(serviceId)
                withContext(Dispatchers.Main) {
                    _toastMessageEvent.publishEvent(ResponseType.ACQUIRED_SUCCESSFULLY)
                    //localChanges.isAcquiredFlag = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _toastMessageEvent.publishEvent(ResponseType.ACQUIRE_ERROR)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    setProgress(serviceId, false)
                }
            }
        }
    }

    fun onToggleFavouriteFlag() {
        _serviceHolder.value?.onReady { serviceFullEvent ->

            if (isInProgress(serviceId)) return

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    withContext(Dispatchers.Main) {
                        setProgress(serviceId, true)
                    }
                    val newIsFavourite =
                        !(localChanges.isFavouriteFlags[serviceId] ?: serviceFullEvent.isFavourite)
                    servicesRepository.setIsFavourite(
                        CurrentAccount().id,
                        serviceId,
                        newIsFavourite
                    )
                    withContext(Dispatchers.Main) {
                        setFavouriteFlag(serviceId, newIsFavourite)
                        localChangesFlow.value = OnChange(localChanges)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _toastMessageEvent.publishEvent(ResponseType.FAVS_ERROR)
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        setProgress(serviceId, false)
                    }
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
        fun create(serviceId: Long): ServiceViewModel2
    }

}*/