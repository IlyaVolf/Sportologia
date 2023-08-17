package com.thesis.sportologia.ui


import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.OnChange
import com.thesis.sportologia.model.services.ServicesRepository
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.services.entities.ExerciseViewItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ExerciseViewModel @AssistedInject constructor(
    @Assisted("serviceId") private val serviceId: String,
    @Assisted("exerciseId") private val exerciseId: String,
    private val servicesRepository: ServicesRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val _exerciseHolder = ObservableHolder<ExerciseViewItem>(DataHolder.loading())
    val exerciseHolder = _exerciseHolder.share()

    init {
        getExercise()
    }

    fun getExercise() {
        viewModelScope.launch {
            try {
                _exerciseHolder.value = DataHolder.loading()
                val exercise = servicesRepository.getExercise(serviceId, exerciseId, CurrentAccount().id)
                if (exercise != null) {
                    _exerciseHolder.value =
                        DataHolder.ready(ExerciseViewItem(exercise.copy()))
                } else {
                    _exerciseHolder.value = DataHolder.error(Exception("no such exercise"))
                }
            } catch (e: Exception) {
                _exerciseHolder.value = DataHolder.error(e)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("serviceId") serviceId: String,
            @Assisted("exerciseId") exerciseId: String
        ): ExerciseViewModel
    }

}