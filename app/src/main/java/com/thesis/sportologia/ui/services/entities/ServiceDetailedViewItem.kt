package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.ExerciseDataEntity
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.UserType

data class ServiceDetailedViewItem(
    var serviceDetailedDataEntity: ServiceDetailedDataEntity,
) {
    val id: String get() = serviceDetailedDataEntity.id!!
    val name: String get() = serviceDetailedDataEntity.name
    val generalDescription: String get() = serviceDetailedDataEntity.generalDescription
    val authorId: String get() = serviceDetailedDataEntity.authorId
    val authorName: String get() = serviceDetailedDataEntity.authorName
    val authorType: UserType get() = serviceDetailedDataEntity.authorType
    val price: Float get() = serviceDetailedDataEntity.price
    val serviceType: ServiceType get() = serviceDetailedDataEntity.type
    val currency: String get() = serviceDetailedDataEntity.currency
    val profilePictureUrl: String? get() = serviceDetailedDataEntity.profilePictureUrl
    val categories: Map<String, Boolean> get() = serviceDetailedDataEntity.categories
    val acquiredNumber: Int get() = serviceDetailedDataEntity.acquiredNumber
    val reviewsNumber: Int get() = serviceDetailedDataEntity.reviewsNumber
    val rating: Float? get() = serviceDetailedDataEntity.rating
    val isFavourite: Boolean get() = serviceDetailedDataEntity.isFavourite
    val isAcquired: Boolean get() = serviceDetailedDataEntity.isAcquired
    val generalPhotosUrls: List<String> get() = serviceDetailedDataEntity.generalPhotosUrls
    val detailedDescription: String get() = serviceDetailedDataEntity.detailedDescription
    val detailedPhotosUrls: List<String> get() = serviceDetailedDataEntity.detailedPhotosUrls
    val exercises: List<ExerciseDataEntity> get() = serviceDetailedDataEntity.exerciseDataEntities
}