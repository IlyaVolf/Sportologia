package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R
import com.thesis.sportologia.model.events.entities.Event

fun convertEnumToCategory(context: Context?, categoryEnum: String): String? {
    context ?: return null

    return when (categoryEnum) {
        Event.RUNNING -> context.getString(R.string.category_running)
        Event.MARTIAL_ARTS -> context.getString(R.string.category_martial_arts)
        Event.MASTER_CLASS -> context.getString(R.string.category_master_classes)
        else -> null
    }
}