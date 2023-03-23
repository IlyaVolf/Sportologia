package com.thesis.sportologia.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class AdaptiveHeightLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
        val maxHeight = findMaxHeight(recycler, state)
        setMeasuredDimension(widthSpec, View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.EXACTLY))
    }

    private fun findMaxHeight(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var maxHeight = 0
        for (i in 0 until state.itemCount) {
            val view = recycler.getViewForPosition(i)
            measureChild(view, 0, 0)
            val height = getDecoratedMeasuredHeight(view)
            if (height > maxHeight) maxHeight = height
            recycler.recycleView(view)
        }
        return maxHeight
    }
}
