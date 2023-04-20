package com.thesis.sportologia.ui.services.adapters

import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentListServicesHeaderBinding
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceType

class ServicesHeaderAdapterProfileOther(
    fragment: Fragment,
    val listener: FilterListener,
    filterParamsServices: FilterParamsServices,
    private val serviceType: ServiceType?,
) : ServicesHeaderAdapter(fragment, listener, filterParamsServices) {

    override fun createHolder(
        fragment: Fragment,
        binding: FragmentListServicesHeaderBinding
    ): Holder {
        return HolderProfileOther(fragment, listener, binding, serviceType)
    }

    class HolderProfileOther(
        fragment: Fragment,
        listener: FilterListener,
        binding: FragmentListServicesHeaderBinding,
        serviceType: ServiceType?,
    ) : Holder(fragment, binding, listener) {

        override val renderHeader: () -> Unit = {
            enableServicesFilter(serviceType)
        }

    }

}