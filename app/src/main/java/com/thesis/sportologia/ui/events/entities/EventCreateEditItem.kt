package com.thesis.sportologia.ui.events.entities

import android.location.Address
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.services.entities.ServiceCreateEditItem
import com.thesis.sportologia.utils.Position
import java.util.*

// TODO parcelable!
data class EventCreateEditItem(
    var name: String?,
    var description: String?,
    var dateFrom: Long?,
    var dateTo: Long?,
    var position: Position?,
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
        name = name,
        description = description,
        dateFrom = dateFrom,
        dateTo = dateTo,
        position = position,
        priceString = price.toString(),
        currency = currency,
        categories = categories,
        photosUrls = photosUrls.toMutableList()
    )
}