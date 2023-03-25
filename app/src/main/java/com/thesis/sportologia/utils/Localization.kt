package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.users.entities.UserType

class Localization {

    companion object {

        fun convertUserTypeEnumToLocalized(context: Context, userType: UserType): String {
            return when (userType) {
                UserType.ATHLETE -> context.getString(R.string.athlete)
                UserType.ORGANIZATION -> context.getString(R.string.organization)
            }
        }

        fun convertServiceTypeEnumToLocalized(context: Context, userType: Service.ServiceType): String {
            return when (userType) {
                Service.ServiceType.TRAINING_PROGRAM -> context.getString(R.string.service_training_program)
            }
        }

    }

}