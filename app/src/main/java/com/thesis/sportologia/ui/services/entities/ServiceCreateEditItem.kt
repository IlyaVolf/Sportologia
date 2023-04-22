package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.ExerciseDataEntity
import com.thesis.sportologia.model.services.entities.ServiceDetailedDataEntity
import com.thesis.sportologia.model.services.entities.ServiceType

// TODO parcelable!
data class ServiceCreateEditItem(
    var name: String?,
    var generalDescription: String?,
    var priceString: String?,
    var currency: String?,
    var categories: Map<String, Boolean>?,
    var type: ServiceType?,
    var generalPhotosUrls: List<String>?,
    var detailedDescription: String?,
    var detailedPhotosUrls: List<String>?,
    var exercises: MutableList<ExerciseDataEntity>,
) : java.io.Serializable {
    companion object {
        fun getEmptyInstance(): ServiceCreateEditItem {
            return ServiceCreateEditItem(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                mutableListOf()
            )
        }
    }
}

fun ServiceDetailedDataEntity.toCreateEditItem(): ServiceCreateEditItem {
    return ServiceCreateEditItem(
        name = name,
        generalDescription = generalDescription,
        priceString = price.toString(),
        currency = currency,
        categories = categories,
        type = type,
        generalPhotosUrls = generalPhotosUrls,
        detailedDescription = detailedDescription,
        detailedPhotosUrls = detailedPhotosUrls,
        exercises = exerciseDataEntities.toMutableList()
    )
}