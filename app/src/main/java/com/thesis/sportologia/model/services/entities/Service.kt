package com.thesis.sportologia.model.services.entities

import com.thesis.sportologia.model.users.entities.UserType

data class Service(
    val id: Long,
    val name: String,
    val type: ServiceType,
    var generalDescription: String,
    var authorId: String,
    var authorName: String,
    var authorType: UserType,
    var profilePictureUrl: String?,
    var acquiredNumber: Int,
    var reviewsNumber: Int,
    var rating: Float?,
    var price: Float,
    var currency: String,
    var categories: Map<String, Boolean>,
    var isFavourite: Boolean,
    var isAcquired: Boolean,
    var generalPhotosUrls: List<String>,
) {
    companion object {
        const val NULL = -2L
    }
}
