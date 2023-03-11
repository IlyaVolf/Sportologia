package com.thesis.sportologia.model.events.entities

import android.location.Address
import java.util.*

data class Event(
    val id: Long,
    var organizerId: String,
    var organizerName: String,
    var isOrganizerAthlete: Boolean,
    var profilePictureUrl: String?,
    var dateFrom: Calendar,
    var dateTo: Calendar,
    var address: Address?, // TODO NON-NULL
    var price: Float,
    var currency: String,
    var categories: Map<String, Boolean>,
    var description: String,
    var likesCount: Int,
    var isLiked: Boolean,
    var isFavourite: Boolean,
    var photosUrls: List<String>?,
)