package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
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
    BaseFragment(R.layout.fragment_list_posts_not_work_2) {

    //constructor() : this(ListPostsMode.PROFILE_OWN_PAGE)

    /*@Inject lateinit var factory: ListPostsViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(mode)
    }*/

    @Inject lateinit var factory: ListPostsViewModel.Factory

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListPostsBinding.inflate(inflater, container, false)

        mode = arguments?.getSerializable("mode") as ListPostsMode? ?: ListPostsMode.HOME_PAGE

        setupUsersList()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun setupUsersList() {
        val adapter = PostsPagerAdapter()

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)

        // combined adapter which shows both the list of posts + footer indicator when loading pages
        val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)

        val postFilterListener: OnSpinnerOnlyOutlinedActionListener = {
            Log.d("BUGFIX", "Listener: $it, $mode, ${viewModel.hashCode()}, ${this.hashCode()}")
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

        observePosts(adapter)
        observeLoadState(adapter)

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

        Log.d("BUGFIX", "Listener: $mode, ${viewModel.hashCode()}, ${this.hashCode()}," +
                "${postsHeaderAdapter.hashCode()}, ${adapterWithLoadState.hashCode()}")

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

    // ----

    /*private val onSpinnerMoreActionListener: OnSpinnerMoreActionListener = {
        when (it) {
            // ActionsMore.DELETE.action -> viewModel.
            // ActionsMore.REPORT.action ->
        }
    }

    private val onItemClick: OnItemPostActionListener = {
        when(it) {
        }
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        PostAdapter(onItemClick,onSpinnerMoreActionListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentListPostsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.createPostButton.setOnClickListener {
            onCreatePostButtonPressed()
        }
    }

    override fun observeViewModel() {
        viewModel.posts.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    binding.flpLoading.root.visibility = VISIBLE
                    binding.flpError.root.visibility = GONE
                    binding.flpContent.visibility = GONE
                }
                is DataHolder.READY -> {
                    initUIElements()

                    binding.flpLoading.root.visibility = GONE
                    binding.flpError.root.visibility = GONE
                    binding.flpContent.visibility = VISIBLE

                    if (holder.data.isEmpty()) {
                        binding.postsEmptyBlock.visibility = VISIBLE
                    } else {
                        binding.postsEmptyBlock.visibility = GONE
                    }

                    binding.postsList.adapter = adapter
                    adapter.setupItems(holder.data)
                }
                is DataHolder.ERROR -> {
                    binding.flpLoading.root.visibility = GONE
                    binding.flpError.root.visibility = VISIBLE
                    binding.flpContent.visibility = GONE
                }
            }
        }
    }

    override fun setupViews() {
        super.setupViews()
        binding.flpError.veTryAgain.setOnClickListener {
            viewModel.load()
        }
    }

    private fun initUIElements() {
        binding.postsFilter.root.visibility = GONE
    }

    */
}

enum class ListPostsMode {
    HOME_PAGE,
    PROFILE_OWN_PAGE
}