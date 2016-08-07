package com.heaven7.android.dragflowlayout;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * drag adapter, this class is unlike the {@link android.widget.BaseAdapter} or other adapter.
 * and can't notify data change. if you should 'CRUD' item please use
 * @param <T> the data
 */
public abstract class DragAdapter<T> {

    /**
     * get the item layout id.
     * @return the id of item
     */
    @LayoutRes
    public abstract int getItemLayoutId();

    /**
     * bind data to the view ,but you should not set onClickListener or onLongClickListener for it's children.
     * and instead you should use {@link DragFlowLayout#setOnItemClickListener(DragFlowLayout.OnItemClickListener)}.
     * @param itemView the item view which is a direct child of DragFlowLayout
     * @param dragState the drag state. see {@link DragFlowLayout#DRAG_STATE_DRAGGING} and etc.
     * @param data  the data
     */
    public abstract void onBindData(View itemView, int dragState, T data);

    /**
     * get the data by target child view, which is a direct child of DragFlowLayout.
     * @param itemView the item view
     * @return the bind data.
     */
    @NonNull
    public abstract  T getData(View itemView);

}
