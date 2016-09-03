package com.heaven7.android.dragflowlayout;

import android.util.Log;

/**
 * the class for debug log
 * Created by heaven7 on 2016/8/8.
 */
/*public*/ class Debugger {

    private static final boolean DEBUG = false;

    private final String tag;
    private final boolean debugable;

    public Debugger(String tag,boolean debugable) {
        this.tag = tag;
        this.debugable = debugable;
    }
    public void i(String method, String msg) {
        if(DEBUG && debugable) {
            Log.i(tag, "called [ " + method + "() ]: " + msg);
        }
    }
    public  void d(String method, String msg) {
        if(DEBUG && debugable) {
            Log.d(tag, "called [ " + method + "() ]: " + msg);
        }
    }
    public  void w(String method, String msg) {
        if(DEBUG && debugable) {
            Log.w(tag, "called [ " + method + "() ]: " + msg);
        }
    }
}
