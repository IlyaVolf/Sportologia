package com.thesis.sportologia.ui.posts.list_posts_screen

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.home.HomeFragment
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapter
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapterHome
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListPostsFragmentHome : ListPostsFragment() {

    @Inject
    lateinit var factory: ListPostsViewModelHome.Factory

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

    override fun initPostHeaderAdapter(): PostsHeaderAdapter {
        return PostsHeaderAdapterHome(this, viewModel)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListPostsFragmentHome {
            val myFragment = ListPostsFragmentHome()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}