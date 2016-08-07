package com.heaven7.android.dragflowlayout;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * default drag callback
 * @param <T> the data may implements {@link IDraggable}
 * Created by heaven7 on 2016/8/7.
 */
/*public*/ class DefaultDragCallback<T> extends DragFlowLayout.Callback {

    private final DragAdapter<T> mAdapter;

    public DefaultDragCallback(DragAdapter<T> adapter) {
        this.mAdapter = adapter;
    }

    public DragAdapter getDragAdapter(){
        return mAdapter;
    }

    @Override
    public void setChildByDragState(View child, int dragState) {
        mAdapter.onBindData(child, dragState , mAdapter.getData(child));
    }

    @NonNull
    @Override
    public View createChildView(View child, int index, int dragState) {
        View view = View.inflate(child.getContext(), mAdapter.getItemLayoutId(), null);
        mAdapter.onBindData(view, dragState , mAdapter.getData(child));
        return view;
    }

    @Override
    public void setWindowViewByChild(View windowView, View child,int dragState) {
        mAdapter.onBindData(windowView, dragState , mAdapter.getData(child));
    }

    @Override
    public boolean isChildDraggable(View child) {
        final Object data = mAdapter.getData(child);
        return !(data instanceof IDraggable) || ((IDraggable) data).isDraggable();
    }
}
