package com.heaven7.android.dragflowlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * the drag flow layout
 * Created by heaven7 on 2016/8/1.
 */
public class DragFlowLayout extends FlowLayout {

    private static final String TAG = "DragGridLayout";
    private static final Debugger sDebugger = new Debugger(TAG);

    private static final int INVALID_INDXE = -1;

    /** indicate currrent is idle, and can't draggable  */
    public static final int DRAG_STATE_IDLE       = 1;
    /** indicate currrent is dragging                   */
    public static final int DRAG_STATE_DRAGGING   = 2;
    /** indicate currrent is not dragging but can drag  */
    public static final int DRAG_STATE_DRAGGABLE  = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DRAG_STATE_IDLE,  DRAG_STATE_DRAGGING , DRAG_STATE_DRAGGABLE })
    public @interface DragState{
    }

    private static final Comparator<Item> sComparator = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return compareImpl(lhs.index, rhs.index);
        }
        public int compareImpl(int lhs, int rhs) {
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    };

    private final InternalItemHelper mItemManager = new InternalItemHelper();
    private AlertWindowHelper mWindomHelper;
    private @DragState int mDragState = DRAG_STATE_IDLE;

    private DragItemManager mDragManager;
    private DefaultDragCallback mCallback;
    private OnItemClickListener mClickListener;

    /** indicate whether dispatch the event to the alert window or not. */
    private boolean mDispatchToAlertWindow;
    private final int[] mTempLocation  = new int[2];

    private CheckForDrag mCheckForDrag ;
    private CheckForRelease mCheckForRelease;

    private boolean mReDrag ;
    private volatile boolean mCancelled ;

    private GestureDetectorCompat mGestureDetector;
    private volatile View mTouchChild;

    private final AlertWindowHelper.ICallback mWindowCallback = new AlertWindowHelper.ICallback() {
        @Override
        public void onCancel(View view, MotionEvent event) {
            sDebugger.i("onCancel","------------->");
            releaseDragInternal();
        }
        @Override
        public boolean onMove(View view, MotionEvent event) {
            //infoWhenDebug("onMove","------------->");
            return processOverlap(view);
        }
    };

    public interface OnItemClickListener {
        /**
         * called when a click event occurrence ,perform the click event if you need. and return true if you performed the click event.
         * @param dragFlowLayout the DragFlowLayout
         * @param child the direct child of DragFlowLayout.
         * @param event the event of trigger this click event
         * @param dragState indicate current drag state , see {@link DragFlowLayout#DRAG_STATE_DRAGGING} and etc.
         * @return true,if you performed the click event
         */
        boolean performClick(DragFlowLayout dragFlowLayout, View child,
                                             MotionEvent event, int dragState);
    }
    /**
     * the callback of DragFlowLayout.
     */
    static abstract class Callback {

        /**
         * set the child data by target drag state.
         * @param child the direct child of DragFlowLayout
         * @param dragState the drag state of current,see {@link DragFlowLayout#DRAG_STATE_DRAGGING} and etc.
         */
        public abstract void setChildByDragState(View child, int dragState);

        /**
         * create a child view  from the target child view.
         * @param child the direct child of DragFlowLayout
         * @param index the index of this child view, or -1 for unknown index.
         * @param dragState current drag state. see {@link DragFlowLayout#DRAG_STATE_DRAGGING} and etc.
         * @return the new child view
         */
        @NonNull
        public abstract View createChildView(View child, int index, int dragState);

        /**
         * set the window view by target child view.
         * @param windowView the window view, often  like the child view.
         * @param child the direct child view of DragFlowLayout
         * @param dragState current drag state. see {@link DragFlowLayout#DRAG_STATE_DRAGGING} and etc.
         */
        public abstract void setWindowViewByChild(View windowView, View child, int dragState);

        /**
         * create window view by target child view
         * @param child the direct child view of DragFlowLayout
         * @param dragState current drag state. see {@link DragFlowLayout#DRAG_STATE_DRAGGING} and etc.
         * @return a window view that will attach to application.
         */
        public View createWindowView(View child, int dragState){
            return createChildView(child, -1, dragState);
        }

        /**
         * is the child draggable,default is true.
         * @param child the direct child of DragFlowLayout
         * @return true if the child is draggable
         */
        public boolean isChildDraggable(View child) {
            return true;
        }
    }

    public DragFlowLayout(Context context) {
        this(context,null);
    }

    public DragFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DragFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(21)
    public DragFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mWindomHelper = new AlertWindowHelper(context);
        mGestureDetector = new GestureDetectorCompat(context, new GestureListenerImpl());
    }

    /** get the drag state */
    public @DragState int getDragState(){
        return mDragState;
    }

    /***
     *  set the item click listenr
     * @param l the item listener
     */
    public void setOnItemClickListener(OnItemClickListener l) {
        this.mClickListener = l;
    }

    /** set the adapter */
    public <T> void setDragAdapter(DragAdapter<T> adapter){
        if(adapter == null){
            throw new NullPointerException();
        }
        this.mCallback = new DefaultDragCallback<T>(adapter);
    }

    /**
     * get the {@link DragItemManager} for manage the item of DragFlowLayout. eg 'CRUD'
     * @return the DragItemManager.
     */
    public DragItemManager getDragItemManager(){
        if(mDragManager == null){
            mDragManager = new DragItemManager();
        }
        return mDragManager;
    }

    /**
     * set the drag state
     * @param dragState the drag state of current,see {@link DragFlowLayout#DRAG_STATE_DRAGGING} and etc
     * @param showChildren if show all direct children of DragFlowLayout
     */
    private void setDragState(@DragState int dragState, boolean showChildren){
        checkCallback();
        this.mDragState = dragState;
        final Callback mCallback = this.mCallback;
        View view;
        for(int i=0, size = getChildCount(); i < size ;i++){
            view = getChildAt(i);
            if(showChildren && view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
            }
            mCallback.setChildByDragState(view, dragState);
        }
    }

    /** tag finish the drag state */
    public void finishDrag(){
        setDragState(DragFlowLayout.DRAG_STATE_IDLE, true);
    }

    private void checkForRelease(){
        if(mCheckForRelease == null){
            mCheckForRelease = new CheckForRelease();
        }
        postDelayed(mCheckForRelease, 100);
    }
    private void checkForDrag(long delay, boolean checkRelease){
        if(mCheckForDrag == null) {
            mCheckForDrag = new CheckForDrag();
        }
        postDelayed(mCheckForDrag, delay);
        if(checkRelease){
            checkForRelease();
        }
    }
    private void beginDragImpl(View childView){
        checkCallback();
        //impl
        childView.setVisibility(View.INVISIBLE);
        mDispatchToAlertWindow = true;
        mItemManager.findDragItem(childView);
        childView.getLocationInWindow(mTempLocation);
        mWindomHelper.showView(mCallback.createWindowView(childView, mDragState), mTempLocation[0],
                mTempLocation[1], true, mWindowCallback);
        mDragState = DRAG_STATE_DRAGGING;
    }

    /**
     * 根据指定的view,处理重叠事件
     * @param view  the target view
     * @return true 如果处理重叠成功。
     */
    private boolean processOverlap(View view) {
        final List<Item> mItems = mItemManager.mItems;
        final Callback mCallback = this.mCallback;
        Item item = null;
        int centerX, centerY;
        boolean found = false;
        for(int i=0, size = mItems.size() ; i < size ; i++){
            item = mItems.get(i);
            item.view.getLocationOnScreen(mTempLocation);
            centerX = mTempLocation[0] + item.view.getWidth()/2;
            centerY = mTempLocation[1] + item.view.getHeight()/2;
            if(isViewUnderInScreen(view, centerX, centerY, false)  && item != mItemManager.mDragItem
                   && mCallback.isChildDraggable(item.view) ){
                sDebugger.i("onMove_isViewUnderInScreen","index = " + item.index );
                /**
                 * Drag到target目标的center时，判断有没有已经hold item, 有的话，先删除旧的,
                 */
                found = true;
                break;
            }
        }
        if(found ){
            //the really index to add
            final int index = item.index;
            Item dragItem = mItemManager.mDragItem;
            // remove old
            removeView(mItemManager.mDragItem.view);
            //add hold
            View hold = mCallback.createChildView(dragItem.view, dragItem.index, mDragState);
            hold.setVisibility(View.INVISIBLE);  //隐藏
            addView(hold, index);
            //reset drag item and alert view
            mItemManager.findDragItem(hold);
            mCallback.setWindowViewByChild(mWindomHelper.getView(), mItemManager.mDragItem.view, mDragState);
            sDebugger.i("onMove","hold index = " + mItemManager.mDragItem.index);
        }
        return found;
    }

    private void releaseDragInternal(){
        checkCallback();
        if(mItemManager.mDragItem!=null) {
            mItemManager.mDragItem.view.setVisibility(View.VISIBLE);
            mCallback.setChildByDragState(mItemManager.mDragItem.view, mDragState);
        }
        mWindomHelper.releaseView();
        mDispatchToAlertWindow = false;
        mTouchChild = null;
        mDragState = DRAG_STATE_DRAGGABLE;
    }

    private void checkCallback() {
        if(mCallback == null){
            throw new IllegalStateException("you must call #setDragAdapter first.");
        }
    }

    /**
     * Find the topmost child under the given point within the parent view's coordinate system.
     *
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return The topmost child view under (x, y) or null if none found.
     */
    public View findTopChildUnder(int x, int y) {
        checkCallback();
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (ViewUtils.isViewIntersect(child,x, y))
                return child;
        }
        return null;
    }

    private boolean isViewUnderInScreen(View view, int x, int y, boolean log) {
        if (view == null) {
            return false;
        }
        int w = view.getWidth();
        int h = view.getHeight();
        view.getLocationOnScreen(mTempLocation);
        int viewX = mTempLocation[0];
        int viewY = mTempLocation[1];
        if(log) {
            sDebugger.i("isViewUnderInScreen", String.format(Locale.getDefault(),
                    "viewX = %d ,viewY = %d ,width = %d ,height = %d", viewX, viewY, w, h));
        }
        return x >= viewX && x < viewX + w
                && y >= viewY && y < viewY + h;
    }
    private void checkIfAutoReleaseDrag() {
        if(getChildCount()==0){
            releaseDragInternal();
            mDragState = DRAG_STATE_IDLE;
        }
    }

    //=================================== override method ===================================== //

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        throw new UnsupportedOperationException("you should use DragFlowLayout.OnItemClickListener instead..");
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        checkCallback();
        mItemManager.onAddView(child, index, params) ;
        mCallback.setChildByDragState(child, mDragState);
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        mItemManager.onRemoveViewAt(index);
        checkIfAutoReleaseDrag();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        mItemManager.onRemoveView(view);
        checkIfAutoReleaseDrag();
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mItemManager.onRemoveAllViews();
        checkIfAutoReleaseDrag();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mCheckForDrag);
        removeCallbacks(mCheckForRelease);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //infoWhenDebug("onTouchEvent", event.toString());
        //infoWhenDebug("onTouchEvent", "------> mDispatchToAlertWindow = " + mDispatchToAlertWindow +" ,mIsDragState = " + mIsDragState);
        mCancelled = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
        final boolean handled = mGestureDetector.onTouchEvent(event);
        //解决ScrollView嵌套DragFlowLayout时，引起的事件冲突
        if(getParent()!=null){
            getParent().requestDisallowInterceptTouchEvent(mDispatchToAlertWindow);
        }
        if(mDispatchToAlertWindow){
            mWindomHelper.getView().dispatchTouchEvent(event);
            if(mCancelled){
                mDispatchToAlertWindow = false;
            }
        }
        return handled;
    }
    //=================================== end -- override method ===================================== //

    //================================================================================

    private class CheckForDrag implements Runnable{
        @Override
        public void run() {
            if(mTouchChild != null){
                beginDragImpl(mTouchChild);
            }
        }
    }
    private class CheckForRelease implements Runnable{
        @Override
        public void run() {
            if(mCancelled) {
                releaseDragInternal();
            }
        }
    }

    private static class Item{
        int index;
        View view;

        @Override
        public String toString() {
            return "Item{" +
                    "index=" + index +
                    '}';
        }
    }

    private static class InternalItemHelper {
        final List<Item> mItems = new ArrayList<>();
        /** 对应的拖拽item */
        Item mDragItem = null;

        public void onAddView(View child, int index, LayoutParams params) {
            index = index != -1 ? index : mItems.size();
            sDebugger.d("onAddView", "index = " + index );
            Item item;
            for(int i=0,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.index >= index){
                    item.index ++;
                }
            }
            //add
            item = new Item();
            item.index = index;
            item.view = child;
            mItems.add(item);
            Collections.sort(mItems, sComparator);
            //debugWhenDebug("onAddView",mItems.toString());
        }

        public void onRemoveViewAt(int index) {
            sDebugger.d("onRemoveViewAt", "index = " + index );
            Item item;
            for(int i=0,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.index > index){
                    item.index --;
                }
            }
            mItems.remove(index);
            Collections.sort(mItems, sComparator);
           // debugWhenDebug("onAddView",mItems.toString());
        }

        public void onRemoveView(View view) {
            Item item;
            int targetIndex = INVALID_INDXE;
            for(int i=0, size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.view == view){
                    targetIndex = item.index ;
                    break;
                }
            }
            sDebugger.d("onRemoveView", "targetIndex = " + targetIndex );
            if(targetIndex == -1){
                throw new IllegalStateException("caused by targetIndex == -1");
            }
            // -- index if need
            for(int i=0,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.index > targetIndex){
                    item.index --;
                }
            }
            mItems.remove(targetIndex);
            Collections.sort(mItems, sComparator);
            //debugWhenDebug("onAddView",mItems.toString());
        }
        public void onRemoveAllViews() {
            mItems.clear();
        }

        public void findDragItem(View touchView) {
            Item item;
            for(int i=0 ,size = mItems.size() ;i<size ;i++){
                item =  mItems.get(i);
                if(item.view == touchView){
                    mDragItem = item;
                    break;
                }
            }
        }
    }
    private class GestureListenerImpl extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            mTouchChild = findTopChildUnder((int) e.getX(), (int) e.getY());
            sDebugger.i("mGestureDetector_onDown","----------------- > after find : mTouchChild = "
                    + mTouchChild);
            mReDrag = false;
            if(mTouchChild!=null && !mDispatchToAlertWindow && mDragState != DRAG_STATE_IDLE){
                if(mCallback.isChildDraggable(mTouchChild)) {
                    mReDrag = true;
                    checkForDrag(130, false);
                }
            }
            return mTouchChild != null;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            if(mClickListener == null){
                return false;
            }
            //处理点击时，看起来有点怪异的感觉(控件偏离了点位置)
            removeCallbacks(mCheckForDrag);
            boolean performed = mClickListener.performClick(DragFlowLayout.this, mTouchChild, e , mDragState);
            sDebugger.i("mGestureDetector_onSingleTapUp","----------------- > performed = " + performed);
            if(performed){
                playSoundEffect(SoundEffectConstants.CLICK);
            }else if (mReDrag) {
                checkForDrag(0, true);
            }
            return performed;
        }
        @Override
        public void onLongPress(MotionEvent e) {
            sDebugger.i("mGestureDetector_onLongPress","----------------- >");
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if(mDragState!= DRAG_STATE_DRAGGING  && mTouchChild!=null && mCallback.isChildDraggable(mTouchChild)) {
                setDragState(DRAG_STATE_DRAGGING, false);
                checkForDrag(0, false);
            }
        }
    }

    /**
     * the drag item manager
     */
    public class DragItemManager {

        DragAdapter getDragAdapter() {
            return mCallback.getDragAdapter();
        }

        /** get the item count
         * @return the item count  */
        public int getItemCount(){
            return getChildCount();
        }

        /**
         * get all items
         * @param <T> the t
         * @return the items that not removed
         */
        public <T> List<T> getItems(){
            final DragAdapter adapter = getDragAdapter();
            List<T> list = new ArrayList<>();
            T t;
            for (int i=0 ,size = getChildCount(); i < size;  i++) {
                t = (T) adapter.getData(getChildAt(i));
                list.add(t);
            }
            return list;
        }

        /**
         * add order items to the last.
         * @param datas the datas
         */
        public void addItems(Object...datas){
            for(int i=0,size = datas.length ;i<size ; i++){
                addItem(i, datas[i]);
            }
        }
        /**
         * add order items to the last.
         * @param list the list data
         *  @param <T> the t
         */
        public <T> void addItems(List<T> list){
            for(int i=0,size = list.size() ;i<size ; i++){
                addItem(i, list.get(i));
            }
        }

        /**
         * add items  from target startIndex and data.
         * @param startIndex the start index to add
         * @param data the data.
         */
        public void addItems(int startIndex, Object...data){
            if(startIndex > getItemCount()){
                throw new IllegalArgumentException();
            }
            for(int i=0,size = data.length ;i<size ; i++){
                addItem(startIndex + i, data[i]);
            }
        }
        /**
         * add items  from target startIndex and data.
         * @param startIndex the start index to add
         * @param data the data.
         * @param <T> the t
         */
        public <T> void addItems(int startIndex, List<T> data){
            if(startIndex > getItemCount()){
                throw new IllegalArgumentException();
            }
            for(int i=0,size = data.size() ;i<size ; i++){
                addItem(startIndex + i, data.get(i));
            }
        }

        /**
         * add a item to the DragFlowLayout
         * @param index the index , can be -1 if add last.
         * @param data  the data
         */
        public void addItem(int index, Object data) {
            if (index < -1) {
                throw new IllegalArgumentException("index can't < -1.");
            }
            final DragAdapter mAdapter = getDragAdapter();
            final View view = View.inflate(getContext(), mAdapter.getItemLayoutId(), null);
            mAdapter.onBindData(view, getDragState(), data);
            addView(view, index);
        }

        /**
         * remove item by index
         * @param index the index , you should be careful of the drag state.
         */
        public void removeItem(int index) {
            removeViewAt(index);
        }

        /**
         * remove item by data
         * @param data the data
         */
        public void removeItem(Object data) {
            final DragAdapter adapter = getDragAdapter();
            Object rawData;
            int index = INVALID_INDXE;
            for (int size = getChildCount(), i = size - 1; i >= 0; i--) {
                rawData = adapter.getData(getChildAt(i));
                if (rawData.equals(data)) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                removeViewAt(index);
            }
        }
        /**
         * update item by index and new data.
         * @param index the index
         * @param data  the data
         */
        public void updateItem(int index, Object data) {
            final View view = getChildAt(index);
            getDragAdapter().onBindData(view, getDragState(), data);
        }

        /**
         * update item by previous data and new data.
         * @param preData the previous data
         * @param newData the new data
         */
        public void updateItem(Object preData, Object newData) {
            final DragAdapter adapter = getDragAdapter();
            Object rawData;
            View view = null;
            for (int size = getChildCount(), i = size - 1; i >= 0; i--) {
                view = getChildAt(i);
                rawData = adapter.getData(view);
                if (rawData.equals(preData)) {
                    break;
                }
            }
            if (view != null) {
                getDragAdapter().onBindData(view,getDragState(),newData);
            }
        }
    }


}
