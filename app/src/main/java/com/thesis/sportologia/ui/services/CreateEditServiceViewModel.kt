package com.thesis.sportologia.ui.services


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.model.services.entities.ServiceDetailed
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.events.CreateEditEventViewModel
import com.thesis.sportologia.ui.services.entities.ServiceCreateEditItem
import com.thesis.sportologia.ui.services.entities.ServiceGeneralCreateEditItem
import com.thesis.sportologia.ui.services.entities.toCreateEditItem

import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CreateEditServiceViewModel @AssistedInject constructor(
    @Assisted private val serviceId: Long?,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    // TODO
    private val currentAccount = CurrentAccount()

    private var mode: Mode

    private val _serviceHolder = ObservableHolder<ServiceDetailed?>(DataHolder.ready(null))
    val serviceHolder = _serviceHolder.share()

    private val _saveHolder = ObservableHolder<Unit>(DataHolder.init())
    val saveHolder = _saveHolder.share()

    private val _toastMessageService = MutableLiveEvent<ErrorType>()
    val toastMessageService = _toastMessageService.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    init {
        if (serviceId == null) {
            mode = Mode.CREATE
        } else {
            mode = Mode.EDIT
            getService()
        }
    }

    fun onSaveButtonPressed(service: ServiceCreateEditItem) {
        if (!validateData(service)) {
            return
        }

        // check whether text has left the same
        var savedService: ServiceDetailed? = null
        _serviceHolder.value!!.onReady {
            savedService = it
        }
        if (savedService?.toCreateEditItem() == service) {
            goBack()
        }

        val reformattedName = reformatText(service.name!!)
        val reformattedGeneralDescription = reformatText(service.generalDescription!!)
        val reformattedDetailedDescription = reformatText(service.detailedDescription!!)

        lateinit var newService: ServiceDetailed
        when (mode) {
            Mode.CREATE ->
                newService = ServiceDetailed(
                    id = -1, // не тут надо создавать!
                    name = reformattedName,
                    type = service.type!!,
                    generalDescription = reformattedGeneralDescription,
                    authorId = currentAccount.id,
                    authorName = currentAccount.userName,
                    authorType = currentAccount.userType,
                    profilePictureUrl = currentAccount.profilePictureUrl,
                    acquiredNumber = 0,
                    reviewsNumber = 0,
                    rating = null,
                    price = service.priceString!!.toFloat(),
                    currency = service.currency!!,
                    categories = service.categories!!,
                    isFavourite = false,
                    isAcquired = true,
                    generalPhotosUrls = service.generalPhotosUrls ?: listOf(),
                    detailedDescription = reformattedDetailedDescription,
                    detailedPhotosUrls = service.detailedPhotosUrls ?: listOf(),
                    exercises = listOf(),
                    dateCreatedMillis = Calendar.getInstance().timeInMillis
                )
            Mode.EDIT ->
                _serviceHolder.value!!.onReady {
                    newService =
                        it!!.copy(
                            name = reformattedName,
                            generalDescription = reformattedGeneralDescription,
                            type = service.type!!,
                            price = service.priceString!!.toFloat(),
                            currency = service.currency!!,
                            categories = service.categories!!,
                            generalPhotosUrls = service.generalPhotosUrls ?: listOf(),
                            detailedDescription = reformattedDetailedDescription,
                            detailedPhotosUrls = service.detailedPhotosUrls ?: listOf(),
                            exercises = listOf(),
                        )
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.loading()
                }
                when (mode) {
                    Mode.CREATE -> servicesRepository.createService(newService)
                    Mode.EDIT -> servicesRepository.updateService(newService)
                }
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.ready(Unit)
                    goBack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.error(e)
                }
            }
        }
    }

    fun getService() = viewModelScope.launch(Dispatchers.IO) {
        try {
            withContext(Dispatchers.Main) {
                _serviceHolder.value = DataHolder.loading()
            }
            val service = servicesRepository.getServiceDetailed(serviceId!!)
            withContext(Dispatchers.Main) {
                _serviceHolder.value = DataHolder.ready(service)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _serviceHolder.value = DataHolder.error(e)
            }
        }
    }

    private fun validateData(service: ServiceCreateEditItem): Boolean {
        if (!validateText(service.name, ErrorType.EMPTY_NAME)) {
            return false
        }
        if (!validateText(service.generalDescription, ErrorType.EMPTY_GENERAL_DESCRIPTION)) {
            return false
        }
        if (!validateText(service.detailedDescription, ErrorType.EMPTY_DETAILED_DESCRIPTION)) {
            return false
        }
        if (!validatePrice(service.priceString)) {
            return false
        }
        if (!validateCurrency(service.currency)) {
            return false
        }
        if (!validateType(service.type)) {
            return false
        }

        return true
    }

    private fun validateText(text: String?, errorType: ErrorType): Boolean {
        // check whether the text is empty
        if (text == null || text == "") {
            _toastMessageService.publishEvent(errorType)
            return false
        }

        return true
    }

    private fun validatePrice(price: String?): Boolean {
        // check whether the text is empty
        if (price == null || price == "") {
            _toastMessageService.publishEvent(ErrorType.EMPTY_PRICE)
            return false
        }
        if (price.toFloatOrNull() == null) {
            _toastMessageService.publishEvent(ErrorType.INCORRECT_PRICE)
            return false
        }

        return true
    }

    private fun validateType(serviceType: ServiceType?): Boolean {
        if (serviceType == null) {
            _toastMessageService.publishEvent(ErrorType.EMPTY_TYPE)
            return false
        }
        return true
    }

    private fun validateCurrency(currency: String?): Boolean {
        if (currency == null) {
            _toastMessageService.publishEvent(ErrorType.EMPTY_CURRENCY)
            return false
        }
        return true
    }

    private fun reformatText(text: String): String {
        val newText = StringBuilder()

        // remove empty strings at the start and at the end of the text
        newText.append(removeEmptyStrings(text))

        return newText.toString()
    }

    private fun goBack() = _goBackEvent.publishEvent()

    enum class ErrorType {
        EMPTY_NAME,
        EMPTY_GENERAL_DESCRIPTION,
        EMPTY_DETAILED_DESCRIPTION,
        EMPTY_PRICE,
        EMPTY_TYPE,
        EMPTY_CURRENCY,
        INCORRECT_PRICE,
    }

    enum class Mode {
        CREATE,
        EDIT
    }

    @AssistedFactory
    interface Factory {
        fun create(serviceId: Long?): CreateEditServiceViewModel
    }

}