package com.thesis.sportologia

import com.thesis.sportologia.model.users.entities.UserType

data class CurrentAccount(
    val id: String = "i_volf",
    val profilePictureUrl: String = "https://i.imgur.com/tGbaZCY.jpg",
    val isAthlete: Boolean = true,
    val userName: String = "Илья Вольф",
    val userType: UserType = UserType.ATHLETE
)