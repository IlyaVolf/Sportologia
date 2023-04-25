package com.thesis.sportologia.ui.entities

import android.location.Address
import com.thesis.sportologia.model.users.entities.GenderType
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.Position

data class ProfileSettingsViewItem(
    val accountType: UserType?,
    val name: String?,
    val nickname: String?,
    val description: String?,
    val gender: GenderType?,
    val birthDate: Long?,
    val profilePhotoUri: String?,
    val categories: Map<String, Boolean>?,
    val position: Position?
)