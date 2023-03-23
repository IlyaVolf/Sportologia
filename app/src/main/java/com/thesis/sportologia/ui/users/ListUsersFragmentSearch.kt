package com.thesis.sportologia.ui.users

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.thesis.sportologia.model.FilterParams
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.ui.FilterFragmentUsers
import com.thesis.sportologia.ui.SearchFragment
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapter
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapterFollowers
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapterSearch
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListUsersFragmentSearch : ListUsersFragment() {

    @Inject
    lateinit var factory: ListUsersViewModelSearch.Factory

    override val viewModel by viewModelCreator {
        factory.create(filterParams, userId)
    }

    override val isSwipeToRefreshEnabled: Boolean = true
    override val onUserSnippetItemPressed: (String) -> Unit = { userId ->
        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.GO_TO_PROFILE_REQUEST_CODE,
            bundleOf(SearchFragment.USER_ID to userId)
        )
    }

    override fun initUsersHeaderAdapter(): UsersHeaderAdapter  {
        return UsersHeaderAdapterSearch(this, filterParams)
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListUsersFragmentSearch {
            val myFragment = ListUsersFragmentSearch()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}