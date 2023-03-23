package com.thesis.sportologia.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.thesis.sportologia.databinding.ItemToggleButtonBinding
import com.thesis.sportologia.ui.base.BaseAdapter
import com.thesis.sportologia.ui.base.BaseViewHolder
import com.thesis.sportologia.ui.search.entities.FilterToggleButtonItem

class FilterButtonsListAdapter(
    private val onItemClick: (FilterToggleButtonItem, Boolean) -> Unit
) : BaseAdapter<BaseViewHolder<FilterToggleButtonItem>, FilterToggleButtonItem>() {

    override fun takeViewHolder(parent: ViewGroup): BaseViewHolder<FilterToggleButtonItem> =
        FilterButtonsListViewHolder(
            viewBinding = ItemToggleButtonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : BaseViewHolder<FilterToggleButtonItem> =
        takeViewHolder(parent)
}