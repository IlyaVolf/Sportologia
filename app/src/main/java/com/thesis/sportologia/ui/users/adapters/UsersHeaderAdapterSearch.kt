package com.thesis.sportologia.ui.users.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.ui.FilterFragmentUsers
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.concatMap

class UsersHeaderAdapterSearch(
    fragment: Fragment,
    filterParamsUsers: FilterFragmentUsers.FilterParamsUsers
) : UsersHeaderAdapter(fragment) {

    private val restrictionsParser: () -> Unit = {
        val restrictionsStringBuilder = StringBuilder("")

        if (filterParamsUsers.categories != null) {
            val localizedCategories = Categories.getLocalizedCategories(
                fragment.context!!,
                filterParamsUsers.categories!!
            )

            restrictionsStringBuilder.append(concatMap(localizedCategories, ", "))
            restrictionsStringBuilder.append(fragment.context!!.getString(R.string.split_dot))
        }

        if (filterParamsUsers.distance != null) {
            restrictionsStringBuilder.append(fragment.context!!.getString(R.string.within))
                .append(" ")
                .append(filterParamsUsers.distance)
                .append("")
                .append(fragment.context!!.getString(R.string.km))
            restrictionsStringBuilder.append(fragment.context!!.getString(R.string.split_dot))
        }

        if (filterParamsUsers.address != null) {
            restrictionsStringBuilder.append(filterParamsUsers.address)
        }

        val restrictionsString = restrictionsStringBuilder.toString()
        if (restrictionsString == "") {
            binding.usersChosenFilters.restrictions.text =
                fragment.context!!.getString(R.string.filter_not_specified)
        } else {
            binding.usersChosenFilters.restrictions.text = restrictionsString
        }
    }

    private val sortingParser: () -> Unit = { }

    override val renderHeader = {
        enableUsersChosenFilters(restrictionsParser, sortingParser)
    }

}