package com.thesis.sportologia.model.services.entities

data class ExerciseFirestoreEntity(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var setsNumber: Int? = null,
    var repsNumber: Int? = null,
    var regularity: Map<String, Boolean>? = null,
    var photosUris: List<String> = mutableListOf(),
)
