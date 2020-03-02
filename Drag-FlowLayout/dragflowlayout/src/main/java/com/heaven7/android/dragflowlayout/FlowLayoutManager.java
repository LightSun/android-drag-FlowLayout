package com.heaven7.android.dragflowlayout;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by heaven7 on 2016/10/27.
 */
public class FlowLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                          int widthSpec, int heightSpec) {
      //  super.onMeasure(recycler, state, widthSpec, heightSpec);
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        int width = 0;
        int height = 0;

        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
            case View.MeasureSpec.AT_MOST:
                width = widthSize;
                break;
            case View.MeasureSpec.UNSPECIFIED:
            default:
                //width = ViewCompat.getMinimumWidth(getv);
                break;
        }

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
            case View.MeasureSpec.AT_MOST:
                height = heightSize;
                break;
            case View.MeasureSpec.UNSPECIFIED:
            default:
               // height = ViewCompat.getMinimumHeight(this);
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }
}
