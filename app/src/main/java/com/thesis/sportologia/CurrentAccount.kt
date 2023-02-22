package com.thesis.sportologia

data class CurrentAccount(
    val id: Int = 1,
    val profilePictureUrl: String = "https://i.imgur.com/tGbaZCY.jpg",
    val isAthlete: Boolean = true,
    val userName: String = "Илья Вольф",
)