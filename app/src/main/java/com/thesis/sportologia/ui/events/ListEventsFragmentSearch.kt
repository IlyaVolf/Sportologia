package com.thesis.sportologia.ui.events

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.HomeFragment
import com.thesis.sportologia.ui.SearchFragment
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapter
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapterHome
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapterSearch
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListEventsFragmentSearch : ListEventsFragment() {

    @Inject
    lateinit var factory: ListEventsViewModelSearch.Factory

    override val viewModel by viewModelCreator {
        factory.create(userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onHeaderBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(HomeFragment.USER_ID to userId)
        )
    }

    override fun initEventHeaderAdapter(): EventsHeaderAdapter {
        return EventsHeaderAdapterSearch(this, viewModel)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListEventsFragmentSearch {
            val myFragment = ListEventsFragmentSearch()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}