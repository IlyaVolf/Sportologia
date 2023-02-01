package com.thesis.sportologia.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewButtonRoundBinding

typealias OnButtonRoundActionListener = (OnButtonRoundAction) -> Unit

enum class OnButtonRoundAction {
    POSITIVE
}

class ButtonRoundView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewButtonRoundBinding

    private var listener: OnButtonRoundActionListener? = null

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
        inflater.inflate(R.layout.view_button_round, this, true)
        binding = ViewButtonRoundBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ButtonRoundView, defStyleAttr, defStyleRes
        )

        with(binding) {
            val icon =
                typedArray.getResourceId(
                    R.styleable.ButtonRoundView_drawable,
                    R.drawable.icon_snippet_white
                )
            button.setImageResource(icon)

            button.visibility = ConstraintLayout.VISIBLE
        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.button.setOnClickListener {
            this.listener?.invoke(OnButtonRoundAction.POSITIVE)
        }
    }

    fun setListener(listener: OnButtonRoundActionListener?) {
        this.listener = listener
    }

}