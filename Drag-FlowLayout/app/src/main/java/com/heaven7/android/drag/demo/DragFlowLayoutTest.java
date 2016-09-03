package com.heaven7.android.drag.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.heaven7.android.BaseActivity;
import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl;
import com.heaven7.android.dragflowlayout.DragAdapter;
import com.heaven7.android.dragflowlayout.DragFlowLayout;
import com.heaven7.android.dragflowlayout.IDraggable;
import com.heaven7.android.dragflowlayout.IViewObserver;
import com.heaven7.core.util.Logger;

import butterknife.InjectView;
import butterknife.OnClick;

/** 拖拽流布局 示例程序
 * Created by heaven7 on 2016/8/1.
 */
public class DragFlowLayoutTest extends BaseActivity {

    private static final String TAG = "DragFlowLayoutTest";

    @InjectView(R.id.drag_flowLayout)
    DragFlowLayout mDragflowLayout;

    private int mIndex;
    @Override
    protected int getlayoutId() {
        return R.layout.ac_drag_flow_test2;
    }

    @Override
    protected void initView() {
        mDragflowLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.iv_close){
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
                //删除成功后的处理。
            }
        });
        mDragflowLayout.setDragAdapter(new DragAdapter<TestBean>() {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_drag_flow;
            }
            @Override
            public void onBindData(View itemView, int dragState, TestBean data) {
                itemView.setTag(data);

                TextView tv = (TextView) itemView.findViewById(R.id.tv_text);
                tv.setText(data.text);
                //iv_close是关闭按钮。只有再非拖拽空闲的情况吓才显示
                itemView.findViewById(R.id.iv_close).setVisibility(
                        dragState!= DragFlowLayout.DRAG_STATE_IDLE
                        && data.draggable ? View.VISIBLE : View.INVISIBLE);
            }
            @NonNull
            @Override
            public TestBean getData(View itemView) {
                return (TestBean) itemView.getTag();
            }
        });
        //预存指定个数的Item. 这些Item view会反复使用，避免重复创建
        mDragflowLayout.prepareItemsByCount(10);
        //设置拖拽状态监听器
        mDragflowLayout.setOnDragStateChangeListener(new DragFlowLayout.OnDragStateChangeListener() {
            @Override
            public void onDragStateChange(DragFlowLayout dfl, int dragState) {
                System.out.println("on drag state change : dragState = " + dragState);
            }
        });
        //添加view观察者
        mDragflowLayout.addViewObserver(new IViewObserver() {
            @Override
            public void onAddView(View child, int index) {
                Logger.i(TAG, "onAddView", "index = " + index);
            }
            @Override
            public void onRemoveView(View child, int index) {
               Logger.i(TAG, "onRemoveView", "index = " + index);
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
    @OnClick(R.id.bt_done)
    public void onClickDone(View v){
        mDragflowLayout.finishDrag();
    }

    @OnClick(R.id.bt_add)
    public void onClickAdd(View v){
        final TestBean bean = new TestBean("test_" + (mIndex++));
        int index;
        if(mDragflowLayout.getChildCount()==0) {
            //为了测试，设置第一个条目不准拖拽
            bean.draggable = false;
            index = 0;
        }else{
            index = 1;
        }
        mDragflowLayout.getDragItemManager().addItem(index, bean);
    }
    @OnClick(R.id.bt_remove_center)
    public void onClickRemoveCenter(View v){
        final DragFlowLayout.DragItemManager itemManager = mDragflowLayout.getDragItemManager();
        final int count = itemManager.getItemCount();
        if(count == 0){
            return;
        }
        if(count <=2) {
            itemManager.removeItem(count-1);
        }else{
            itemManager.removeItem(count-2);
        }
    }

    /** 如果想禁止某些Item拖拽请实现 {@link IDraggable} 接口 */
    private static class  TestBean implements IDraggable{
        String text;
        boolean draggable = true;
        public TestBean(String text) {
            this.text = text;
        }
        @Override
        public boolean isDraggable() {
            return draggable;
        }
    }
}
