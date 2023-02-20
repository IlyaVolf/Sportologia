package com.thesis.sportologia.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewButtonBasicBinding

typealias OnButtonBasicActionListener = (OnButtonBasicAction) -> Unit

enum class OnButtonBasicAction {
    POSITIVE
}

class ButtonBasicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewButtonBasicBinding

    private var listener: OnButtonBasicActionListener? = null

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
        inflater.inflate(R.layout.view_button_basic, this, true)
        binding = ViewButtonBasicBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ButtonBasicView, defStyleAttr, defStyleRes
        )

        with(binding) {
            val icon =
                typedArray.getResourceId(
                    R.styleable.ButtonBasicView_icon,
                    R.drawable.icon_snippet_black
                )
            drawable.setImageResource(icon)
            //drawable.visibility = VISIBLE

            val text = typedArray.getText(R.styleable.ButtonBasicView_text)
            binding.text.text = text ?: "Description"

            val backgroundColor =
                typedArray.getColor(
                    R.styleable.ButtonBasicView_backgroundColor,
                    ContextCompat.getColor(context, R.color.grey_20)
                )
            background.backgroundTintList = ColorStateList.valueOf(backgroundColor)

            val iconColor =
                typedArray.getColor(
                    R.styleable.ButtonBasicView_iconColor,
                    ContextCompat.getColor(context, R.color.black)
                )
            drawable.setColorFilter(iconColor)

            val textColor =
                typedArray.getColor(
                    R.styleable.ButtonBasicView_textColor,
                    ContextCompat.getColor(context, R.color.black)
                )
            binding.text.setTextColor(ColorStateList.valueOf(textColor))
        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(OnButtonBasicAction.POSITIVE)
        }
    }

    fun setListener(listener: OnButtonBasicActionListener?) {
        this.listener = listener
    }

}