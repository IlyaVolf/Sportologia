package com.thesis.sportologia.data.events.entities

import android.location.Address
import com.thesis.sportologia.model.FilterParams

// TODO Parcelable
data class FilterParamsEvents(
    var categories: HashMap<String, Boolean>?,
    var distance: Int?,
    var sortBy: EventsSortBy,
    var dateFrom: Long?,
    var dateTo: Long?,
    var price: Float?,
    var address: Address?,
) : FilterParams {

    enum class EventsSortBy {
        Date, Distance, Popularity
    }

    companion object {
        fun newEmptyInstance(): FilterParamsEvents {
            return FilterParamsEvents(null, null, EventsSortBy.Date, null, null, null, null)
        }
    }
}
