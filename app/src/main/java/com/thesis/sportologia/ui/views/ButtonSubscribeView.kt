package com.thesis.sportologia.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.thesis.sportologia.R


class ButtonSubscribeView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ButtonBasicView(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    fun setButtonPressed(isPressed: Boolean) {
        when (isPressed) {
            true -> {
                binding.drawable.setImageResource(R.drawable.icon_unsubscribe)
                binding.drawable.setColorFilter(context.getColor(R.color.white))
                binding.text.text = context.getText(R.string.action_unsubscribe)
                binding.text.setTextColor(context.getColor(R.color.white))
                binding.background.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.purple_dark))
            }
            false -> {
                binding.drawable.setImageResource(R.drawable.icon_subscribe)
                binding.drawable.setColorFilter(R.color.background_inverted)
                binding.text.text = context.getText(R.string.action_subscribe)
                binding.text.setTextColor(context.getColor(R.color.background_inverted))
                binding.background.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.element_primary))
            }
        }
    }

}