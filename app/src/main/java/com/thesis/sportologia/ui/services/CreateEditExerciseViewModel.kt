package com.thesis.sportologia.ui.services

import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.services.entities.ExerciseCreateEditItem

import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CreateEditExerciseViewModel @AssistedInject constructor(
    @Assisted private val exercise: ExerciseCreateEditItem?,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    // TODO
    private val currentAccount = CurrentAccount()

    private var mode: Mode

    private val _exerciseHolder = ObservableHolder<ExerciseCreateEditItem?>(DataHolder.init())
    val exerciseHolder = _exerciseHolder.share()

    private val _saveHolder = ObservableHolder<ExerciseCreateEditItem?>(DataHolder.init())
    val saveHolder = _saveHolder.share()

    private val _toastMessageService = MutableLiveEvent<ErrorType>()
    val toastMessageService = _toastMessageService.share()

    init {
        mode = if (exercise == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }
        getExercise()
    }

    fun onSaveButtonPressed(exercise: ExerciseCreateEditItem) {
        if (!validateData(exercise)) {
            return
        }

        val reformattedName = reformatText(exercise.name!!)
        val reformattedDescription = reformatText(exercise.description!!)

        lateinit var newExercise: ExerciseCreateEditItem
        when (mode) {
            Mode.CREATE ->
                newExercise = ExerciseCreateEditItem(
                    id = -1, // не тут надо создавать!
                    name = reformattedName,
                    description = reformattedDescription,
                    repsNumber = exercise.repsNumber!!,
                    setsNumber = exercise.setsNumber!!,
                    regularity = exercise.regularity ?: hashMapOf(),
                    photosUris = exercise.photosUris
                )
            Mode.EDIT ->
                _exerciseHolder.value!!.onReady {
                    newExercise =
                        it!!.copy(
                            name = reformattedName,
                            description = reformattedDescription,
                            repsNumber = exercise.repsNumber,
                            setsNumber = exercise.setsNumber,
                            regularity = exercise.regularity ?: hashMapOf(),
                            photosUris = exercise.photosUris
                        )
                }
        }

        _saveHolder.value = DataHolder.ready(newExercise)
    }

    fun getExercise() {
        _exerciseHolder.value = DataHolder.ready(exercise)
    }

    private fun validateData(exercise: ExerciseCreateEditItem): Boolean {
        if (!validateText(exercise.name, ErrorType.EMPTY_NAME)) {
            return false
        }
        if (!validateText(exercise.description, ErrorType.EMPTY_DESCRIPTION)) {
            return false
        }
        if (!validateInt(
                exercise.setsNumber,
                ErrorType.EMPTY_SETS_NUMBER,
                ErrorType.INCORRECT_SETS_NUMBER
            )
        ) {
            return false
        }
        if (!validateInt(
                exercise.repsNumber,
                ErrorType.EMPTY_REPS_NUMBER,
                ErrorType.INCORRECT_REPS_NUMBER
            )
        ) {
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

    private fun validateInt(
        number: String?,
        emptyErrorType: ErrorType,
        incorrectErrorType: ErrorType
    ): Boolean {
        // check whether the text is empty
        if (number == null || number == "") {
            _toastMessageService.publishEvent(emptyErrorType)
            return false
        }
        if (number.toIntOrNull() == null) {
            _toastMessageService.publishEvent(incorrectErrorType)
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

    enum class ErrorType {
        EMPTY_NAME,
        EMPTY_DESCRIPTION,
        EMPTY_SETS_NUMBER,
        EMPTY_REPS_NUMBER,
        INCORRECT_SETS_NUMBER,
        INCORRECT_REPS_NUMBER
    }

    enum class Mode {
        CREATE,
        EDIT
    }

    @AssistedFactory
    interface Factory {
        fun create(
            exercise: ExerciseCreateEditItem?
        ): CreateEditExerciseViewModel
    }

}