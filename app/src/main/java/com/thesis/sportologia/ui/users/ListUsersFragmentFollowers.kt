package com.thesis.sportologia.ui.users

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.FavouritesFragment
import com.thesis.sportologia.ui.FollowersFragment
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapter
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapterFollowers
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListUsersFragmentFollowers : ListUsersFragment() {

    @Inject
    lateinit var factory: ListUsersViewModelFollowers.Factory

    override val viewModel by viewModelCreator {
        factory.create(filterParams, userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onUserSnippetItemPressed: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            FollowersFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(FollowersFragment.USER_ID to userId)
        )
    }

    override val initUserHeaderAdapter = {
        UsersHeaderAdapterFollowers(this, filterParams)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListUsersFragmentFollowers {
            val myFragment = ListUsersFragmentFollowers()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}