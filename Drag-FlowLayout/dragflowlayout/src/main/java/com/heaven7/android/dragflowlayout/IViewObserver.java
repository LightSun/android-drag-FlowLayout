package com.heaven7.android.dragflowlayout;

import android.view.View;

/**
 * the view manager listener, can listen the view add and remove.
 * Created by heaven7 on 2016/8/8.
 */
public interface IViewObserver {

    void onAddView(View child, int index);

    void onRemoveView(View child, int index);
}
