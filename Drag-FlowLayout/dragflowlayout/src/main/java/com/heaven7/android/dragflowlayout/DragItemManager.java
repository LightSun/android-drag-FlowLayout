package com.heaven7.android.dragflowlayout;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * the drag item manager
 */
/*public*/ class DragItemManager<T> {

    private final DragFlowLayout dfl;

    public DragItemManager(DragFlowLayout dfl) {
        this.dfl = dfl;
    }

    /**
     * get the item count
     *
     * @return the item count
     */
    public int getItemCount() {
        return dfl.getChildCount();
    }

    /**
     * get all items
     *
     * @return the items that not removed
     */
    public List<T> getItems() {
        final DragAdapter adapter = dfl.getDragAdapter();
        List<T> list = new ArrayList<>();
        T t;
        for (int i = 0, size = getItemCount(); i < size; i++) {
            t = (T) adapter.getData(dfl.getChildAt(i));
            list.add(t);
        }
        return list;
    }

    /**
     * add order items to the last.
     *
     * @param datas the datas
     */
    public void addItems(T... datas) {
        for (int i = 0, size = datas.length; i < size; i++) {
            addItem(i, datas[i]);
        }
    }

    /**
     * add order items to the last.
     *
     * @param list the list data
     */
    public void addItems(List<T> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            addItem(i, list.get(i));
        }
    }

    /**
     * add items  from target startIndex and data.
     *
     * @param startIndex the start index to add
     * @param data       the data.
     */
    public void addItems(int startIndex, T... data) {
        if (startIndex > getItemCount()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0, size = data.length; i < size; i++) {
            addItem(startIndex + i, data[i]);
        }
    }

    /**
     * add items  from target startIndex and data.
     *
     * @param startIndex the start index to add
     * @param data       the data.
     */
    public  void addItems(int startIndex, List<T> data) {
        if (startIndex > getItemCount()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0, size = data.size(); i < size; i++) {
            addItem(startIndex + i, data.get(i));
        }
    }

    /**
     * add a item to the DragFlowLayout
     *
     * @param index the index , can be -1 if add last.
     * @param data  the data
     */
    public void addItem(int index, T data) {
        if (index < -1) {
            throw new IllegalArgumentException("index can't < -1.");
        }
        final DragAdapter mAdapter = dfl.getDragAdapter();
        final View view = View.inflate(dfl.getContext(), mAdapter.getItemLayoutId(), null);
        mAdapter.onBindData(view, dfl.getDragState(), data);
        dfl.addView(view, index);
    }

    /**
     * remove item by index
     *
     * @param index the index , you should be careful of the drag state.
     */
    public void removeItem(int index) {
        dfl.removeViewAt(index);
    }

    /**
     * remove item by child
     *
     * @param child the direct child of DragFlowLayout
     */
    public void removeItem(View child) {
        dfl.removeView(child);
    }

    /**
     * remove item by data
     *
     * @param data the data
     */
    public void removeItem(Object data) {
        final DragAdapter adapter = dfl.getDragAdapter();
        Object rawData;
        int index = DragFlowLayout.INVALID_INDXE;
        for (int size = dfl.getChildCount(), i = size - 1; i >= 0; i--) {
            rawData = adapter.getData(dfl.getChildAt(i));
            if (rawData.equals(data)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            dfl.removeViewAt(index);
        }
    }

    public void replaceAll(List<T> list) {
        dfl.removeAllViews();
        addItems(list);
    }

    /**
     * update item by index and new data.
     *
     * @param index the index
     * @param data  the data
     */
    public void updateItem(int index, T data) {
        final View view = dfl.getChildAt(index);
        dfl.getDragAdapter().onBindData(view, dfl.getDragState(), data);
    }

    /**
     * update item by previous data and new data.
     *
     * @param preData the previous data
     * @param newData the new data
     */
    public void updateItem(Object preData, Object newData) {
        final DragAdapter adapter = dfl.getDragAdapter();
        Object rawData;
        View view = null;
        for (int size = dfl.getChildCount(), i = size - 1; i >= 0; i--) {
            view = dfl.getChildAt(i);
            rawData = adapter.getData(view);
            if (rawData.equals(preData)) {
                break;
            }
        }
        if (view != null) {
            adapter.onBindData(view, dfl.getDragState(), newData);
        }
    }
}
