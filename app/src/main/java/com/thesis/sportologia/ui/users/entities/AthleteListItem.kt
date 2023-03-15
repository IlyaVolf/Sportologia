package com.thesis.sportologia.ui.users.entities

import com.thesis.sportologia.model.users.entities.Athlete

data class AthleteListItem(
    val athlete: Athlete,
) : UserListItem(athlete) {
    val isMale: Boolean get() = athlete.isMale
}