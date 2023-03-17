package com.thesis.sportologia.ui.events.entities

import android.location.Address
import com.thesis.sportologia.model.events.entities.Event
import java.util.*

// TODO parcelable!
data class EventCreateEditItem(
    val name: String,
    var description: String,
    var dateFrom: Long?,
    var dateTo: Long?,
    var address: Address?, // TODO NON-NULL
    var priceString: String,
    var currency: String,
    var categories: Map<String, Boolean>,
    var photosUrls: List<String>?,
) : java.io.Serializable