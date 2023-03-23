package com.thesis.sportologia.ui.image_views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class CustomImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = Math.min(measuredWidth, measuredHeight).toFloat()

        if ((measuredHeight.toFloat() / measuredWidth.toFloat()) > (4f / 3f)) {
            setMeasuredDimension((size * (4f / 4f)).toInt(), (size * (4f / 3f)).toInt())
        } else if ((measuredHeight.toFloat() / measuredWidth.toFloat()) < (1f / 4f)) {
            setMeasuredDimension((size * (4f / 4f)).toInt(), (size * (1f / 4f)).toInt())
        } else {
            setMeasuredDimension(measuredWidth, measuredHeight)
        }
    }
}