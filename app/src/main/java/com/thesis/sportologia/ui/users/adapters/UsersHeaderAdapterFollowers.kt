package com.thesis.sportologia.ui.users.adapters

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

class UsersHeaderAdapterFollowers(
    fragment: Fragment,
) : UsersHeaderAdapter(fragment) {

    override val renderHeader = {
        binding.usersChosenFilters.root.isVisible = false
        binding.usersChosenFiltersSpace.isVisible = false
    }

}