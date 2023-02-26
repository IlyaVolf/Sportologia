package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListPostsBinding
import com.thesis.sportologia.ui.adapters.*
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener
import com.thesis.sportologia.utils.findTopNavController
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
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
@ExperimentalCoroutinesApi
@FlowPreview
class ListPostsFragment :
    BaseFragment(R.layout.fragment_list_posts) {

    @Inject
    lateinit var factory: ListPostsViewModel.Factory

    private lateinit var mode: ListPostsMode

    override val viewModel by viewModelCreator {
        factory.create(mode)
    }

    private lateinit var binding: FragmentListPostsBinding
    private lateinit var mainLoadStateHolder: DefaultLoadStateAdapter.Holder

    companion object {
        fun newInstance(mode: ListPostsMode): ListPostsFragment {
            val myFragment = ListPostsFragment()
            val args = Bundle()
            args.putSerializable("mode", mode)
            myFragment.arguments = args
            return myFragment
        }
    }

    // onViewCreated() won't work because of lateinit mod initializations required to create viewmodel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListPostsBinding.inflate(inflater, container, false)

        mode = arguments?.getSerializable("mode") as ListPostsMode? ?: ListPostsMode.HOME_PAGE

        setupPostsList()

        observeErrorMessages()

        processResults()

        return binding.root
    }

    private fun processResults() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreatePostFragment.REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isCreated = data.getBoolean(CreatePostFragment.IS_CREATED)
            if (isCreated) {
                viewModel.onPostCreated()
            }
        }
    }

    /*override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }*/

    // TODO save scroll position when navigating

    private fun setupPostsList() {
        val adapter = PostsPagerAdapter(this, mode, viewModel)

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)
        val headerAdapter = DefaultLoadStateAdapter(tryAgainAction)

        // combined adapter which shows both the list of posts + footer indicator when loading pages
        val adapterWithLoadState =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        val postFilterListener: OnSpinnerOnlyOutlinedActionListener = {
            when (it) {
                getString(R.string.filter_posts_all) -> {
                    viewModel.athTorgF = null
                }
                getString(R.string.filter_posts_athletes) -> {
                    viewModel.athTorgF = true
                }
                getString(R.string.filter_posts_organizations) -> {
                    viewModel.athTorgF = false
                }
            }
            viewModel.refresh()
        }

        val postsHeaderAdapter = PostsHeaderAdapter(postFilterListener, this, mode)
        val concatAdapter = ConcatAdapter(postsHeaderAdapter, adapterWithLoadState)

        binding.postsList.layoutManager = LinearLayoutManager(context)
        binding.postsList.adapter = concatAdapter
        (binding.postsList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        mainLoadStateHolder = DefaultLoadStateAdapter.Holder(
            binding.loadStateView,
            tryAgainAction
        )

        observeErrorMessages()
        observePosts(adapter)
        observeLoadState(adapter)
        observeInvalidationEvents(adapter)

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading
                && loadState.append.endOfPaginationReached && adapter.itemCount < 1
            ) {
                binding.postsList.isVisible = false
                binding.postsEmptyBlock.isVisible = true
            } else {
                binding.postsList.isVisible = true
                binding.postsEmptyBlock.isVisible = false
            }
        }

        handleScrollingToTop(adapter)
        handleListVisibility(adapter)
    }

    private fun observePosts(adapter: PostsPagerAdapter) {
        lifecycleScope.launch {
            viewModel.postsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState(adapter: PostsPagerAdapter) {
        // you can also use adapter.addLoadStateListener
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200).collectLatest { state ->
                // main indicator in the center of the screen
                mainLoadStateHolder.bind(state.refresh)
            }
        }
    }

    private fun handleListVisibility(adapter: PostsPagerAdapter) = lifecycleScope.launch {
        // list should be hidden if an error is displayed OR if items are being loaded after the error:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.postsList.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun handleScrollingToTop(adapter: PostsPagerAdapter) = lifecycleScope.launch {
        // list should be scrolled to the 1st item (index = 0) if data has been reloaded:
        // (prev state = Loading, current state = NotLoading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.postsList.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: PostsPagerAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

    private fun observeErrorMessages() {
        viewModel.errorEvents.observeEvent(this) { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeInvalidationEvents(adapter: PostsPagerAdapter) {
        viewModel.invalidateEvents.observeEvent(this) {
            adapter.refresh()
        }
    }

}

enum class ListPostsMode {
    HOME_PAGE,
    PROFILE_OWN_PAGE
}