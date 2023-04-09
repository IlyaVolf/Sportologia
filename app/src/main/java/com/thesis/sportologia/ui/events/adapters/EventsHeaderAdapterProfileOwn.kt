package com.thesis.sportologia.ui.events.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentListEventsHeaderBinding
import com.thesis.sportologia.model.events.entities.FilterParamsEvents

class EventsHeaderAdapterProfileOwn(
    fragment: Fragment,
    val listener: FilterListener,
    filterParamsEvents: FilterParamsEvents,
) : EventsHeaderAdapter(fragment, listener, filterParamsEvents) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListEventsHeaderBinding
    ): Holder {
        return HolderProfileOwn(fragment, listener, binding)
    }

    class HolderProfileOwn(
        fragment: Fragment,
        listener: FilterListener,
        binding: FragmentListEventsHeaderBinding,
    ) : Holder(fragment, binding, listener) {

        override val renderHeader: () -> Unit = {
            enableEventsFilter()
            enableCreateEventButton()
        }

    }

}