package com.heaven7.android.dragflowlayout;

import android.view.MotionEvent;
import android.view.View;


/**
 * a default implements for {@link com.heaven7.android.dragflowlayout.DragFlowLayout.OnItemClickListener}
 * that support click to delete item in {@link DragFlowLayout}. you should call {@link #onDeleteSuccess(DragFlowLayout, View, Object)}
 * to handle your logic.
 * Created by heaven7 on 2016/8/15.
 */
public class ClickToDeleteItemListenerImpl implements DragFlowLayout.OnItemClickListener {

    private final int mDeleteViewId;

    /**
     * create ClickToDeleteItemListenerImpl with a delete id.
     *
     * @param id the view id for delete this item
     */
    public ClickToDeleteItemListenerImpl(int id) {
        this.mDeleteViewId = id;
    }

    @Override
    public boolean performClick(DragFlowLayout dragFlowLayout, View child, MotionEvent event, int dragState) {
        //检查是否点击了关闭按钮。点击了就删除
        final View mDeleteView = child.findViewById(mDeleteViewId);
        boolean performed = dragState != DragFlowLayout.DRAG_STATE_IDLE && mDeleteView.getVisibility() == View.VISIBLE
                && ViewUtils.isViewUnderInScreen(mDeleteView, (int) event.getRawX(), (int) event.getRawY());
        if (performed) {
            dragFlowLayout.postDelayed(new DeleteRunnable(dragFlowLayout, child), 60);
        }
        return true;
    }

    /**
     * called when delete success
     *
     * @param dfl   the DragFlowLayout
     * @param child the direct child of DragFlowLayout
     * @param data  the data from child , from {@link DragAdapter#getData(View)}
     */
    protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {

    }

    private class DeleteRunnable implements Runnable {
        private final DragFlowLayout mParent;
        private final View mChild;

        public DeleteRunnable(DragFlowLayout mParent, View mChild) {
            this.mParent = mParent;
            this.mChild = mChild;
        }

        @Override
        public void run() {
            Object data = mParent.getDragAdapter().getData(mChild);
            mParent.removeView(mChild);
            onDeleteSuccess(mParent, mChild, data);
        }
    }
}
