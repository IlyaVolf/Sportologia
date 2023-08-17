package com.thesis.sportologia.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ViewButtonRoundBinding

typealias OnButtonRoundActionListener = (Unit) -> Unit

class ButtonRoundView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ViewButtonRoundBinding

    private var listener: OnButtonRoundActionListener? = null

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
        inflater.inflate(R.layout.view_button_round, this, true)
        binding = ViewButtonRoundBinding.bind(this)
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
            attrs, R.styleable.ButtonRoundView, defStyleAttr, defStyleRes
        )

        with(binding) {
            val icon =
                typedArray.getResourceId(
                    R.styleable.ButtonRoundView_br_drawable,
                    R.drawable.icon_snippet_white
                )
            button.setImageResource(icon)

            button.visibility = ConstraintLayout.VISIBLE
        }

        typedArray.recycle()
    }

    private fun initListeners() {
        binding.root.setOnClickListener {
            this.listener?.invoke(Unit)
        }
    }

    fun setListener(listener: OnButtonRoundActionListener?) {
        this.listener = listener
    }

}