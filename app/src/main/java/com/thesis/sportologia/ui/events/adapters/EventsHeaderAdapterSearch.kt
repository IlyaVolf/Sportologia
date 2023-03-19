package com.thesis.sportologia.ui.events.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListEventsHeaderBinding
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.utils.*
import java.util.Calendar
import java.util.Currency

class EventsHeaderAdapterSearch(
    fragment: Fragment,
    val listener: FilterListener,
    filterParamsEvents: FilterParamsEvents,
) : EventsHeaderAdapter(fragment, listener, filterParamsEvents) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListEventsHeaderBinding
    ): Holder {
        return HolderSearch(fragment, listener, binding, filterParamsEvents)
    }

    class HolderSearch(
        fragment: Fragment,
        listener: FilterListener,
        binding: FragmentListEventsHeaderBinding,
        var filterParamsEvents: FilterParamsEvents,
    ) : Holder(fragment, binding, listener) {

        override val renderHeader: () -> Unit = {
            enableEventsChosenFilters(parser)
        }

        private val parser = {
            restrictionsParser()
            sortingParser()
        }

        val restrictionsParser: () -> Unit = {
            val restrictionBlockList = mutableListOf<String>()

            if (filterParamsEvents.categories != null) {
                val restrictionBlock = StringBuilder("")
                val localizedCategories = Categories.getLocalizedCategories(
                    fragment.context!!,
                    filterParamsEvents.categories!!
                )

                restrictionBlock.append(concatMap(localizedCategories, ", "))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsEvents.price != null) {
                val restrictionBlock = StringBuilder("")

                restrictionBlock.append(fragment.getString(R.string.under))
                    .append(" ")
                    .append(formatPrice(filterParamsEvents.price.toString()))
                    .append(" ")
                    .append(fragment.getString(R.string.ruble_abbreviation))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsEvents.distance != null) {
                val restrictionBlock = StringBuilder("")

                restrictionBlock.append(fragment.getString(R.string.within))
                    .append(" ")
                    .append(filterParamsEvents.distance)
                    .append(" ")
                    .append(fragment.getString(R.string.km))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsEvents.dateFrom != null) {
                val restrictionBlock = StringBuilder("")
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = filterParamsEvents.dateFrom!!
                restrictionBlock.append(fragment.getString(R.string.event_date_hint_from))
                    .append(" ")
                    .append(parseDate(calendar, "d MMM uuuu"))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsEvents.dateTo != null) {
                val restrictionBlock = StringBuilder("")
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = filterParamsEvents.dateTo!!
                restrictionBlock.append(fragment.getString(R.string.event_date_hint_to))
                    .append(" ")
                    .append(parseDate(calendar, "d MMM uuuu"))

                restrictionBlockList.add(restrictionBlock.toString())
            }

            if (filterParamsEvents.address != null) {
                val restrictionBlock = StringBuilder("")

                restrictionBlock.append(filterParamsEvents.address)

                restrictionBlockList.add(restrictionBlock.toString())
            }

            val restrictionsString = concatList(restrictionBlockList, splittingDot)
            if (restrictionsString == "") {
                binding.eventsChosenFilters.restrictions.text =
                    fragment.getString(R.string.filter_not_specified)
            } else {
                binding.eventsChosenFilters.restrictions.text = restrictionsString
            }
        }

        val sortingParser: () -> Unit = {
            binding.eventsChosenFilters.sorting.text = when (filterParamsEvents.sortBy) {
                FilterParamsEvents.EventsSortBy.Popularity -> fragment.getString(R.string.filter_sort_by_popularity)
                FilterParamsEvents.EventsSortBy.Date -> fragment.getString(R.string.filter_sort_by_date)
                FilterParamsEvents.EventsSortBy.Distance -> fragment.getString(R.string.filter_sort_by_distance)
            }
        }

        private val splittingDot = " " + fragment.context!!.getString(R.string.split_dot) + " "

    }

}