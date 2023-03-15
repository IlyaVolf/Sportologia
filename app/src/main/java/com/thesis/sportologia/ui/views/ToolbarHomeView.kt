package com.thesis.sportologia.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewToolbarHomeBinding

typealias OnToolbarHomeActionListener = (OnToolbarHomeAction) -> Unit

enum class OnToolbarHomeAction {
    LEFT,
    RIGHT
}

class ToolbarHomeView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewToolbarHomeBinding

    lateinit var avatar: ImageView

    private var listeners = mutableListOf<OnToolbarHomeActionListener?>()

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
        inflater.inflate(R.layout.view_toolbar_home, this, true)
        binding = ViewToolbarHomeBinding.bind(this)
        avatar = binding.avatar

        initAttributes(attrs, defStyleAttr, defStyleRes)
        initListeners()
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ToolbarHomeView, defStyleAttr, defStyleRes
        )

        val title = typedArray.getText(R.styleable.ToolbarHomeView_th_toolbarTitle)
        binding.title.text = title ?: "Title"

        val avatar = typedArray.getResourceId(
            R.styleable.ToolbarHomeView_th_toolbarAvatar,
            R.drawable.avatar
        )
        binding.avatar.setImageResource(avatar)

        val icon = typedArray.getResourceId(R.styleable.ToolbarHomeView_th_toolbarRightButton, 0)
        if (icon == 0) {
            binding.icon.visibility = GONE
        } else {
            binding.icon.visibility = VISIBLE
            binding.icon.setImageResource(icon)
        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.avatar.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnToolbarHomeAction.LEFT)
            }
        }
        binding.icon.setOnClickListener {
            listeners.forEach { listener ->
                listener?.invoke(OnToolbarHomeAction.RIGHT)
            }
        }
    }

    fun setListener(listener: OnToolbarHomeActionListener?) {
        listeners.add(listener)
    }

    fun removeListener(listener: OnToolbarHomeActionListener?) {
        listeners.remove(listener)
    }

}