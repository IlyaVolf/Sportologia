package com.thesis.sportologia.ui.events.adapters

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

class EventsHeaderAdapterProfileOwn(
    fragment: Fragment,
    listener: FilterListener
) : EventsHeaderAdapter(fragment, listener) {

    override val renderHeader: () -> Unit = {
        binding.eventsFilter.root.isVisible = false
        binding.eventsFilterSpace.isVisible = false

        binding.eventsChosenFilters.root.isVisible = false
        binding.eventsChosenFiltersSpace.isVisible = false

        binding.createEventButton.isVisible = true
        binding.createEventButtonSpace.isVisible = true

        binding.createEventButton.setOnClickListener {
            onCreateEventButtonPressed()
        }
    }

}