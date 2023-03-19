package com.thesis.sportologia.ui.users.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentListUsersHeaderBinding
import com.thesis.sportologia.model.users.entities.FilterParamsUsers

class UsersHeaderAdapterFollowers(
    fragment: Fragment,
    filterParamsUsers: FilterParamsUsers
) : UsersHeaderAdapter(fragment, filterParamsUsers) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListUsersHeaderBinding,
    ): Holder {
        return HolderFollowers(fragment, binding)
    }

    class HolderFollowers(
        fragment: Fragment,
        binding: FragmentListUsersHeaderBinding,
    ) : Holder(fragment, binding) {

        override val renderHeader: () -> Unit = {}

    }

}