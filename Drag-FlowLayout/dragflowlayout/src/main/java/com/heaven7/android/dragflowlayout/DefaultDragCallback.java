package com.heaven7.android.dragflowlayout;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.heaven7.memory.util.Cacher;

/**
 * default drag callback
 * @param <T> the data may implements {@link IDraggable}
 * Created by heaven7 on 2016/8/7.
 */
/*public*/ class DefaultDragCallback<T> extends DragFlowLayout.Callback implements IViewObserver {

    private final DragAdapter<T> mAdapter;
    private final Cacher<View,Void> mCacher = new Cacher<View, Void>() {
        @Override
        public View create(Void aVoid) {
            DragFlowLayout.sDebugger.d("createItemView","---------------");
            ViewGroup parent = getDragFlowLayout();
            return LayoutInflater.from(parent.getContext()).inflate(mAdapter.getItemLayoutId(),
                    parent, false);
        }

        @Override
        public View obtain() {
            //DragFlowLayout.sDebugger.d("obtain","current size = " + getCurrentPoolSize());
            final View view = super.obtain();
           // DragFlowLayout.sDebugger.d("obtain","parent = " + view.getParent());
            if(view.getParent() != null){
                return obtain();
            }
            return view;
        }

        @Override
        protected void onRecycleSuccess(View view) {
            removeFromParent(view);
            DragFlowLayout.sDebugger.d("onRecycleSuccess","parent = " + view.getParent()
                    + " ,child count = " + getDragFlowLayout().getChildCount());
        }
    };

    public DefaultDragCallback(DragFlowLayout parent,DragAdapter<T> adapter) {
        super(parent);
        this.mAdapter = adapter;
    }

    public DragAdapter getDragAdapter(){
        return mAdapter;
    }

    public void setMaxItemCount(int maxItemCount) {
        mCacher.setMaxPoolSize(maxItemCount);
    }
    @Override
    public void setChildByDragState(View child, int dragState) {
        mAdapter.onBindData(child, dragState , mAdapter.getData(child));
    }

    @NonNull
    @Override
    public View createChildView(View child, int index, int dragState) {
        View view = mCacher.obtain();
       // removeFromParent(view);
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

    @Override
    public void onAddView(View child, int index) {

    }
    @Override
    public void onRemoveView(View child, int index) {
        //removeFromParent(child);
         mCacher.recycle(child);
    }

    private void removeFromParent(View child) {
        final ViewParent parent = child.getParent();
        if(parent !=null && parent instanceof ViewGroup){
            IViewObserverManager vom = null;
            if(parent instanceof IViewObserverManager){
                vom = (IViewObserverManager) parent;
            }
            if(vom!=null){
                vom.enableViewObserver(false);
            }
            ((ViewGroup) parent).removeView(child);
            if(vom!=null){
                vom.enableViewObserver(true);
            }
        }
    }

    public void prepareItemsByCount(int count) {
        mCacher.setMaxPoolSize(count);
        mCacher.prepare();
    }

    public View obtainItemView() {
        return mCacher.obtain();
    }
}
