package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R
import com.thesis.sportologia.model.services.entities.Exercise
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.UserType

class Localization {

    companion object {

        fun convertUserTypeEnumToLocalized(context: Context, userType: UserType): String {
            return when (userType) {
                UserType.ATHLETE -> context.getString(R.string.athlete)
                UserType.ORGANIZATION -> context.getString(R.string.organization)
            }
        }

        fun convertServiceTypeEnumToLocalized(context: Context, userType: ServiceType): String {
            return when (userType) {
                ServiceType.TRAINING_PROGRAM -> context.getString(R.string.service_training_program)
            }
        }

        fun convertServiceTypeLocalizedToEnum(context: Context, localized: String): ServiceType? {
            return when (localized) {
                context.getString(R.string.service_training_program) -> ServiceType.TRAINING_PROGRAM
                else -> null
            }
        }

        fun convertExerciseRegularityEnumToLocalized(context: Context, regularity: Exercise.Regularity): String {
            return when (regularity) {
                Exercise.Regularity.EVERYDAY -> context.getString(R.string.exercise_everyday)
                Exercise.Regularity.IN_A_DAY -> context.getString(R.string.exercise_in_a_day)
                Exercise.Regularity.MONDAY -> context.getString(R.string.mon_short)
                Exercise.Regularity.TUESDAY -> context.getString(R.string.tue_short)
                Exercise.Regularity.WEDNESDAY -> context.getString(R.string.wen_short)
                Exercise.Regularity.THURSDAY -> context.getString(R.string.thu_short)
                Exercise.Regularity.FRIDAY -> context.getString(R.string.fri_short)
                Exercise.Regularity.SATURDAY -> context.getString(R.string.sat_short)
                Exercise.Regularity.SUNDAY -> context.getString(R.string.sun_short)
            }
        }

    }

}