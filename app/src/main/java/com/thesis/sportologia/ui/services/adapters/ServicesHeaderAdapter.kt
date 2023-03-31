package com.thesis.sportologia.ui.services.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListServicesHeaderBinding
import com.thesis.sportologia.databinding.FragmentListUsersHeaderBinding
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.ui.TabsFragmentDirections
import com.thesis.sportologia.ui.services.CreateEditServiceFragment
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapter
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener
import com.thesis.sportologia.utils.findTopNavController

abstract class ServicesHeaderAdapter(
    private val fragment: Fragment,
    listener: FilterListener,
    filterParamsServices: FilterParamsServices,
) : RecyclerView.Adapter<ServicesHeaderAdapter.Holder>() {

    protected lateinit var filterParamsServices: FilterParamsServices

    init {
        setFilterParamsServices(filterParamsServices)
    }

    @JvmName("setFilterParamsServices1")
    fun setFilterParamsServices(filterParamsServices: FilterParamsServices) {
        this.filterParamsServices = filterParamsServices
        notifyDataSetChanged()
    }

    protected lateinit var binding: FragmentListServicesHeaderBinding

    abstract fun createHolder(
        fragment: Fragment,
        binding: FragmentListServicesHeaderBinding,
    ): Holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FragmentListServicesHeaderBinding.inflate(inflater, parent, false)

        return createHolder(fragment, binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    interface FilterListener {

        fun filterApply(serviceType: ServiceType?)

    }

    abstract class Holder(
        val fragment: Fragment,
        val binding: FragmentListServicesHeaderBinding,
        val listener: FilterListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        abstract val renderHeader: () -> Unit

        fun bind() {
            disableAllItems()
            renderHeader()
        }

        private fun disableAllItems() {
            binding.servicesChosenFilters.root.isVisible = false
            binding.servicesChosenFiltersSpace.isVisible = false
            binding.createServiceButton.isVisible = false
            binding.createServiceButtonSpace.isVisible = false
            binding.servicesFilter.root.isVisible = false
            binding.servicesFilterSpace.isVisible = false
        }

        protected fun enableServicesFilter(serviceType: ServiceType?) {
            binding.servicesFilter.root.isVisible = true
            binding.servicesFilterSpace.isVisible = true
            binding.servicesFilter.spinner.initAdapter(filterOptionsList, getFilterValue(serviceType))
            binding.servicesFilter.spinner.setListener(eventsFilterListener)
        }

        protected fun enableServicesChosenFilters(parser: () -> Unit) {
            binding.servicesChosenFilters.root.isVisible = true
            binding.servicesChosenFiltersSpace.isVisible = true

            parser()
        }

        protected fun enableCreateServiceButton() {
            binding.createServiceButton.isVisible = true
            binding.createServiceButtonSpace.isVisible = true
            binding.createServiceButton.setOnClickListener {
                onCreateServiceButtonPressed()
            }
        }

        private val filterOptionsList = listOf(
            fragment.context?.getString(R.string.filter_services_all) ?: "",
            fragment.context?.getString(R.string.filter_services_tr_programs) ?: "",
        )

        private fun getFilterValue(serviceType: ServiceType?): String {
            return when (serviceType) {
                ServiceType.TRAINING_PROGRAM -> filterOptionsList[1]
                else -> filterOptionsList[0]
            }
        }

        private val eventsFilterListener: OnSpinnerOnlyOutlinedActionListener = {
            when (it) {
                filterOptionsList[1] -> listener.filterApply(ServiceType.TRAINING_PROGRAM)
                filterOptionsList[0] -> listener.filterApply(null)
            }
        }

        private fun onCreateServiceButtonPressed() {
            val direction = TabsFragmentDirections.actionTabsFragmentToCreateEditServiceFragment(
                CreateEditServiceFragment.ServiceId(null)
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