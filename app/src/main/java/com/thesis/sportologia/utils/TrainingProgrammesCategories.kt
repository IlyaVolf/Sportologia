package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R

class TrainingProgrammesCategories {

    companion object {

        val emptyCategoriesAssociativeList
            get() = AssociativeList(
                listOf(
                    Pair(GAINING_MUSCLES_MASS, false),
                    Pair(LOSING_WEIGHT, false),
                    Pair(KEEPING_FORM, false),
                    Pair(ELSE, false)
                ),
            )


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
                context.getString(R.string.tp_category_gaining_muscles_mass) -> GAINING_MUSCLES_MASS
                context.getString(R.string.tp_category_losing_weight) -> LOSING_WEIGHT
                context.getString(R.string.tp_category_keeping_form) -> KEEPING_FORM
                context.getString(R.string.tp_category_else) -> ELSE
                else -> null
            }
        }

        const val GAINING_MUSCLES_MASS = "GAINING_MUSCLES_MASS"
        const val LOSING_WEIGHT = "LOSING_WEIGHT"
        const val KEEPING_FORM = "KEEPING_FORM"
        const val ELSE = "ELSE"
    }

}