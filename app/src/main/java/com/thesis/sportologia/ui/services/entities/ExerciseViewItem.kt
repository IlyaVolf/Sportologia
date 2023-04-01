package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.Exercise

data class ExerciseViewItem(
    var exercise: Exercise,
) {
    val id: Long get() = exercise.id
    val name: String get() = exercise.name
    val description: String get() = exercise.description
    val setsNumber: Int get() = exercise.setsNumber
    val repsNumber: Int get() = exercise.repsNumber
    val regularity: Map<String, Boolean> get() = exercise.regularity
    val photosUris: List<String> get() = exercise.photosUris
}