package com.thesis.sportologia.ui.events.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListEventsHeaderBinding
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.ui.TabsFragmentDirections
import com.thesis.sportologia.ui.events.CreateEditEventFragment
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener
import com.thesis.sportologia.utils.findTopNavController
import kotlin.properties.Delegates

abstract class EventsHeaderAdapter(
    private val fragment: Fragment,
    listener: FilterListener,
    filterParamsEvents: FilterParamsEvents,
) : RecyclerView.Adapter<EventsHeaderAdapter.Holder>() {

    protected lateinit var filterParamsEvents: FilterParamsEvents
    private var isUpcomingOnly = true

    init {
        setFilterParamsEvents(filterParamsEvents)
    }

    @JvmName("setFilterParamsEvents1")
    fun setFilterParamsEvents(filterParamsEvents: FilterParamsEvents) {
        this.filterParamsEvents = filterParamsEvents
        notifyDataSetChanged()
    }

    @JvmName("setIsUpcomingOnly1")
    fun setIsUpcomingOnly(isUpcomingOnly: Boolean) {
        this.isUpcomingOnly = isUpcomingOnly
        notifyDataSetChanged()
    }

    protected lateinit var binding: FragmentListEventsHeaderBinding

    abstract fun createHolder(
        fragment: Fragment,
        binding: FragmentListEventsHeaderBinding,
    ): Holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FragmentListEventsHeaderBinding.inflate(inflater, parent, false)

        return createHolder(fragment, binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(isUpcomingOnly)
    }

    override fun getItemCount(): Int {
        return 1
    }

    interface FilterListener {

        fun filterApply(isUpcomingOnly: Boolean)

    }

    abstract class Holder(
        val fragment: Fragment,
        val binding: FragmentListEventsHeaderBinding,
        val listener: FilterListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        abstract val renderHeader: () -> Unit

        private var isUpcomingOnly by Delegates.notNull<Boolean>()

        fun bind(isUpcomingOnly: Boolean) {
            this.isUpcomingOnly = isUpcomingOnly
            disableAllItems()
            renderHeader()
        }

        private fun disableAllItems() {
            binding.eventsChosenFilters.root.isVisible = false
            binding.eventsChosenFiltersSpace.isVisible = false
            binding.eventsFilter.root.isVisible = false
            binding.eventsFilterSpace.isVisible = false
            binding.createEventButton.isVisible = false
            binding.createEventButtonSpace.isVisible = false
        }

        protected fun enableEventsFilter() {
            binding.eventsFilter.root.isVisible = true
            binding.eventsFilterSpace.isVisible = true
            binding.eventsFilter.spinner.initAdapter(filterOptionsList, getFilterValue(isUpcomingOnly))
            binding.eventsFilter.spinner.setListener(eventsFilterListener)
        }

        protected fun enableEventsChosenFilters(parser: () -> Unit) {
            binding.eventsChosenFilters.root.isVisible = true
            binding.eventsChosenFiltersSpace.isVisible = true

            parser()
        }

        protected fun enableCreateEventButton() {
            binding.createEventButton.isVisible = true
            binding.createEventButtonSpace.isVisible = true
            binding.createEventButton.setOnClickListener {
                onCreateEventButtonPressed()
            }
        }

        private val filterOptionsList = listOf(
            fragment.context?.getString(R.string.filter_events_upcoming) ?: "",
            fragment.context?.getString(R.string.filter_events_all) ?: "",
        )

        private fun getFilterValue(isUpcomingOnly: Boolean): String {
            return when (isUpcomingOnly) {
                true -> filterOptionsList[0]
                false -> filterOptionsList[1]
            }
        }

        private val eventsFilterListener: OnSpinnerOnlyOutlinedActionListener = {
            when (it) {
                filterOptionsList[0] -> listener.filterApply(true)
                filterOptionsList[1] -> listener.filterApply(false)
            }
        }

        private fun onCreateEventButtonPressed() {
            val direction = TabsFragmentDirections.actionTabsFragmentToCreateEditEventFragment(
                CreateEditEventFragment.EventId(null)
            )

            fragment.findTopNavController().navigate(direction,
                navOptions {
                    anim {
                        enter = R.anim.enter
                        exit = R.anim.exit
                        popEnter = R.anim.pop_enter
                        popExit = R.anim.pop_exit
                    }
                })
        }

    }
}