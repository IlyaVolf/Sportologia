package com.thesis.sportologia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.databinding.PartDefaultLoadStateBinding

/**
 * Action to be executed when Try Again button is pressed
 */
typealias TryAgainAction = () -> Unit

/**
 * This adapter is used for rendering the load state (ProgressBar, error message and Try Again button)
 * in the list's header and footer.
 */
class DefaultLoadStateAdapter(
    private val tryAgainAction: TryAgainAction
) : LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PartDefaultLoadStateBinding.inflate(inflater, parent, false)

        return Holder(binding, tryAgainAction)
    }

    /**
     * The same layout is used for:
     * - footer
     * - main indicator
     */
    class Holder(
        private val binding: PartDefaultLoadStateBinding,
        private val tryAgainAction: TryAgainAction
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.flpError.veTryAgain.setOnClickListener { tryAgainAction() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            flpError.root.isVisible = loadState is LoadState.Error
            flpLoading.root.isVisible = loadState is LoadState.Loading
        }
    }

}