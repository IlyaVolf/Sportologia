package com.thesis.sportologia.model.services.entities

import com.thesis.sportologia.model.FilterParams

// TODO Parcelable
data class FilterParamsServices(
    var categories: HashMap<String, Boolean>?,
    var sortBy: ServicesSortBy,
    var serviceType: ServiceType?,
    var ratingFrom: Float?,
    var priceFrom: Float?,
) : FilterParams {

    enum class ServicesSortBy {
        Price, Rating, Popularity
    }

    companion object {
        fun newEmptyInstance(): FilterParamsServices {
            return FilterParamsServices(null, ServicesSortBy.Rating, null, null, null)
        }
    }
}
