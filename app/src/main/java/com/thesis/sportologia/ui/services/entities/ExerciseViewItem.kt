package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.ExerciseDataEntity

data class ExerciseViewItem(
    var exerciseDataEntity: ExerciseDataEntity,
) {
    val id: String get() = exerciseDataEntity.id!!
    val name: String get() = exerciseDataEntity.name
    val description: String get() = exerciseDataEntity.description
    val setsNumber: Int get() = exerciseDataEntity.setsNumber
    val repsNumber: Int get() = exerciseDataEntity.repsNumber
    val regularity: Map<String, Boolean> get() = exerciseDataEntity.regularity
    val photosUris: List<String> get() = exerciseDataEntity.photosUris
}