package com.thesis.sportologia.model.services.entities

data class Exercise(
    var id: String?,
    var name: String,
    var description: String,
    var setsNumber: Int,
    var repsNumber: Int,
    var regularity: Map<String, Boolean>,
    var photosUris: List<String>,
) : java.io.Serializable {

    companion object {
        const val NULL = -2L
    }

    /*enum class Regularity {
        EVERYDAY, IN_A_DAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }*/
}