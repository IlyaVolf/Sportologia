package com.thesis.sportologia.ui.base;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

public class SmartNestedScrollView extends NestedScrollView {
    public SmartNestedScrollView(Context context) {
        super(context);
    }

    public SmartNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        int childCount = getChildCount();
        int heightNeededToDisplayAllChildren = 0;
        boolean shouldScroll = false;
        for (int i = 0; i < childCount; i++) {
            heightNeededToDisplayAllChildren += getChildAt(i).getHeight();
            if(heightNeededToDisplayAllChildren > height){
                shouldScroll = true;
                break; // no need to go through all the children once the
            }
        }

        setNestedScrollingEnabled(shouldScroll);
    }
}