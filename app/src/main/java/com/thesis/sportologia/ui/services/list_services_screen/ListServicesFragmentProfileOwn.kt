package com.thesis.sportologia.ui.services.list_services_screen

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.profile.profile_screen.ProfileFragment
import com.thesis.sportologia.ui.services.adapters.ServicesHeaderAdapter
import com.thesis.sportologia.ui.services.adapters.ServicesHeaderAdapterProfileOwn
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListServicesFragmentProfileOwn : ListServicesFragment() {

    @Inject
    lateinit var factory: ListServicesViewModelProfile.Factory

    override val viewModel by viewModelCreator {
        factory.create(filterParams, userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = false
    override val onAuthorBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            ProfileFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(ProfileFragment.USER_ID to userId)
        )
    }
    override val onStatsBlockPressedAction: (String) -> Unit = { serviceId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            ProfileFragment.GO_TO_STATS_REQUEST_CODE,
            bundleOf(ProfileFragment.SERVICE_ID to serviceId)
        )
    }
    override val onInfoBlockPressedAction: (String) -> Unit = { serviceId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            ProfileFragment.GO_TO_SERVICE_REQUEST_CODE,
            bundleOf(ProfileFragment.SERVICE_ID to serviceId)
        )
    }

    override fun initServicesHeaderAdapter(): ServicesHeaderAdapter {
        return ServicesHeaderAdapterProfileOwn(
            this,
            viewModel,
            filterParams,
            viewModel.serviceType
        )
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListServicesFragmentProfileOwn {
            val myFragment = ListServicesFragmentProfileOwn()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}