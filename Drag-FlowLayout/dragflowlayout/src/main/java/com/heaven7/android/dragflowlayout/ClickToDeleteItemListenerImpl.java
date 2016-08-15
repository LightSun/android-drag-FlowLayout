package com.heaven7.android.dragflowlayout;

import android.view.MotionEvent;
import android.view.View;


/**
 * a default implements for {@link com.heaven7.android.dragflowlayout.DragFlowLayout.OnItemClickListener}
 *  that support click to delete item in {@link DragFlowLayout}
 * Created by heaven7 on 2016/8/15.
 */
public class ClickToDeleteItemListenerImpl implements DragFlowLayout.OnItemClickListener {

    private final int mDeleteViewId;

    /**
     * create ClickToDeleteItemListenerImpl with a delete id.
     * @param id the view id for delete this item
     */
    public ClickToDeleteItemListenerImpl(int id) {
        this.mDeleteViewId = id;
    }

    @Override
    public boolean performClick(DragFlowLayout dragFlowLayout, View child, MotionEvent event, int dragState) {
        //检查是否点击了关闭按钮。点击了就删除
        boolean performed = dragState != DragFlowLayout.DRAG_STATE_IDLE && ViewUtils.isViewUnderInScreen(child.findViewById(mDeleteViewId),
                (int) event.getRawX(),(int) event.getRawY());
        if(performed){
            dragFlowLayout.removeView(child);
        }
        return performed;
    }
}
