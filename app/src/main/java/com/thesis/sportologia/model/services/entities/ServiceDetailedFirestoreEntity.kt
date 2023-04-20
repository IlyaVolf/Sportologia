package com.thesis.sportologia.model.services.entities

import android.app.Service
import com.thesis.sportologia.model.users.entities.UserType

data class ServiceDetailedFirestoreEntity(
    var id: String? = null,
    var name: String? = null,
    var type: Service? = null,
    var generalDescription: String? = null,
    var detailedDescription: String? = null,
    var authorId: String? = null,
    var authorType: UserType? = null,
    var acquiredNumber: Int? = null,
    var reviewsNumber: Int? = null,
    var rating: Float? = null,
    var price: Float? = null,
    var currency: String? = null,
    var categories: Map<String, Boolean>? = null,
    var postedDate: Long? = null,
    var generalPhotosUrls: List<String> = mutableListOf(),
    var detailedPhotosUrls: List<String> = mutableListOf(),
    var usersIdsAcquired: List<String> = mutableListOf(),
    var usersIdsFavs: List<String> = mutableListOf(),
)
