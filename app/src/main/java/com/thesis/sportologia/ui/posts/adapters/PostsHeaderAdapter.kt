package com.thesis.sportologia.ui.posts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.thesis.sportologia.utils.findTopNavController
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListPostsHeaderBinding
import com.thesis.sportologia.ui.TabsFragmentDirections
import com.thesis.sportologia.ui.posts.CreateEditPostFragment
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener

abstract class PostsHeaderAdapter(
    private val fragment: Fragment,
    private val listener: FilterListener
) : RecyclerView.Adapter<PostsHeaderAdapter.Holder>() {

    protected lateinit var binding: FragmentListPostsHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FragmentListPostsHeaderBinding.inflate(inflater, parent, false)

        disableAllItems()
        
        return Holder(fragment, renderHeader, binding)
    }

    private fun disableAllItems() {
        binding.postsFilter.root.isVisible = false
        binding.postsFilterSpace.isVisible = false
        binding.createPostButton.isVisible = false
        binding.createPostButtonSpace.isVisible = false
    }

    protected fun enablePostsFilter(athTorgF: Boolean?) {
        binding.postsFilter.root.isVisible = true
        binding.postsFilterSpace.isVisible = true
        binding.postsFilter.spinner.initAdapter(filterOptionsList, getFilterValue(athTorgF))
        binding.postsFilter.spinner.setListener(postsFilterListener)
    }
    
    protected fun enableCreatePostButton() {
        binding.createPostButton.isVisible = true
        binding.createPostButtonSpace.isVisible = true
        binding.createPostButton.setOnClickListener {
            onCreatePostButtonPressed()
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    abstract val renderHeader: () -> Unit

    interface FilterListener {

        fun filterApply(athTorgF: Boolean?)

    }

    private val filterOptionsList = listOf(
        fragment.context?.getString(R.string.filter_posts_all) ?: "",
        fragment.context?.getString(R.string.filter_posts_athletes) ?: "",
        fragment.context?.getString(R.string.filter_posts_organizations) ?: ""
    )

    private fun getFilterValue(athTorgF: Boolean?): String {
        return when (athTorgF) {
            null -> filterOptionsList[0]
            true -> filterOptionsList[1]
            false -> filterOptionsList[2]
        }
    }

    private val postsFilterListener: OnSpinnerOnlyOutlinedActionListener = {
        when (it) {
            filterOptionsList[0] -> listener.filterApply(null)
            filterOptionsList[1] -> listener.filterApply(true)
            filterOptionsList[2] -> listener.filterApply(false)
        }
    }

    private fun onCreatePostButtonPressed() {
        val direction = TabsFragmentDirections.actionTabsFragmentToEditPostFragment(
            CreateEditPostFragment.PostId(null)
        )

        fragment.findTopNavController().navigate(direction,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
    }

    class Holder(
        val fragment: Fragment,
        private val renderHeader: () -> Unit,
        private val viewBinding: FragmentListPostsHeaderBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind() {
            renderHeader()
        }

    }
}