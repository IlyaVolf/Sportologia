package com.thesis.sportologia.ui.users.entities

import com.thesis.sportologia.model.users.entities.Athlete

data class AthleteItem(
    val athlete: Athlete,
) : UserItem(athlete) {
    val isMale: Boolean get() = athlete.isMale
}