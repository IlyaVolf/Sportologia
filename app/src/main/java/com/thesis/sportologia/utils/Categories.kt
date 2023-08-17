package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R

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

        fun getCategoriesFromLocalized(
            context: Context,
            hashMap: Map<String, Boolean>
        ): Map<String, Boolean> {

            val localizedHashMap = hashMapOf<String, Boolean>()

            hashMap.forEach {
                localizedHashMap[convertCategoryToEnum(context, it.key)!!] = it.value
            }

            return localizedHashMap
        }

        fun convertCategoryToEnum(
            context: Context?,
            category: String
        ): String? {
            context ?: return null

            return when (category) {
                context.getString(R.string.category_running) -> RUNNING
                context.getString(R.string.category_master_classes) -> MASTER_CLASS
                context.getString(R.string.category_martial_arts) -> MARTIAL_ARTS
                else -> null
            }
        }

        const val RUNNING = "RUNNING"
        const val MASTER_CLASS = "MASTER_CLASS"
        const val MARTIAL_ARTS = "MARTIAL_ARTS"
    }

}