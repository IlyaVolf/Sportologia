package com.thesis.sportologia.data.events.entities

import com.thesis.sportologia.core.entities.Position
import com.thesis.sportologia.core.entities.UserType

data class EventDataEntity(
    val id: Long,
    val name: String,
    var description: String,
    var organizerId: String,
    var organizerName: String,
    var userType: UserType,
    var profilePictureUrl: String?,
    var dateFrom: Long,
    var dateTo: Long?,
    var position: Position,
    var price: Float,
    var currency: String,
    var categories: Map<String, Boolean>,
    var likesCount: Int,
    var postedDate: Long?,
    var isLiked: Boolean,
    var isFavourite: Boolean,
    var photosUrls: List<String>,
)