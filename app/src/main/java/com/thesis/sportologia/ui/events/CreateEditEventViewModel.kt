package com.thesis.sportologia.ui.events


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.events.entities.EventCreateEditItem
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

    private val _saveHolder = ObservableHolder(DataHolder.ready(null))
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

    fun onSaveButtonPressed(newEventCreateEditItem: EventCreateEditItem) {
        if (!validateText(newEventCreateEditItem.name, ErrorType.EMPTY_NAME)) {
            return
        }
        if (!validateText(newEventCreateEditItem.description, ErrorType.EMPTY_DESCRIPTION)) {
            return
        }
        if (!validatePrice(newEventCreateEditItem.priceString)) {
            return
        }
        /*if (!validateText(newEventCreateEditItem.description, ErrorType.EMPTY_DATE_FROM)) {
            return
        }
        if (!validateText(newEventCreateEditItem.description, ErrorType.EMPTY_DATE_TO)) {
            return
        }
        if (!validateText(newEventCreateEditItem.description, ErrorType.EMPTY_ADDRESS)) {
            return
        }*/

        // check whether text has left the same
        var savedEvent: Event? = null
        _eventHolder.value!!.onReady {
            savedEvent = it
        }
        if (savedEvent?.toEventCreateEditItem() == newEventCreateEditItem) {
            goBack()
        }

        val reformattedName = reformatText(newEventCreateEditItem.name)
        val reformattedDescription = reformatText(newEventCreateEditItem.description)

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
                    dateFrom = newEventCreateEditItem.dateFrom,
                    dateTo = newEventCreateEditItem.dateTo,
                    address = newEventCreateEditItem.address,
                    price = newEventCreateEditItem.priceString.toFloat(),
                    currency = newEventCreateEditItem.currency,
                    categories = newEventCreateEditItem.categories,
                    likesCount = 0,
                    isLiked = false,
                    isFavourite = false,
                    photosUrls = newEventCreateEditItem.photosUrls,
                )
            Mode.EDIT ->
                _eventHolder.value!!.onReady {
                    newEvent =
                        it!!.copy(
                            name = reformattedName,
                            description = reformattedDescription,
                            dateFrom = newEventCreateEditItem.dateFrom,
                            dateTo = newEventCreateEditItem.dateTo,
                            address = newEventCreateEditItem.address,
                            price = newEventCreateEditItem.priceString.toFloat(),
                            currency = newEventCreateEditItem.currency,
                            categories = newEventCreateEditItem.categories,
                            photosUrls = newEventCreateEditItem.photosUrls,
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
                    _saveHolder.value = DataHolder.ready(null)
                    goBack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _saveHolder.value = DataHolder.error(e)
                }
            }
        }

        return
    }

    private fun getEvent() = viewModelScope.launch(Dispatchers.IO) {
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
        INCORRECT_PRICE,
        EMPTY_DATE_FROM,
        EMPTY_DATE_TO,
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