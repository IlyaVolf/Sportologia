package com.thesis.sportologia.utils

import android.content.Context
import com.thesis.sportologia.R
import com.thesis.sportologia.model.users.entities.UserType

class Localization {

    companion object {

        fun convertUserTypeEnumToLocalized(context: Context, userType: UserType): String {
            return when (userType) {
                UserType.ATHLETE -> context.getString(R.string.athlete)
                UserType.ORGANIZATION -> context.getString(R.string.organization)
            }
        }

    }

}