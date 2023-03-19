package com.thesis.sportologia.ui.events

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.ProfileFragment
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapter
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapterProfileOwn
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapterSearch
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapter
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListEventsFragmentProfileOwn : ListEventsFragment() {

    @Inject
    lateinit var factory: ListEventsViewModelProfile.Factory

    override val viewModel by viewModelCreator {
        factory.create(filterParams, userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = false
    override val onHeaderBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            ProfileFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(ProfileFragment.USER_ID to userId)
        )
    }

    override fun initEventsHeaderAdapter(): EventsHeaderAdapter {
        return EventsHeaderAdapterProfileOwn(
            this,
            viewModel,
            filterParams,
            viewModel.isUpcomingOnly
        )
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListEventsFragmentProfileOwn {
            val myFragment = ListEventsFragmentProfileOwn()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}