package com.thesis.sportologia.model.services.entities

import com.thesis.sportologia.model.users.entities.UserType

data class ServiceDetailedDataEntity(
    val id: String?,
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
    var detailedDescription: String,
    var detailedPhotosUrls: List<String>,
    var exerciseDataEntities: List<ExerciseDataEntity>,
    var dateCreatedMillis: Long,
) {
    fun toGeneral(): ServiceDataEntity {
        return ServiceDataEntity(
            id,
            name,
            type,
            generalDescription,
            authorId,
            authorName,
            authorType,
            profilePictureUrl,
            acquiredNumber,
            reviewsNumber,
            rating,
            price,
            currency,
            categories,
            isFavourite,
            isAcquired,
            generalPhotosUrls,
            dateCreatedMillis
        )
    }

}
