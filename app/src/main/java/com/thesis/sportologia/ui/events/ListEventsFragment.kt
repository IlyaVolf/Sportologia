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
import com.thesis.sportologia.ui.ProfileFragment
import com.thesis.sportologia.ui.SearchFragment
import com.thesis.sportologia.ui.adapters.LoadStateAdapterPage
import com.thesis.sportologia.ui.adapters.LoadStateAdapterPaging
import com.thesis.sportologia.ui.adapters.TryAgainAction
import com.thesis.sportologia.ui.events.adapters.EventsHeaderAdapter
import com.thesis.sportologia.ui.events.adapters.EventsPagerAdapter
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

    protected var userId by Delegates.notNull<String>()
    protected lateinit var binding: FragmentListEventsBinding
    private lateinit var adapter: EventsPagerAdapter
    private lateinit var mainLoadStateHolder: LoadStateAdapterPage.Holder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = arguments?.getString("userId") ?: throw Exception()
        adapter = EventsPagerAdapter(this, onHeaderBlockPressedAction, viewModel)
    }

    // onViewCreated() won't work because of lateinit mod initializations required to create viewmodel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListEventsBinding.inflate(inflater, container, false)

        userId = arguments?.getString("userId") ?: throw Exception()

        initSearchQueryReceiver()
        initResultsProcessing()
        initSwipeToRefresh()
        initEventsList()

        observeErrorMessages()
        observeEvents(adapter)
        observeLoadState(adapter)
        observeInvalidationEvents(adapter)

        handleScrollingToTop(adapter)
        handleListVisibility(adapter)

        return binding.root
    }

    private fun initSearchQueryReceiver() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            SearchFragment.SUBMIT_SEARCH_QUERY_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val searchQuery =
                data.getString(SearchFragment.SEARCH_QUERY) ?: return@setFragmentResultListener

            viewModel.setSearchBy(searchQuery)
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
            ProfileFragment.REFRESH_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val refresh = data.getBoolean(ProfileFragment.REFRESH)
            if (refresh) {
                viewModel.refresh()
            }
        }
    }

    abstract fun initEventHeaderAdapter(): EventsHeaderAdapter

    private fun initEventsList() {

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = LoadStateAdapterPaging(tryAgainAction)
        val headerAdapter = LoadStateAdapterPaging(tryAgainAction)

        // combined adapter which shows both the list of events + footer indicator when loading pages
        val adapterWithLoadState =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        val swipeRefreshLayout = if (isSwipeToRefreshEnabled) {
            binding.eventsSwipeRefreshLayout
        } else {
            null
        }
        val eventsHeaderAdapter = initEventHeaderAdapter()
        val concatAdapter = ConcatAdapter(eventsHeaderAdapter, adapterWithLoadState)

        binding.eventsList.layoutManager = LinearLayoutManager(context)
        binding.eventsList.adapter = concatAdapter
        (binding.eventsList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        mainLoadStateHolder = LoadStateAdapterPage.Holder(
            binding.loadStateView,
            swipeRefreshLayout,
            tryAgainAction
        )
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
        // you can also use adapter.addLoadStateListener
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200).collectLatest { state ->
                // main indicator in the center of the screen
                mainLoadStateHolder.bind(state.refresh)
            }
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading
                && loadState.append.endOfPaginationReached && adapter.itemCount < 1
            ) {
                binding.eventsList.isVisible = false
                binding.eventsEmptyBlock.isVisible = true
            } else {
                binding.eventsList.isVisible = true
                binding.eventsEmptyBlock.isVisible = false
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

}