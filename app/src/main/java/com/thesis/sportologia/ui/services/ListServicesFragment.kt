package com.thesis.sportologia.ui.services

import android.os.Bundle
import android.util.Log
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
import com.thesis.sportologia.databinding.FragmentListServicesBinding
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.ui.*
import com.thesis.sportologia.ui.adapters.LoadStateAdapterPaging
import com.thesis.sportologia.ui.adapters.TryAgainAction
import com.thesis.sportologia.ui.services.adapters.ServicesHeaderAdapter
import com.thesis.sportologia.ui.services.adapters.ServicesPagerAdapter
import com.thesis.sportologia.utils.observeEvent
import com.thesis.sportologia.utils.simpleScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
abstract class ListServicesFragment : Fragment() {

    abstract val viewModel: ListServicesViewModel
    abstract val isSwipeToRefreshEnabled: Boolean
    abstract val onAuthorBlockPressedAction: (String) -> Unit
    abstract val onStatsBlockPressedAction: (Long) -> Unit
    abstract val onInfoBlockPressedAction: (Long) -> Unit

    protected var userId by Delegates.notNull<String>()
    protected lateinit var filterParams: FilterParamsServices
    protected lateinit var binding: FragmentListServicesBinding
    private lateinit var servicesHeaderAdapter: ServicesHeaderAdapter
    private lateinit var adapter: ServicesPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = arguments?.getString("userId") ?: throw Exception()
        filterParams = savedInstanceState?.getSerializable("filterParams") as FilterParamsServices?
            ?: FilterParamsServices.newEmptyInstance()
        adapter = ServicesPagerAdapter(
            this,
            onAuthorBlockPressedAction,
            onStatsBlockPressedAction,
            onInfoBlockPressedAction,
            viewModel
        )
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
        binding = FragmentListServicesBinding.inflate(inflater, container, false)

        initErrorActions()
        initResultsProcessing()
        initSwipeToRefresh()
        initServicesList()
        initSearchQueryReceiver()

        observeErrorMessages()
        observeServices(adapter)
        observeLoadState(adapter)
        observeInvalidationServices(adapter)

        handleScrollingToTop(adapter)
        handleListVisibility(adapter)

        return binding.root
    }

    abstract fun initServicesHeaderAdapter(): ServicesHeaderAdapter

    private fun initErrorActions() {
        binding.loadStateView.flpError.veTryAgain.setOnClickListener { adapter.retry() }
    }

    private fun initSearchQueryReceiver() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            SearchFragment.SUBMIT_SEARCH_SERVICES_QUERY_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val receivedSearchQuery =
                data.getString(SearchFragment.SEARCH_QUERY) ?: return@setFragmentResultListener

            val receivedFilterParams =
                data.getSerializable(SearchFragment.FILTER_PARAMETERS) as FilterParamsServices?
                    ?: return@setFragmentResultListener

            filterParams = receivedFilterParams
            viewModel.setSearchBy(receivedSearchQuery, receivedFilterParams)
            servicesHeaderAdapter.setFilterParamsServices(filterParams)
        }
    }

    private fun initResultsProcessing() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditServiceFragment.IS_CREATED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            Log.d("abcdef", "IS_CREATED_REQUEST_CODE")
            val isSaved = data.getBoolean(CreateEditServiceFragment.IS_CREATED)
            if (isSaved) {
                viewModel.onServiceCreated()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditServiceFragment.IS_EDITED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isSaved = data.getBoolean(CreateEditServiceFragment.IS_EDITED)
            if (isSaved) {
                viewModel.onServiceEdited()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            ServiceFragment.IS_DELETED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isDeleted = data.getBoolean(ServiceFragment.IS_DELETED)
            if (isDeleted) {
                viewModel.onServiceDeleted()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            REFRESH_SERVICES_LIST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            Log.d("abcdef", "REFRESH_SERVICES_LIST_KEY")
            viewModel.refresh()
        }

    }

    private fun initServicesList() {

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = LoadStateAdapterPaging(tryAgainAction)
        val headerAdapter = LoadStateAdapterPaging(tryAgainAction)

        // combined adapter which shows both the list of services + footer indicator when loading pages
        val adapterWithLoadState =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        servicesHeaderAdapter = initServicesHeaderAdapter()
        val concatAdapter = ConcatAdapter(servicesHeaderAdapter, adapterWithLoadState)

        binding.servicesList.layoutManager = LinearLayoutManager(context)
        binding.servicesList.adapter = concatAdapter
        (binding.servicesList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations =
            false

    }

    private fun initSwipeToRefresh() {
        if (isSwipeToRefreshEnabled) {
            binding.servicesSwipeRefreshLayout.isEnabled = true
            binding.servicesSwipeRefreshLayout.setOnRefreshListener {
                viewModel.refresh()
            }
        } else {
            binding.servicesSwipeRefreshLayout.isEnabled = false
        }
    }

    private fun observeServices(adapter: ServicesPagerAdapter) {
        lifecycleScope.launch {
            viewModel.servicesFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState(adapter: ServicesPagerAdapter) {
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
                    binding.servicesSwipeRefreshLayout.isRefreshing = isLoading
                    binding.loadStateView.flpLoading.root.isVisible = false
                } else {
                    binding.loadStateView.flpLoading.root.isVisible = isLoading
                }
                binding.servicesEmptyBlock.isVisible = isEmpty
                binding.servicesList.isVisible = !isError
            }
        }
    }

    private fun handleListVisibility(adapter: ServicesPagerAdapter) = lifecycleScope.launch {
        // list should be hidden if an error is displayed OR if items are being loaded after the error:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.servicesList.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun handleScrollingToTop(adapter: ServicesPagerAdapter) = lifecycleScope.launch {
        // list should be scrolled to the 1st item (index = 0) if data has been reloaded:
        // (prev state = Loading, current state = NotLoading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading
                    && viewModel.scrollServices.value?.get() != null
                ) {
                    binding.servicesList.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: ServicesPagerAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

    private fun observeErrorMessages() {
        viewModel.errorServices.observeEvent(this) { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeInvalidationServices(adapter: ServicesPagerAdapter) {
        viewModel.invalidateServices.observeEvent(this) {
            adapter.refresh()
        }
    }

}