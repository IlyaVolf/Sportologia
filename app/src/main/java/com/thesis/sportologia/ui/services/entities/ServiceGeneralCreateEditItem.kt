package com.thesis.sportologia.ui.services.entities

import com.thesis.sportologia.model.services.entities.ServiceType

// TODO parcelable!
data class ServiceGeneralCreateEditItem(
    var name: String,
    var generalDescription: String,
    var priceString: String,
    var currency: String,
    var categories: Map<String, Boolean>,
    var type: ServiceType,
    var generalPhotosUris: List<String>,
) : java.io.Serializable {
}

/*fun ServiceDetailed.toServiceGeneralCreateEditItem(): ServiceGeneralCreateEditItem {
    return ServiceGeneralCreateEditItem(
        name = this.name,
        generalDescription = generalDescription,
        priceString = price.toString(),
        currency = currency,
        categories = categories,
        type = type,
        generalPhotosUris = generalPhotosUrls
    )
}*/