package com.thesis.sportologia.ui.events


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.events.EventsRepository
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**class CreateEditEventViewModel @AssistedInject constructor(
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

    fun onSaveButtonPressed(text: String, photosUrls: List<String>) {
        if (!validateText(text)) {
            return
        }

        // check whether text has left the same
        var event: Event? = null
        _eventHolder.value!!.onReady {
            event = it
        }
        if (text == event?.text) {
            goBack()
        }

        val reformattedText = reformatText(text)

        lateinit var newEvent: Event
        when (mode) {
            Mode.CREATE ->
                newEvent = Event(
                    id = -1, // не тут надо создавать!
                    authorName = currentAccount.userName,
                    authorId = currentAccount.id,
                    profilePictureUrl = currentAccount.profilePictureUrl,
                    text = reformattedText,
                    likesCount = 0,
                    isAuthorAthlete = currentAccount.isAthlete,
                    isLiked = false,
                    isFavourite = false,
                    eventedDate = Calendar.getInstance().timeInMillis, // по идее в самом коцне надо создавать!
                    photosUrls = photosUrls
                )
            Mode.EDIT ->
                _eventHolder.value!!.onReady {
                    newEvent = it!!.copy(text = reformattedText, photosUrls = photosUrls)
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

    private fun validateText(text: String): Boolean {
        // check whether the text is empty
        if (text == "") {
            _toastMessageEvent.publishEvent(ErrorType.EMPTY_POST)
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
        EMPTY_POST
    }

    enum class Mode {
        CREATE,
        EDIT
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long?): CreateEditEventViewModel
    }


}*/