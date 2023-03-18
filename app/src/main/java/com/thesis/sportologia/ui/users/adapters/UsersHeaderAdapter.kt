package com.thesis.sportologia.ui.users.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.databinding.FragmentListUsersHeaderBinding

abstract class UsersHeaderAdapter(
    private val fragment: Fragment,
) : RecyclerView.Adapter<UsersHeaderAdapter.Holder>() {

    protected lateinit var binding: FragmentListUsersHeaderBinding

    open val restrictionsParser: () -> Unit = {}
    open val sortingParser: () -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FragmentListUsersHeaderBinding.inflate(inflater, parent, false)

        disableAllItems()
        setChosenFilterText()

        return Holder(fragment, renderHeader, binding)
    }

    private fun disableAllItems() {
        binding.usersChosenFilters.root.isVisible = false
        binding.usersChosenFiltersSpace.isVisible = false
    }

    fun enableUsersChosenFilters() {
        binding.usersChosenFilters.root.isVisible = true
        binding.usersChosenFiltersSpace.isVisible = true

        setChosenFilterText()
    }

    private fun setChosenFilterText() {
        restrictionsParser()
        sortingParser()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    abstract val renderHeader: () -> Unit

    class Holder(
        val fragment: Fragment,
        private val renderHeader: () -> Unit,
        viewBinding: FragmentListUsersHeaderBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind() {
            renderHeader()
        }

    }
}