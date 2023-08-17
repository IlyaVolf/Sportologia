package com.thesis.sportologia.ui.services

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.FavouritesFragment
import com.thesis.sportologia.ui.services.adapters.ServicesHeaderAdapter
import com.thesis.sportologia.ui.services.adapters.ServicesHeaderAdapterFavourites
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListServicesFragmentFavourites : ListServicesFragment() {

    @Inject
    lateinit var factory: ListServicesViewModelFavourites.Factory

    override val viewModel by viewModelCreator {
        factory.create(filterParams, userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onAuthorBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            FavouritesFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(FavouritesFragment.USER_ID to userId)
        )
    }
    override val onStatsBlockPressedAction: (String) -> Unit = { serviceId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            FavouritesFragment.GO_TO_STATS_REQUEST_CODE,
            bundleOf(FavouritesFragment.SERVICE_ID to serviceId)
        )
    }
    override val onInfoBlockPressedAction: (String) -> Unit = { serviceId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            FavouritesFragment.GO_TO_SERVICE_REQUEST_CODE,
            bundleOf(FavouritesFragment.SERVICE_ID to serviceId)
        )
    }

    override fun initServicesHeaderAdapter(): ServicesHeaderAdapter {
        return ServicesHeaderAdapterFavourites(
            this,
            viewModel,
            filterParams,
            viewModel.serviceType
        )
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListServicesFragmentFavourites {
            val myFragment = ListServicesFragmentFavourites()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}