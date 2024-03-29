package com.thesis.sportologia.ui.users.list_users_screen

import android.os.Bundle
import androidx.core.os.bundleOf
import com.thesis.sportologia.ui.profile.followings_screen.FollowingsFragment
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapter
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapterFollowings
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListUsersFragmentFollowings : ListUsersFragment() {

    @Inject
    lateinit var factory: ListUsersViewModelFollowings.Factory

    override val viewModel by viewModelCreator {
        factory.create(filterParams, userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onUserSnippetItemPressed: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            FollowingsFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(FollowingsFragment.USER_ID to userId)
        )
    }

    override fun initUsersHeaderAdapter(): UsersHeaderAdapter  {
        return UsersHeaderAdapterFollowings(this, filterParams)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListUsersFragmentFollowings {
            val myFragment = ListUsersFragmentFollowings()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}