package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListPostsBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.ui.adapters.PostAdapter
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ListPostsFragment : BaseFragment(R.layout.fragment_list_posts) {

    override val viewModel by viewModels<ListPostsViewModel>()

    private lateinit var binding: FragmentListPostsBinding

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        PostAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentListPostsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.createPostButton.setOnClickListener {
            onCreatePostButtonPressed()
        }
    }

    override fun observeViewModel() {
        viewModel.affichePosts.observe(viewLifecycleOwner) { holder ->
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

    private fun onCreatePostButtonPressed() {
        findTopNavController().navigate(R.id.create_post,
            null,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
    }

}