package com.thesis.sportologia.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewToolbarBasicBinding

typealias OnToolbarBasicActionListener = (OnToolbarBasicAction) -> Unit

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

    private val binding: ViewToolbarBasicBinding

    private var isUnderlined = true

    private var listeners = mutableListOf<OnToolbarBasicActionListener?>()

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
        inflater.inflate(R.layout.view_toolbar_basic, this, true)
        binding = ViewToolbarBasicBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ToolbarBasicView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getText(R.styleable.ToolbarBasicView_tb_toolbarTitle)
        binding.title.text = title ?: "Title"

        isUnderlined =
            typedArray.getBoolean(R.styleable.ToolbarBasicView_tb_isToolbarUnderlined, true)
        if (isUnderlined) {
            binding.underline.visibility = VISIBLE
        } else {
            binding.underline.visibility = INVISIBLE
        }

        val leftButtonText = typedArray.getText(R.styleable.ToolbarBasicView_tb_toolbarLeftButton)
        if (leftButtonText == null) {
            binding.leftButton.visibility = GONE
        } else {
            binding.leftButton.visibility = VISIBLE
            binding.leftButton.text = leftButtonText
        }

        val rightButtonText = typedArray.getText(R.styleable.ToolbarBasicView_tb_toolbarRightButton)
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
            listeners.forEach { listener ->
                listener?.invoke(OnToolbarBasicAction.LEFT)
            }
        }
        binding.rightButton.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnToolbarBasicAction.RIGHT)
            }
        }
    }

    fun setListener(listener: OnToolbarBasicActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnToolbarBasicActionListener?) {
        listeners.remove(listener)
    }

}