package com.thesis.sportologia.ui.posts.list_posts_screen

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.profile.profile_screen.ProfileFragment
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapter
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapterProfileOwn
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListPostsFragmentProfileOwn : ListPostsFragment() {

    @Inject
    lateinit var factory: ListPostsViewModelProfile.Factory

    override val viewModel by viewModelCreator {
        factory.create(userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = false
    override val onHeaderBlockPressedAction: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            ProfileFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(ProfileFragment.USER_ID to userId)
        )
    }

    override fun initPostHeaderAdapter(): PostsHeaderAdapter {
        return PostsHeaderAdapterProfileOwn(this, viewModel)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListPostsFragmentProfileOwn {
            val myFragment = ListPostsFragmentProfileOwn()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}