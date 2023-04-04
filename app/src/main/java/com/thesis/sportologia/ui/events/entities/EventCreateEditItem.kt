package com.thesis.sportologia.ui.events.entities

import android.location.Address
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.services.entities.ServiceCreateEditItem
import java.util.*

// TODO parcelable!
data class EventCreateEditItem(
    var name: String?,
    var description: String?,
    var dateFrom: Long?,
    var dateTo: Long?,
    var address: Address?,
    var priceString: String?,
    var currency: String?,
    var categories: Map<String, Boolean>?,
    var photosUrls: MutableList<String>,
)  : java.io.Serializable {
    companion object {
        fun getEmptyInstance(): EventCreateEditItem {
            return EventCreateEditItem(
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

fun Event.toCreateEditItem(): EventCreateEditItem {
    return EventCreateEditItem(
        name,
        description,
        dateFrom,
        dateTo,
        address,
        price.toString(),
        currency,
        categories,
        photosUrls.toMutableList()
    )
}