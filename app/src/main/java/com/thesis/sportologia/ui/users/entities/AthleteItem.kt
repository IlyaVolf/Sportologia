package com.thesis.sportologia.ui.users.entities

import com.thesis.sportologia.model.users.entities.Athlete

data class AthleteItem(
    val athlete: Athlete,
    override var lastAction: LastAction,
) : UserItem(athlete) {
    val isMale: Boolean get() = athlete.isMale
}