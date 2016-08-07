package com.heaven7.android;

import com.heaven7.android.drag.demo.DragFlowLayoutTest;

import java.util.List;

/**
 * Created by heaven7 on 2016/5/25.
 */
public class MainActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(DragFlowLayoutTest.class, "test Drag FlowLayout"));
    }
}
