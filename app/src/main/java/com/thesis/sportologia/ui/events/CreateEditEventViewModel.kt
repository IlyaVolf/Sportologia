package com.thesis.sportologia.ui.events


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.events.entities.EventCreateEditItem
import com.thesis.sportologia.ui.events.entities.toCreateEditItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateEditEventViewModel @AssistedInject constructor(
    @Assisted private val eventId: Long?,
    private val eventsRepository: EventsRepository,
    logger: Logger
) : BaseViewModel(logger) {

    // TODO
    private val currentAccount = CurrentAccount()

    private var mode: Mode

    private val _eventHolder = ObservableHolder<Event?>(DataHolder.ready(null))
    val eventHolder = _eventHolder.share()

    private val _saveHolder = ObservableHolder<Unit>(DataHolder.init())
    val saveHolder = _saveHolder.share()

    private val _toastMessageEvent = MutableLiveEvent<ErrorType>()
    val toastMessageEvent = _toastMessageEvent.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    init {
        if (eventId == null) {
            mode = Mode.CREATE
        } else {
            mode = Mode.EDIT
            getEvent()
        }
    }

    fun onSaveButtonPressed(event: EventCreateEditItem) {
        if (!validateData(event)) {
            return
        }

        // check whether text has left the same
        var savedEvent: Event? = null
        _eventHolder.value!!.onReady {
            savedEvent = it
        }
        if (savedEvent?.toCreateEditItem() == event) {
            goBack()
        }

        val reformattedName = reformatText(event.name)
        val reformattedDescription = reformatText(event.description)

        lateinit var newEvent: Event
        when (mode) {
            Mode.CREATE ->
                newEvent = Event(
                    id = -1, // не тут надо создавать!
                    name = reformattedName,
                    description = reformattedDescription,
                    organizerId = currentAccount.id,
                    organizerName = currentAccount.userName,
                    isOrganizerAthlete = currentAccount.isAthlete,
                    profilePictureUrl = currentAccount.profilePictureUrl,
                    dateFrom = event.dateFrom!!,
                    dateTo = event.dateTo,
                    address = event.address,
                    price = event.priceString.toFloat(),
                    currency = event.currency,
                    categories = event.categories,
                    likesCount = 0,
                    isLiked = false,
                    isFavourite = false,
                    photosUrls = event.photosUrls,
                )
            Mode.EDIT ->
                _eventHolder.value!!.onReady {
                    newEvent =
                        it!!.copy(
                            name = reformattedName,
                            description = reformattedDescription,
                            dateFrom = event.dateFrom!!,
                            dateTo = event.dateTo,
                            address = event.address,
                            price = event.priceString.toFloat(),
                            currency = event.currency,
                            categories = event.categories,
                            photosUrls = event.photosUrls,
                        )
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.loading()
                }
                when (mode) {
                    Mode.CREATE -> eventsRepository.createEvent(newEvent)
                    Mode.EDIT -> eventsRepository.updateEvent(newEvent)
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

    fun getEvent() = viewModelScope.launch(Dispatchers.IO) {
        try {
            withContext(Dispatchers.Main) {
                _eventHolder.value = DataHolder.loading()
            }
            val event = eventsRepository.getEvent(eventId!!)
            withContext(Dispatchers.Main) {
                _eventHolder.value = DataHolder.ready(event)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _eventHolder.value = DataHolder.error(e)
            }
        }
    }

    private fun validateData(event: EventCreateEditItem): Boolean {
        if (!validateText(event.name, ErrorType.EMPTY_NAME)) {
            return false
        }
        if (!validateText(event.description, ErrorType.EMPTY_DESCRIPTION)) {
            return false
        }
        if (!validatePrice(event.priceString)) {
            return false
        }
        if (!validateDate(event.dateFrom, event.dateTo)) {
            return false
        }
        /* if (!validateText(newEventCreateEditItem.description, ErrorType.EMPTY_ADDRESS)) {
            return false
        }*/

        return true
    }

    private fun validateDate(dateFromMillis: Long?, dateToMillis: Long?): Boolean {
        if (dateFromMillis == null) {
            _toastMessageEvent.publishEvent(ErrorType.EMPTY_DATE)
            return false
        }

        if (dateToMillis != null) {
            if (dateFromMillis > dateToMillis) {
                _toastMessageEvent.publishEvent(ErrorType.INCORRECT_DATE)
                return false
            }
        }

        return true
    }

    private fun validateText(text: String, errorType: ErrorType): Boolean {
        // check whether the text is empty
        if (text == "") {
            _toastMessageEvent.publishEvent(errorType)
            return false
        }

        return true
    }

    private fun validatePrice(price: String): Boolean {
        // check whether the text is empty
        if (price == "") {
            _toastMessageEvent.publishEvent(ErrorType.EMPTY_PRICE)
            return false
        }
        if (price.toFloatOrNull() == null) {
            _toastMessageEvent.publishEvent(ErrorType.INCORRECT_PRICE)
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
        EMPTY_DESCRIPTION,
        EMPTY_PRICE,
        EMPTY_DATE,
        INCORRECT_PRICE,
        INCORRECT_DATE,
        EMPTY_ADDRESS
    }

    enum class Mode {
        CREATE,
        EDIT
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long?): CreateEditEventViewModel
    }

}