package com.thesis.sportologia.data.events.entities

import com.thesis.sportologia.core.entities.Position
import com.thesis.sportologia.core.entities.UserType

data class CreateEventDataEntity(
    var description: String,
    var dateFrom: Long,
    var dateTo: Long?,
    var position: Position,
    var price: Float,
    var currency: String,
    var categories: Map<String, Boolean>,
    var photosUrls: List<String>,
)