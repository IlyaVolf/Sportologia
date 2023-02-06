package com.thesis.sportologia.views

import com.thesis.sportologia.databinding.ViewChosenFiltersBinding
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

import com.thesis.sportologia.R

class ChosenFilterView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewChosenFiltersBinding

    private var isRestrictionsBlockEnabled = false
    private var isSortingBlockEnabled = false

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_chosen_filters, this, true)
        binding = ViewChosenFiltersBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        visualizeBlocks()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ChosenFilterView, defStyleAttr, defStyleRes
        )

        isRestrictionsBlockEnabled =
            typedArray.getBoolean(R.styleable.ChosenFilterView_cf_isRestrictionsBlockEnabled, false)
        isSortingBlockEnabled =
            typedArray.getBoolean(R.styleable.ChosenFilterView_cf_isSortingBlockEnabled, false)

        typedArray.recycle()
    }

    private fun visualizeBlocks() {
        if (isRestrictionsBlockEnabled) {
            binding.restrictionsBlock.visibility = VISIBLE
        } else {
            binding.restrictionsBlock.visibility = GONE
        }

        if (isSortingBlockEnabled) {
            binding.sortingBlock.visibility = VISIBLE
        } else {
            binding.sortingBlock.visibility = GONE
        }
    }

    fun setRestrictionsBlock(isRestrictionsBlockEnabled: Boolean, restrictions: List<String>) {
        visualizeBlocks()

        if (isRestrictionsBlockEnabled) {

            var text = ""

            var isAnyChecked = false
            for (i in restrictions.indices) {
                text += restrictions[i] + " • "
                isAnyChecked = true
            }

            if (isAnyChecked) {
                text = text.substring(0, text.length - 2)
            }

            binding.restrictions.text = text
        }
    }

    fun setSortingBlock(isSortingBlockEnabled: Boolean, sorting: String) {
        visualizeBlocks()

        if (isSortingBlockEnabled) {
            binding.restrictions.text = sorting
        }
    }

}