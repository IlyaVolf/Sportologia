package com.thesis.sportologia.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.viewpager.widget.ViewPager

class EnhancedDynamicHeightViewPager : ViewPager {
    private var mCurrentView: View? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        var height = 0
        mCurrentView?.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val h: Int = mCurrentView?.measuredHeight ?: 0
        if (h > height) height = h
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun measureCurrentView(currentView: View) {
        mCurrentView = currentView
        requestLayout()
    }

}
