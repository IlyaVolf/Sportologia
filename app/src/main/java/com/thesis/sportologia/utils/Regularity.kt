package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R

class Regularity {

    companion object {

        val emptyRegularities
            get() = hashMapOf(
                Pair(EVERYDAY, false),
                Pair(IN_A_DAY, false),
                Pair(MONDAY, false),
                Pair(TUESDAY, false),
                Pair(WEDNESDAY, false),
                Pair(THURSDAY, false),
                Pair(FRIDAY, false),
                Pair(SATURDAY, false),
                Pair(SUNDAY, false),
            )

        fun getLocalizedRegularities(
            context: Context,
            hashMap: Map<String, Boolean>
        ): Map<String, Boolean> {

            val localizedHashMap = hashMapOf<String, Boolean>()

            hashMap.forEach {
                localizedHashMap[convertEnumToRegularity(context, it.key)!!] = it.value
            }

            return localizedHashMap
        }

        fun convertEnumToRegularity(context: Context?, categoryEnum: String): String? {
            context ?: return null

            return when (categoryEnum) {
                EVERYDAY -> context.getString(R.string.exercise_everyday)
                IN_A_DAY -> context.getString(R.string.exercise_in_a_day)
                MONDAY -> context.getString(R.string.mon_short)
                TUESDAY -> context.getString(R.string.tue_short)
                WEDNESDAY -> context.getString(R.string.wen_short)
                THURSDAY -> context.getString(R.string.thu_short)
                FRIDAY -> context.getString(R.string.fri_short)
                SATURDAY -> context.getString(R.string.sat_short)
                SUNDAY -> context.getString(R.string.sun_short)
                else -> null
            }
        }

        fun getRegularitiesFromLocalized(
            context: Context,
            hashMap: Map<String, Boolean>
        ): Map<String, Boolean> {

            val localizedHashMap = hashMapOf<String, Boolean>()

            hashMap.forEach {
                localizedHashMap[convertRegularityToEnum(context, it.key)!!] = it.value
            }

            return localizedHashMap
        }

        fun convertRegularityToEnum(
            context: Context?,
            category: String
        ): String? {
            context ?: return null

            return when (category) {
                context.getString(R.string.exercise_everyday) -> EVERYDAY
                context.getString(R.string.exercise_in_a_day) -> IN_A_DAY
                context.getString(R.string.mon_short) -> MONDAY
                context.getString(R.string.tue_short) -> TUESDAY
                context.getString(R.string.wen_short) -> WEDNESDAY
                context.getString(R.string.thu_short) -> THURSDAY
                context.getString(R.string.fri_short) -> FRIDAY
                context.getString(R.string.sat_short) -> SATURDAY
                context.getString(R.string.sun_short) -> SUNDAY
                else -> null
            }
        }
        
        const val EVERYDAY = "RUNNING"
        const val IN_A_DAY = "IN_A_DAY"
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
    }
    
}