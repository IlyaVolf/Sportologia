package com.thesis.sportologia.ui.image_views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class SquareImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = Math.min(measuredWidth, measuredHeight)

        if ((measuredHeight.toFloat() / measuredWidth.toFloat()) > 1) {
            setMeasuredDimension(measuredWidth, measuredWidth)
        } else {
            setMeasuredDimension(measuredWidth, measuredWidth)
        }
    }
}