package com.thesis.sportologia.model.events.entities

import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.utils.Position

data class EventDataEntity(
    val id: String?,
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
    var isLiked: Boolean,
    var isFavourite: Boolean,
    var photosUrls: List<String>,
)