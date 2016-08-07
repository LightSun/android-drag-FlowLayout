package com.heaven7.android.dragflowlayout;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * the alert window helper
 * Created by heaven7 on 2016/8/4.
 */
public class AlertWindowHelper {

    private static final String TAG = "AlertWindowHelper";
    private static final Debugger sDebugger = new Debugger(TAG);

    private final WindowManager mWm;
    private final WindowManager.LayoutParams mParams;
    private final int mStateBarHeight;


    private View mView;
    private ICallback mCallback;

    /**
     * the drag event callback
     */
    public interface ICallback{
        void onCancel(View v, MotionEvent event);
        boolean onMove(View view, MotionEvent event);
    }

    public AlertWindowHelper(Context context) {
        this.mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mParams = createWindowParams();
        this.mStateBarHeight = ViewUtils.getStatusHeight(context);
    }
    public View getView() {
         return mView;
    }
    private WindowManager.LayoutParams createWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.LEFT | Gravity.TOP; //这是窗体的原点位置,如果设置为CENTER默认,远点会在屏幕中间
        params.x = 0 ;
        params.y = 0;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;      //focusable不能去,如果去了后面的窗口就都不能操作了
        // params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//需要权限SYSTEM_ALERT_WINDOW; 6.0以后弹出窗体会异常
        // params.setTitle("Toast");
       // mWM.addView(view, params);//将View对象添加到窗体上显示
        return params;
    }

    /**
     * show the view by target position.
     * @param view the view in window
     * @param initLeft the init eft
     * @param initTop the init top
     */
    public void showView(View view, int initLeft, int initTop){
        showView(view , initLeft, initTop, false , null);
    }
    /**
     * show the view by target position.
     * @param view the view in window
     * @param initLeft the init eft
     * @param initTop the init top
     * @param useInternalDrag if use drag in internal. if true it will set the internal onTouch listener.
     * @param callback callback ,can be null
     */
    public void showView(View view, int initLeft, int initTop, boolean useInternalDrag, ICallback callback){
        releaseView();
        this.mView = view;
        if(callback!=null){
            mCallback = callback;
        }
        if(useInternalDrag){
            view.setOnTouchListener(new DragTouchListener());
        }
        mParams.x = initLeft;
        mParams.y = initTop;
        mWm.addView(view, mParams);
    }

    /**
     * update the view
     * @param initLeft the left
     * @param initTop the top
     */
    public void updateViewLayout(int initLeft, int initTop){
        if(mView ==null ){
            throw new IllegalStateException("must call #showView first");
        }
        mParams.x = initLeft;
        mParams.y = initTop;
        mWm.updateViewLayout(mView, mParams);
    }

    public void releaseView(){
        if(mView != null) {
            mView.setOnTouchListener(null);
            mWm.removeView(mView);
            mView = null;
        }
    }

    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final ICallback mCallback = AlertWindowHelper.this.mCallback;
            if(event.getAction()== MotionEvent.ACTION_CANCEL || event.getAction()== MotionEvent.ACTION_UP){
                if(mCallback!=null){
                    mCallback.onCancel(v, event);
                }
                return false;
            }
            // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
            mParams.x = (int) (event.getRawX() - mView.getMeasuredWidth() * 1f / 2 );
            mParams.y = (int) (event.getRawY() - mView.getMeasuredHeight() * 1f / 2 - mStateBarHeight);
            sDebugger.i(TAG, "===== mStateBarHeight(" + mStateBarHeight +") =====>>> X = "+ event.getX() + " ,RawX = " + event.getRawX()
                    + " ,Y = "+ event.getY()+" ,RawY =" + event.getRawY());
            // 刷新
            mWm.updateViewLayout(mView, mParams);
            //onTouch和onClick冲突 ,可通过v.performClick()去解决
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                return mCallback != null &&  mCallback.onMove(v,event);
            }
            return false;
        }
    }

}
