package com.thesis.sportologia.ui.events

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.FavouritesFragment
import com.thesis.sportologia.ui.HomeFragment
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapter
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapterFavourites
import com.thesis.sportologia.ui.events.ListEventsFragmentHome
import com.thesis.sportologia.ui.events.ListEventsViewModelHome
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapterHome
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListEventsFragmentHome : ListEventsFragment() {

    @Inject
    lateinit var factory: ListEventsViewModelHome.Factory

    override val viewModel by viewModelCreator {
        factory.create(userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onHeaderBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            HomeFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(HomeFragment.USER_ID to userId)
        )
    }

    override fun initEventHeaderAdapter(): EventsHeaderAdapter {
        return EventsHeaderAdapterHome(this, viewModel, viewModel.isUpcomingOnly)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListEventsFragmentHome {
            val myFragment = ListEventsFragmentHome()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}