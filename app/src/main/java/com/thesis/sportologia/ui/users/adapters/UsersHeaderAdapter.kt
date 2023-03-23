package com.thesis.sportologia.ui.users.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.databinding.FragmentListUsersHeaderBinding
import com.thesis.sportologia.model.users.entities.FilterParamsUsers

abstract class UsersHeaderAdapter(
    private val fragment: Fragment,
    filterParamsUsers: FilterParamsUsers,
) : RecyclerView.Adapter<UsersHeaderAdapter.Holder>() {

    protected lateinit var filterParamsUsers: FilterParamsUsers

    init {
        setFilterParamsUsers(filterParamsUsers)
    }

    @JvmName("setFilterParamsUsers1")
    fun setFilterParamsUsers(filterParamsUsers: FilterParamsUsers) {
        this.filterParamsUsers = filterParamsUsers
        notifyDataSetChanged()
    }

    protected lateinit var binding: FragmentListUsersHeaderBinding

    abstract fun createHolder(
        fragment: Fragment,
        binding: FragmentListUsersHeaderBinding,
    ): Holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        Log.d("ABCDEF", "onCreateViewHolder $filterParamsUsers")

        val inflater = LayoutInflater.from(parent.context)
        binding = FragmentListUsersHeaderBinding.inflate(inflater, parent, false)

        return createHolder(fragment, binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    abstract class Holder(
        val binding: FragmentListUsersHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        abstract val renderHeader: () -> Unit

        fun bind() {
            disableAllItems()
            renderHeader()
        }

        private fun disableAllItems() {
            binding.usersChosenFilters.root.isVisible = false
            binding.usersChosenFiltersSpace.isVisible = false
        }

        fun enableUsersChosenFilters(parser: () -> Unit) {
            binding.usersChosenFilters.root.isVisible = true
            binding.usersChosenFiltersSpace.isVisible = true

            parser()
        }

    }
}