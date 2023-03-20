package com.thesis.sportologia.ui.events.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentListEventsHeaderBinding
import com.thesis.sportologia.model.events.entities.FilterParamsEvents

class EventsHeaderAdapterProfileOwn(
    fragment: Fragment,
    val listener: FilterListener,
    filterParamsEvents: FilterParamsEvents,
    val isUpcomingOnly: Boolean,
) : EventsHeaderAdapter(fragment, listener, filterParamsEvents) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListEventsHeaderBinding
    ): Holder {
        return HolderProfileOwn(fragment, listener, binding, isUpcomingOnly)
    }

    class HolderProfileOwn(
        fragment: Fragment,
        listener: FilterListener,
        binding: FragmentListEventsHeaderBinding,
        isUpcomingOnly: Boolean,
    ) : Holder(fragment, binding, listener) {

        override val renderHeader: () -> Unit = {
            enableEventsFilter(isUpcomingOnly)
            enableCreateEventButton()
        }

    }

}