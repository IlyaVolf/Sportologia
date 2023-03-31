package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R
import com.thesis.sportologia.model.events.entities.Event

class TrainingProgrammesCategories {

    companion object {

        val emptyCategoriesMap
            get() = hashMapOf(
                Pair(GAINING_MUSCLES_MASS, false),
                Pair(LOSING_WEIGHT, false),
                Pair(KEEPING_FORM, false),
                Pair(ELSE, false),
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

        fun convertEnumToCategory(
            context: Context?,
            categoryEnum: String
        ): String? {
            context ?: return null

            return when (categoryEnum) {
                GAINING_MUSCLES_MASS -> context.getString(R.string.tp_category_gaining_muscles_mass)
                LOSING_WEIGHT -> context.getString(R.string.tp_category_losing_weight)
                KEEPING_FORM -> context.getString(R.string.tp_category_keeping_form)
                ELSE -> context.getString(R.string.tp_category_else)
                else -> null
            }
        }

        const val GAINING_MUSCLES_MASS = "Gainng mMuscles mass"
        const val LOSING_WEIGHT = "Losing weight"
        const val KEEPING_FORM = "Keeping form"
        const val ELSE = "Else"
    }

}