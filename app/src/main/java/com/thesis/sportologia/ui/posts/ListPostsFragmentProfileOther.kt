package com.thesis.sportologia.ui.posts

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.ProfileFragment
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapter
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapterProfileOther
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListPostsFragmentProfileOther : ListPostsFragment() {

    @Inject
    lateinit var factory: ListPostsViewModelProfile.Factory

    override val viewModel by viewModelCreator {
        Log.d("abcdef", "ListPostsFragmentProfileOther $userId")
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
        return PostsHeaderAdapterProfileOther(this, viewModel)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListPostsFragmentProfileOther {
            val myFragment = ListPostsFragmentProfileOther()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}