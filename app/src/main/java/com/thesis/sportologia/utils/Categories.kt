package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R
import com.thesis.sportologia.model.events.entities.Event

class Categories {

    companion object {

        val emptyCategoriesMap
            get() = hashMapOf(
                Pair(RUNNING, false),
                Pair(MASTER_CLASS, false),
                Pair(MARTIAL_ARTS, false)
            )

        fun getLocalizedCategories(
            context: Context,
            hashMap: Map<String, Boolean>
        ): Map<String, Boolean> {

            val localizedHashMap = hashMapOf<String, Boolean>()

            hashMap.forEach {
                localizedHashMap[convertEnumToCategory(context, it.key)!!] = it.value
            }

            return localizedHashMap
        }

        fun convertEnumToCategory(context: Context?, categoryEnum: String): String? {
            context ?: return null

            return when (categoryEnum) {
                RUNNING -> context.getString(R.string.category_running)
                MARTIAL_ARTS -> context.getString(R.string.category_martial_arts)
                MASTER_CLASS -> context.getString(R.string.category_master_classes)
                else -> null
            }
        }

        const val RUNNING = "Running"
        const val MASTER_CLASS = "Master class"
        const val MARTIAL_ARTS = "Martial arts"
    }

}