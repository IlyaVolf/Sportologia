package com.thesis.sportologia.ui.events

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
import com.thesis.sportologia.databinding.FragmentListEventsBinding
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.ui.entities.REFRESH_EVENTS_LIST_KEY
import com.thesis.sportologia.ui.search.search_screen.SearchFragment
import com.thesis.sportologia.ui.base.LoadStateAdapterPaging
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapter
import com.thesis.sportologia.ui.events.adapters.EventsPagerAdapter
import com.thesis.sportologia.utils.TryAgainAction
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
abstract class ListEventsFragment : Fragment() {

    abstract val viewModel: ListEventsViewModel
    abstract val isSwipeToRefreshEnabled: Boolean
    abstract val onHeaderBlockPressedAction: (String) -> Unit

    private var _binding: FragmentListEventsBinding? = null
    private val binding
        get() = _binding!!


    protected var userId by Delegates.notNull<String>()
    protected lateinit var filterParams: FilterParamsEvents
    private lateinit var eventsHeaderAdapter: EventsHeaderAdapter
    private lateinit var adapter: EventsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = arguments?.getString("userId") ?: throw Exception()
        filterParams = savedInstanceState?.getSerializable("filterParams") as FilterParamsEvents?
            ?: FilterParamsEvents.newEmptyInstance()
        adapter = EventsPagerAdapter(this, onHeaderBlockPressedAction, viewModel)
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
        _binding = FragmentListEventsBinding.inflate(inflater, container, false)

        initErrorActions()
        initResultsProcessing()
        initSwipeToRefresh()
        initEventsList()
        initSearchQueryReceiver()

        observeErrorMessages()
        observeEvents(adapter)
        observeLoadState(adapter)
        observeInvalidationEvents(adapter)
        observeFilter()

        handleScrollingToTop(adapter)
        handleListVisibility(adapter)

        return binding.root
    }

    abstract fun initEventsHeaderAdapter(): EventsHeaderAdapter

    private fun initErrorActions() {
        binding.loadStateView.flpError.veTryAgain.setOnClickListener { adapter.retry() }
    }

    private fun initSearchQueryReceiver() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            SearchFragment.SUBMIT_SEARCH_EVENTS_QUERY_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val receivedSearchQuery =
                data.getString(SearchFragment.SEARCH_QUERY) ?: return@setFragmentResultListener

            val receivedFilterParams =
                data.getSerializable(SearchFragment.FILTER_PARAMETERS) as FilterParamsEvents?
                    ?: return@setFragmentResultListener

            filterParams = receivedFilterParams
            viewModel.setSearchBy(receivedSearchQuery, receivedFilterParams)
            eventsHeaderAdapter.setFilterParamsEvents(filterParams)
        }
    }

    private fun initResultsProcessing() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditEventFragment.IS_CREATED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isSaved = data.getBoolean(CreateEditEventFragment.IS_CREATED)
            if (isSaved) {
                viewModel.onEventCreated()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditEventFragment.IS_EDITED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isSaved = data.getBoolean(CreateEditEventFragment.IS_EDITED)
            if (isSaved) {
                viewModel.onEventEdited()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            REFRESH_EVENTS_LIST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            viewModel.refresh()
        }
    }

    private fun initEventsList() {

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = LoadStateAdapterPaging(tryAgainAction)
        val headerAdapter = LoadStateAdapterPaging(tryAgainAction)

        // combined adapter which shows both the list of events + footer indicator when loading pages
        val adapterWithLoadState =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        eventsHeaderAdapter = initEventsHeaderAdapter()
        val concatAdapter = ConcatAdapter(eventsHeaderAdapter, adapterWithLoadState)

        binding.eventsList.layoutManager = LinearLayoutManager(context)
        binding.eventsList.adapter = concatAdapter
        (binding.eventsList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
    }

    private fun initSwipeToRefresh() {
        if (isSwipeToRefreshEnabled) {
            binding.eventsSwipeRefreshLayout.isEnabled = true
            binding.eventsSwipeRefreshLayout.setOnRefreshListener {
                viewModel.refresh()
            }
        } else {
            binding.eventsSwipeRefreshLayout.isEnabled = false
        }
    }

    private fun observeEvents(adapter: EventsPagerAdapter) {
        lifecycleScope.launch {
            viewModel.eventsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState(adapter: EventsPagerAdapter) {
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
                    binding.eventsSwipeRefreshLayout.isRefreshing = isLoading
                    binding.loadStateView.flpLoading.root.isVisible = false
                } else {
                    binding.loadStateView.flpLoading.root.isVisible = isLoading
                }
                binding.eventsEmptyBlock.isVisible = isEmpty
                binding.eventsList.isVisible = !isError
            }
        }
    }

    private fun handleListVisibility(adapter: EventsPagerAdapter) = lifecycleScope.launch {
        // list should be hidden if an error is displayed OR if items are being loaded after the error:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.eventsList.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun handleScrollingToTop(adapter: EventsPagerAdapter) = lifecycleScope.launch {
        // list should be scrolled to the 1st item (index = 0) if data has been reloaded:
        // (prev state = Loading, current state = NotLoading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading
                    && viewModel.scrollEvents.value?.get() != null
                ) {
                    binding.eventsList.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: EventsPagerAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

    private fun observeErrorMessages() {
        viewModel.errorEvents.observeEvent(this) { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeInvalidationEvents(adapter: EventsPagerAdapter) {
        viewModel.invalidateEvents.observeEvent(this) {
            adapter.refresh()
        }
    }

    private fun observeFilter() {
        viewModel.isUpcomingOnlyLiveData.observe(viewLifecycleOwner) {
            eventsHeaderAdapter.setIsUpcomingOnly(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}