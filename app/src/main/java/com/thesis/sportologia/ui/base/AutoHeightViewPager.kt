package com.thesis.sportologia.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec
import androidx.viewpager.widget.ViewPager


class AutoHeightViewPager(context: Context, attrs: AttributeSet) :
    ViewPager(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // find the current child view
        // and you must cache all the child view
        // use setOffscreenPageLimit(adapter.getCount())
        val view: View? = getChildAt(currentItem)
        view?.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measureHeight(heightMeasureSpec, view))
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @param view the base view with already measured height
     *
     * @return The height of the view, honoring constraints from measureSpec
     */
    private fun measureHeight(measureSpec: Int, view: View?): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            // set the height from the base view if available
            if (view != null) {
                result = view.measuredHeight
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    /**
     * Separate measurement view to obtain size
     *
     * @param view
     */
    fun measeureView(view: View) {
        val intw: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val inth: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        // Re-measure view
        view.measure(intw, inth)

        // The above 3 sentences can be abbreviated as the following sentence
        //view.measure(0,0);

        // Get the measured view size
        val intwidth: Int = view.measuredWidth
        val intheight: Int = view.measuredHeight
    }
}