package com.heaven7.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.Toaster;

/**
 * Created by heaven7 on 2016/8/23.
 */
public class SimpleService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("SimpleService", "onCreate");
        Toaster.show(this, "NetstateChangeReceiver_onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }
}
