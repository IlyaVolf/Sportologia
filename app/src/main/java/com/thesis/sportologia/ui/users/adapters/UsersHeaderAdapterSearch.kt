package com.thesis.sportologia.ui.users.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.ui.FilterFragmentUsers
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.concatList
import com.thesis.sportologia.utils.concatMap

class UsersHeaderAdapterSearch(
    fragment: Fragment,
    filterParamsUsers: FilterFragmentUsers.FilterParamsUsers
) : UsersHeaderAdapter(fragment) {

    override val restrictionsParser: () -> Unit = {
        val restrictionBlockList = mutableListOf<String>()

        if (filterParamsUsers.categories != null) {
            val restrictionBlock = StringBuilder("")
            val localizedCategories = Categories.getLocalizedCategories(
                fragment.context!!,
                filterParamsUsers.categories!!
            )

            restrictionBlock.append(concatMap(localizedCategories, ", "))

            restrictionBlockList.add(restrictionBlock.toString())
        }

        if (filterParamsUsers.distance != null) {
            val restrictionBlock = StringBuilder("")

            restrictionBlock.append(fragment.getString(R.string.within))
                .append(" ")
                .append(filterParamsUsers.distance)
                .append(" ")
                .append(fragment.getString(R.string.km))

            restrictionBlockList.add(restrictionBlock.toString())
        }

        if (filterParamsUsers.address != null) {
            val restrictionBlock = StringBuilder("")

            restrictionBlock.append(filterParamsUsers.address)

            restrictionBlockList.add(restrictionBlock.toString())
        }

        val restrictionsString = concatList(restrictionBlockList, splittingDot)
        if (restrictionsString == "") {
            binding.usersChosenFilters.restrictions.text =
                fragment.getString(R.string.filter_not_specified)
        } else {
            binding.usersChosenFilters.restrictions.text = restrictionsString
        }
    }

    override val sortingParser: () -> Unit = {
        binding.usersChosenFilters.sorting.text =
            fragment.getString(R.string.filter_sorting_relevance)
    }

    private val splittingDot = " " + fragment.context!!.getString(R.string.split_dot) + " "

    override val renderHeader = {
        enableUsersChosenFilters()
    }

}