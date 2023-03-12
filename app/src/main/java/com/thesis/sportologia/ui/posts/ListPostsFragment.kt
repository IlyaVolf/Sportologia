package com.thesis.sportologia.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListPostsBinding
import com.thesis.sportologia.ui.CreateEditPostFragment
import com.thesis.sportologia.ui.ProfileFragment
import com.thesis.sportologia.ui.adapters.*
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.posts.adapters.PostsHeaderAdapter
import com.thesis.sportologia.ui.posts.adapters.PostsPagerAdapter
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
class ListPostsFragment :
    BaseFragment(R.layout.fragment_list_posts) {

    // TODO Переделать в abstract class!
    // TODO save scroll position when navigating
    // TODO avoid list invalidation when edited post

    @Inject
    lateinit var factory: ListPostsViewModel.Factory

    private lateinit var mode: ListPostsMode
    private var userId by Delegates.notNull<String>()

    override val viewModel by viewModelCreator {
        factory.create(mode, userId)
    }

    private lateinit var binding: FragmentListPostsBinding
    private lateinit var mainLoadStateHolder: LoadStateAdapterPage.Holder

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(mode: ListPostsMode, userId: String): ListPostsFragment {
            val myFragment = ListPostsFragment()
            val args = Bundle()
            args.putSerializable("mode", mode)
            args.putString("userId", userId)
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
        userId = arguments?.getString("userId") ?: throw Exception()

        initResultsProcessing()
        initSwipeToRefresh()
        val adapter = initPostsList()

        observeErrorMessages()
        observePosts(adapter)
        observeLoadState(adapter)
        observeInvalidationEvents(adapter)

        handleScrollingToTop(adapter)
        handleListVisibility(adapter)

        return binding.root
    }

    private fun initResultsProcessing() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditPostFragment.IS_CREATED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isSaved = data.getBoolean(CreateEditPostFragment.IS_CREATED)
            if (isSaved) {
                viewModel.onPostCreated()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditPostFragment.IS_EDITED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isSaved = data.getBoolean(CreateEditPostFragment.IS_EDITED)
            if (isSaved) {
                viewModel.onPostEdited()
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

    /*override fun onResume() {
        super.onResume()

        val layoutView = view!!.findViewById<View>(R.id.layout)
        if (layoutView != null) {
            val layoutParams = layoutView.layoutParams
            if (layoutParams != null) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutView.requestLayout()
            }
        }
    }*/

    private fun initPostsList(): PostsPagerAdapter {
        val adapter = PostsPagerAdapter(this, mode, viewModel)

        // in case of loading errors this callback is called when you tap the 'Try Again' button
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = LoadStateAdapterPaging(tryAgainAction)
        val headerAdapter = LoadStateAdapterPaging(tryAgainAction)

        // combined adapter which shows both the list of posts + footer indicator when loading pages
        val adapterWithLoadState =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)

        val postsHeaderAdapter = PostsHeaderAdapter(this, mode, viewModel)
        val concatAdapter = ConcatAdapter(postsHeaderAdapter, adapterWithLoadState)

        val swipeRefreshLayout = if (mode == ListPostsMode.PROFILE_OWN_PAGE || mode == ListPostsMode.PROFILE_OTHER_PAGE) {
            null
        } else {
            binding.swipeRefreshLayout
        }

        binding.postsList.layoutManager = LinearLayoutManager(context)
        binding.postsList.adapter = concatAdapter
        (binding.postsList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        mainLoadStateHolder = LoadStateAdapterPage.Holder(
            binding.loadStateView,
            swipeRefreshLayout,
            tryAgainAction
        )

        return adapter
    }

    private fun initSwipeToRefresh() {
         if (mode != ListPostsMode.PROFILE_OWN_PAGE && mode != ListPostsMode.PROFILE_OTHER_PAGE) {
             binding.swipeRefreshLayout.isEnabled = true
             binding.swipeRefreshLayout.setOnRefreshListener {
                 viewModel.refresh()
             }
         } else {
             binding.swipeRefreshLayout.isEnabled = false
         }
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
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading
                    && viewModel.scrollEvents.value?.get() != null
                ) {
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
    PROFILE_OTHER_PAGE,
    PROFILE_OWN_PAGE,
    FAVOURITES_PAGE
}