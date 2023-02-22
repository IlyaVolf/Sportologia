package com.thesis.sportologia.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
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

    private var onClickListener: OnClickListener? = null

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

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action === KeyEvent.ACTION_UP &&
            (event.keyCode === KeyEvent.KEYCODE_DPAD_CENTER || event.keyCode === KeyEvent.KEYCODE_ENTER)
        ) {
            onClickListener?.onClick(this)
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        isPressed = when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                onClickListener?.onClick(this)
                false
            }
            else -> {
                false
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        this.onClickListener = onClickListener
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

            val isIconOn = typedArray.getBoolean(R.styleable.ButtonBasicView_isIconOn, true)
            if (isIconOn) {
                drawable.visibility = VISIBLE
            } else {
                drawable.visibility = GONE
            }

            val text = typedArray.getText(R.styleable.ButtonBasicView_text)
            binding.text.text = text ?: "Description"

            val backgroundColor =
                typedArray.getColor(
                    R.styleable.ButtonBasicView_backgroundColor,
                    ContextCompat.getColor(context, R.color.element_primary)
                )
            background.backgroundTintList = ColorStateList.valueOf(backgroundColor)

            val iconColor =
                typedArray.getColor(
                    R.styleable.ButtonBasicView_iconColor,
                    ContextCompat.getColor(context, R.color.background_inverted)
                )
            drawable.setColorFilter(iconColor)

            val textColor =
                typedArray.getColor(
                    R.styleable.ButtonBasicView_textColor,
                    ContextCompat.getColor(context, R.color.background_inverted)
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