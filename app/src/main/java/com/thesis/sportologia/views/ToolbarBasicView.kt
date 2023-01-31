package com.thesis.sportologia.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ToolbarBasicBinding

typealias OnToolbarActionListener = (OnToolbarBasicAction) -> Unit

enum class OnToolbarBasicAction {
    LEFT,
    RIGHT
}

class ToolbarBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ToolbarBasicBinding

    private var listener: OnToolbarActionListener? = null

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
        inflater.inflate(R.layout.toolbar_basic, this, true)
        binding = ToolbarBasicBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ToolbarBasicView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getText(R.styleable.ToolbarBasicView_toolbarTitle)
        binding.title.text = title ?: "Title"

        val leftButtonText = typedArray.getText(R.styleable.ToolbarBasicView_toolbarLeftButton)
        if (leftButtonText == null) {
            binding.leftButton.visibility = GONE
        } else {
            binding.leftButton.visibility = VISIBLE
            binding.leftButton.text = leftButtonText
        }

        val rightButtonText = typedArray.getText(R.styleable.ToolbarBasicView_toolbarRightButton)
        if (rightButtonText == null) {
            binding.rightButton.visibility = GONE
        } else {
            binding.rightButton.visibility = VISIBLE
            binding.rightButton.text = rightButtonText
        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.leftButton.setOnClickListener {
            this.listener?.invoke(OnToolbarBasicAction.LEFT)
        }
        binding.rightButton.setOnClickListener {
            this.listener?.invoke(OnToolbarBasicAction.RIGHT)
        }
    }

    fun setListener(listener: OnToolbarActionListener?) {
        this.listener = listener
    }

}