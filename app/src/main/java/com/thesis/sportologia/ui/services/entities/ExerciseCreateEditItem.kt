package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.Exercise

data class ExerciseCreateEditItem(
    var id: Long?,
    var name: String?,
    var description: String?,
    var setsNumber: String?,
    var repsNumber: String?,
    var regularity: Map<String, Boolean>?,
    var photosUris: List<String>?,
) : java.io.Serializable {
    companion object {
        fun getEmptyInstance(): ExerciseCreateEditItem {
            return ExerciseCreateEditItem(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        }
    }
}

fun Exercise.toCreateEditItem(): ExerciseCreateEditItem {
    return ExerciseCreateEditItem(
        id = id,
        name = name,
        description = description,
        setsNumber = setsNumber.toString(),
        repsNumber = repsNumber.toString(),
        regularity = regularity,
        photosUris = photosUris
    )
}