package com.thesis.sportologia.views

import android.R.attr.maxLength
import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.EditTextBasicBinding


typealias OnEditTextBasicActionListener = (OnEditTextBasicAction) -> Unit

enum class OnEditTextBasicAction {
    POSITIVE
}

class EditTextBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: EditTextBasicBinding

    private var listener: OnEditTextBasicActionListener? = null

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
        inflater.inflate(R.layout.edit_text_basic, this, true)
        binding = EditTextBasicBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.EditTextBasicView, defStyleAttr, defStyleRes
        )

        with(binding) {
            val title = typedArray.getString(R.styleable.EditTextBasicView_title)
            binding.title.text = title

            val lines = typedArray.getInteger(R.styleable.EditTextBasicView_lines,0)
            if (lines > 0) {
                textBlock.setLines(lines)
            }

            val limit = typedArray.getInteger(R.styleable.EditTextBasicView_limit,0)
            if (limit > 0) {
                textBlock.filters = arrayOf<InputFilter>(LengthFilter(limit))
            }

            val hint = typedArray.getString(R.styleable.EditTextBasicView_hint)
            textBlock.hint = hint

        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnEditTextBasicAction.POSITIVE)
        }
    }

    fun setListener(listener: OnEditTextBasicActionListener?) {
        this.listener = listener
    }

}