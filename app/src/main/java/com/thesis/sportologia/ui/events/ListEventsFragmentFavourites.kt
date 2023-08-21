package com.thesis.sportologia.ui.events

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.profile.favourites_screen.FavouritesFragment
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapter
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapterFavourites
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListEventsFragmentFavourites : ListEventsFragment() {

    @Inject
    lateinit var factory: ListEventsViewModelFavourites.Factory

    override val viewModel by viewModelCreator {
        factory.create(filterParams, userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onHeaderBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            FavouritesFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(FavouritesFragment.USER_ID to userId)
        )
    }

    override fun initEventsHeaderAdapter(): EventsHeaderAdapter {
        return EventsHeaderAdapterFavourites(
            this,
            viewModel,
            filterParams,
        )
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListEventsFragmentFavourites {
            val myFragment = ListEventsFragmentFavourites()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}