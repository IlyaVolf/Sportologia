package com.thesis.sportologia.ui.users.adapters

import androidx.fragment.app.Fragment

class UsersHeaderAdapterUsers(
    fragment: Fragment,
) : UsersHeaderAdapter(fragment) {

    override val renderHeader = {
        enableUsersChosenFilters()
    }

}