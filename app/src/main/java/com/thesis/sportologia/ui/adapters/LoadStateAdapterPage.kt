package com.thesis.sportologia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thesis.sportologia.databinding.ViewLoadStateBinding

/**
 * This adapter is used for rendering the load state (ProgressBar, error message and Try Again button)
 * in the list's header and footer.
 */
class LoadStateAdapterPage(
    private val tryAgainAction: TryAgainAction
) : LoadStateAdapter<LoadStateAdapterPage.Holder>() {

    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewLoadStateBinding.inflate(inflater, parent, false)

        return Holder(binding, null, tryAgainAction)
    }

    /**
     * The same layout is used for:
     * - footer
     * - main indicator
     */
    class Holder(
        private val binding: ViewLoadStateBinding,
        private val swipeRefreshLayout: SwipeRefreshLayout?,
        private val tryAgainAction: TryAgainAction
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.flpError.veTryAgain.setOnClickListener { tryAgainAction() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            // если есть swipeRefreshLayout, то не зачем показывать свой flpLoading
            if (swipeRefreshLayout != null) {
                flpError.root.isVisible = loadState is LoadState.Error
                swipeRefreshLayout.isRefreshing = loadState is LoadState.Loading
                flpLoading.root.isVisible = false
            } else {
                //flpLoading.root.isVisible = loadState is LoadState.Loading
            }
        }
    }

}