package com.thesis.sportologia.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.thesis.sportologia.databinding.Ro

class ButtonRoundView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: ButtonRoundBinding

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
        inflater.inflate(R.layout.button_round, this, true)
        binding = ButtonRoundView.bind()
    }

}