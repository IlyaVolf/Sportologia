package com.thesis.sportologia.ui.users.list_users_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.sportologia.databinding.FragmentListUsersBinding
import com.thesis.sportologia.ui.search.search_screen.SearchFragment
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.ui.base.LoadStateAdapterPaging
import com.thesis.sportologia.ui.users.adapters.UsersHeaderAdapter
import com.thesis.sportologia.ui.users.adapters.UsersPagerAdapter
import com.thesis.sportologia.utils.TryAgainAction
import com.thesis.sportologia.utils.observeEvent
import com.thesis.sportologia.utils.simpleScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
abstract class ListUsersFragment : Fragment() {

    abstract val viewModel: ListUsersViewModel
    abstract val isSwipeToRefreshEnabled: Boolean
    abstract val onUserSnippetItemPressed: (String) -> Unit

    private var _binding: FragmentListUsersBinding? = null
    private val binding
        get() = _binding!!

    protected var userId by Delegates.notNull<String>()
    protected lateinit var filterParams: FilterParamsUsers
    private lateinit var usersHeaderAdapter: UsersHeaderAdapter
    private lateinit var adapter: UsersPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = arguments?.getString("userId") ?: throw Exception()
        filterParams = savedInstanceState?.getSerializable("filterParams") as FilterParamsUsers?
            ?: FilterParamsUsers.newEmptyInstance()
        adapter = UsersPagerAdapter(this, onUserSnippetItemPressed)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable("filterParams", filterParams)
    }

    // onViewCreated() won't work because of lateinit mod initializations required to create viewmodel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListUsersBinding.inflate(inflater, container, false)

        initErrorActions()
        initSwipeToRefresh()
        initUsersList()
        initSearchQueryReceiver()

        observeErrorMessages()
        observeUsers(adapter)
        observeLoadState(adapter)
        observeInvalidationEvents(adapter)

        handleScrollingToTop(adapter)
        handleListVisibility(adapter)

        return binding.root
    }

    abstract fun initUsersHeaderAdapter(): UsersHeaderAdapter

    private fun initErrorActions() {
        binding.loadStateView.flpError.veTryAgain.setOnClickListener { adapter.retry() }
    }

    private fun initSearchQueryReceiver() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            SearchFragment.SUBMIT_SEARCH_USERS_QUERY_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val receivedSearchQuery =
                data.getString(SearchFragment.SEARCH_QUERY) ?: return@setFragmentResultListener

            val receivedFilterParams =
                data.getSerializable(SearchFragment.FILTER_PARAMETERS) as FilterParamsUsers?
                    ?: return@setFragmentResultListener

            filterParams = receivedFilterParams
            viewModel.setSearchBy(receivedSearchQuery, receivedFilterParams)
            usersHeaderAdapter.setFilterParamsUsers(filterParams)
        }
    }

    private fun initUsersList() {

        //val adapter = UsersPagerAdapter(this, onHeaderBlockPressedAction, viewModel)

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = LoadStateAdapterPaging(tryAgainAction)
        val headerAdapter = LoadStateAdapterPaging(tryAgainAction)

        // combined adapter which shows both the list of users + footer indicator when loading pages
        val adapterWithLoadState =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        usersHeaderAdapter = initUsersHeaderAdapter()
        val concatAdapter = ConcatAdapter(usersHeaderAdapter, adapterWithLoadState)

        binding.usersList.layoutManager = LinearLayoutManager(context)
        binding.usersList.adapter = concatAdapter
        (binding.usersList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
    }

    private fun initSwipeToRefresh() {
        if (isSwipeToRefreshEnabled) {
            binding.usersSwipeRefreshLayout.isEnabled = true
            binding.usersSwipeRefreshLayout.setOnRefreshListener {
                viewModel.refresh()
            }
        } else {
            binding.usersSwipeRefreshLayout.isEnabled = false
        }
    }

    private fun observeUsers(adapter: UsersPagerAdapter) {
        lifecycleScope.launch {
            viewModel.usersFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState(adapter: UsersPagerAdapter) {
        // can also use adapter.addLoadStateListener
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200).collectLatest { state ->

                val isError = state.refresh is LoadState.Error
                val isLoading = state.refresh is LoadState.Loading
                val isEmpty = (state.source.refresh is LoadState.NotLoading
                        && state.append.endOfPaginationReached && adapter.itemCount < 1)

                // main indicator in the center of the screen
                binding.loadStateView.flpError.root.isVisible = isError
                if (isSwipeToRefreshEnabled) {
                    binding.usersSwipeRefreshLayout.isRefreshing = isLoading
                    binding.loadStateView.flpLoading.root.isVisible = false
                } else {
                    binding.loadStateView.flpLoading.root.isVisible = isLoading
                }
                binding.usersEmptyBlock.isVisible = isEmpty
                binding.usersList.isVisible = !isError
            }
        }
    }

    private fun handleListVisibility(adapter: UsersPagerAdapter) = lifecycleScope.launch {
        // list should be hidden if an error is displayed OR if items are being loaded after the error:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.usersList.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun handleScrollingToTop(adapter: UsersPagerAdapter) = lifecycleScope.launch {
        // list should be scrolled to the 1st item (index = 0) if data has been reloaded:
        // (prev state = Loading, current state = NotLoading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading
                    && viewModel.scrollEvents.value?.get() != null
                ) {
                    binding.usersList.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: UsersPagerAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

    private fun observeErrorMessages() {
        viewModel.errorEvents.observeEvent(this) { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeInvalidationEvents(adapter: UsersPagerAdapter) {
        viewModel.invalidateEvents.observeEvent(this) {
            adapter.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}