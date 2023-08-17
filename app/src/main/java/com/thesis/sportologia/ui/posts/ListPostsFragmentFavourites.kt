package com.thesis.sportologia.ui.posts

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.FavouritesFragment
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapter
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapterFavourites
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListPostsFragmentFavourites : ListPostsFragment() {

    @Inject
    lateinit var factory: ListPostsViewModelFavourites.Factory

    override val viewModel by viewModelCreator {
        factory.create(userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onHeaderBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            FavouritesFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(FavouritesFragment.USER_ID to userId)
        )
    }

    override fun initPostHeaderAdapter(): PostsHeaderAdapter {
        return PostsHeaderAdapterFavourites(this, viewModel)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListPostsFragmentFavourites {
            val myFragment = ListPostsFragmentFavourites()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}