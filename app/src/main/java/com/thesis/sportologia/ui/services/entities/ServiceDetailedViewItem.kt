package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.Exercise
import com.thesis.sportologia.model.services.entities.ServiceDetailed
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.UserType

data class ServiceDetailedViewItem(
    var serviceDetailed: ServiceDetailed,
) {
    val id: Long get() = serviceDetailed.id
    val name: String get() = serviceDetailed.name
    val generalDescription: String get() = serviceDetailed.generalDescription
    val authorId: String get() = serviceDetailed.authorId
    val authorName: String get() = serviceDetailed.authorName
    val authorType: UserType get() = serviceDetailed.authorType
    val price: Float get() = serviceDetailed.price
    val serviceType: ServiceType get() = serviceDetailed.type
    val currency: String get() = serviceDetailed.currency
    val profilePictureUrl: String? get() = serviceDetailed.profilePictureUrl
    val categories: Map<String, Boolean> get() = serviceDetailed.categories
    val acquiredNumber: Int get() = serviceDetailed.acquiredNumber
    val reviewsNumber: Int get() = serviceDetailed.reviewsNumber
    val rating: Float? get() = serviceDetailed.rating
    val isFavourite: Boolean get() = serviceDetailed.isFavourite
    val isAcquired: Boolean get() = serviceDetailed.isAcquired
    val generalPhotosUrls: List<String> get() = serviceDetailed.generalPhotosUrls
    val detailedDescription: String get() = serviceDetailed.detailedDescription
    val detailedPhotosUrls: List<String> get() = serviceDetailed.detailedPhotosUrls
    val exercises: List<Exercise> get() = serviceDetailed.exercises
}