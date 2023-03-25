package com.thesis.sportologia.model.services.entities

data class Exercise(
    var id: Long,
    var name: String,
    var description: String,
    var setsNumber: Int,
    var repsNumber: Int,
    var regularity: List<Regularity>,
    var photosUris: List<String>,
) {
    enum class Regularity {
        EVERYDAY, IN_A_DAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
}