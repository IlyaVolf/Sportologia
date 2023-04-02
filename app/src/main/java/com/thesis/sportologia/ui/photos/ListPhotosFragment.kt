package com.thesis.sportologia.ui.photos

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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.thesis.sportologia.databinding.FragmentListPhotosBinding
import com.thesis.sportologia.ui.adapters.*
import com.thesis.sportologia.ui.photos.adapters.PhotosPagerAdapter
import com.thesis.sportologia.utils.observeEvent
import com.thesis.sportologia.utils.simpleScan
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListPhotosFragment : Fragment() {

    @Inject
    lateinit var factory: ListPhotosViewModel.Factory

    private val viewModel by viewModelCreator {
        factory.create(userId)
    }

    private val isSwipeToRefreshEnabled = true

    private var userId by Delegates.notNull<String>()
    private lateinit var binding: FragmentListPhotosBinding
    private lateinit var adapter: PhotosPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = arguments?.getString("userId") ?: throw Exception()
        adapter = PhotosPagerAdapter(this)
    }

    // onViewCreated() won't work because of lateinit mod initializations required to create viewmodel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListPhotosBinding.inflate(inflater, container, false)

        initErrorActions()
        initSwipeToRefresh()
        initPhotosList()

        observeErrorMessages()
        observePhotos(adapter)
        observeLoadState(adapter)
        observeInvalidationEvents(adapter)

        handleScrollingToTop(adapter)
        handleListVisibility(adapter)

        return binding.root
    }

    private fun initErrorActions() {
        binding.loadStateView.flpError.veTryAgain.setOnClickListener { adapter.retry() }
    }

    private fun initPhotosList() {

        //val adapter = PhotosPagerAdapter(this, onHeaderBlockPressedAction, viewModel)

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = LoadStateAdapterPaging(tryAgainAction)
        val headerAdapter = LoadStateAdapterPaging(tryAgainAction)

        // combined adapter which shows both the list of photos + footer indicator when loading pages
        val adapterWithLoadState =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        binding.photosList.layoutManager = LinearLayoutManager(context)
        binding.photosList.adapter = adapterWithLoadState
        (binding.photosList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
    }

    private fun initSwipeToRefresh() {
        if (isSwipeToRefreshEnabled) {
            binding.photosSwipeRefreshLayout.isEnabled = true
            binding.photosSwipeRefreshLayout.setOnRefreshListener {
                viewModel.refresh()
            }
        } else {
            binding.photosSwipeRefreshLayout.isEnabled = false
        }
    }

    private fun observePhotos(adapter: PhotosPagerAdapter) {
        lifecycleScope.launch {
            viewModel.photosFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState(adapter: PhotosPagerAdapter) {
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
                    binding.photosSwipeRefreshLayout.isRefreshing = isLoading
                    binding.loadStateView.flpLoading.root.isVisible = false
                } else {
                    binding.loadStateView.flpLoading.root.isVisible = isLoading
                }
                binding.photosEmptyBlock.isVisible = isEmpty
                binding.photosList.isVisible = !isError
            }
        }
    }

    private fun handleListVisibility(adapter: PhotosPagerAdapter) = lifecycleScope.launch {
        // list should be hidden if an error is displayed OR if items are being loaded after the error:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.photosList.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun handleScrollingToTop(adapter: PhotosPagerAdapter) = lifecycleScope.launch {
        // list should be scrolled to the 1st item (index = 0) if data has been reloaded:
        // (prev state = Loading, current state = NotLoading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading
                    && viewModel.scrollEvents.value?.get() != null
                ) {
                    binding.photosList.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: PhotosPagerAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

    private fun observeErrorMessages() {
        viewModel.errorEvents.observeEvent(this) { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeInvalidationEvents(adapter: PhotosPagerAdapter) {
        viewModel.invalidateEvents.observeEvent(this) {
            adapter.refresh()
        }
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): ListPhotosFragment {
            val myFragment = ListPhotosFragment()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}