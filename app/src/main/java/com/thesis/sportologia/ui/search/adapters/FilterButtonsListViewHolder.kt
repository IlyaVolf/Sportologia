package com.thesis.sportologia.ui.search.adapters

import android.view.View.GONE
import android.view.View.VISIBLE
import com.thesis.sportologia.databinding.ItemToggleButtonBinding
import com.thesis.sportologia.ui.base.BaseViewHolder
import com.thesis.sportologia.ui.search.entities.FilterToggleButtonItem

class FilterButtonsListViewHolder(
    private val viewBinding: ItemToggleButtonBinding,
    private val onItemClick: (FilterToggleButtonItem, Boolean) -> Unit
) : BaseViewHolder<FilterToggleButtonItem>(viewBinding) {

    override fun bindItem(item: FilterToggleButtonItem) {
        viewBinding.itbButton.setOnClickListener {
            val buttonState = viewBinding.itbButton.isChecked
            onItemClick(item, buttonState)
        }

        setButtonText(item)
        restoreButtonState(item)
        adaptSpacing(item)
    }

    private fun restoreButtonState(item: FilterToggleButtonItem) {
        viewBinding.itbButton.isChecked = item.isPressed
    }

    private fun setButtonText(item: FilterToggleButtonItem) {
        viewBinding.itbButton.text = item.localizedText
        viewBinding.itbButton.textOn = item.localizedText
        viewBinding.itbButton.textOff = item.localizedText
    }

    private fun adaptSpacing(item: FilterToggleButtonItem) {
        if (item.position % 2 == 0) {
            viewBinding.itbSpaceLeft.visibility = GONE
            viewBinding.itbSpaceRight.visibility = VISIBLE
        } else {
            viewBinding.itbSpaceLeft.visibility = VISIBLE
            viewBinding.itbSpaceRight.visibility = GONE
        }
    }
}
