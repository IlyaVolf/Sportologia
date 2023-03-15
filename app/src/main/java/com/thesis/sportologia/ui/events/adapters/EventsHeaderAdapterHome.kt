package com.thesis.sportologia.ui.events.adapters

import androidx.fragment.app.Fragment

class EventsHeaderAdapterHome(
    fragment: Fragment,
    listener: FilterListener,
    isUpcomingOnly: Boolean,
) : EventsHeaderAdapter(fragment, listener) {

    override val renderHeader: () -> Unit = {
        enableEventsFilter(isUpcomingOnly)
    }

}