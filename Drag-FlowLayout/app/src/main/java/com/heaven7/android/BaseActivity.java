package com.heaven7.android;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.heaven7.core.util.Toaster;
import com.heaven7.core.util.ViewHelper;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    private Toaster mToaster;
    private ViewHelper mViewHelper;
    private IntentExecutor mIntentExecutor;

    protected abstract int getlayoutId();

    protected abstract void initView();

    protected abstract void initData(Bundle savedInstanceState);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentExecutor = new IntentExecutor();
        mToaster = new Toaster(this);
        mViewHelper = new ViewHelper(getWindow().getDecorView());

        onPreSetContentView();
        setContentView(getlayoutId());
        ButterKnife.bind(this);

        initView();
        initData(savedInstanceState);
    }

    protected void onPreSetContentView(){}

    @Override
    protected void onStop() {
        super.onStop();
      //  mHttpExecutor.cancelAll();
    }

    protected void showToast(String msg){
        mToaster.show(msg);
    }
    protected void showToast(int resID){
        mToaster.show(resID);
    }
    protected Toaster getToaster(){
        return mToaster;
    }
    public ViewHelper getViewHelper(){
        return mViewHelper;
    }

    public IntentExecutor getIntentExecutor() {
        return mIntentExecutor;
    }

    //================================
    protected void replaceFragment(int containerViewId, Fragment fragment) {
        replaceFragment(containerViewId, fragment, true);
    }

    protected void replaceFragment(int containerViewId, Fragment fragment, boolean addToback) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(containerViewId, fragment);
        if (addToback) {
            ft.addToBackStack(fragment.getClass().getName());
        }
        ft.commit();
    }

    protected void replaceFragmentByPreviousIfExist(int containerViewId, Fragment fragment, boolean addBack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        String tag = fragment.getClass().getName();
        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag(tag);
        ft.replace(containerViewId, previousFragment != null ? previousFragment : fragment, tag);
        if (addBack)
            ft.addToBackStack(null);
        ft.commit();
    }

    protected void setCommonBackListener(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    public class IntentExecutor{

        public void launchActivity(Class<? extends Activity> clazz, int intentFlags) {
            startActivity(new Intent(BaseActivity.this, clazz).addFlags(intentFlags));
        }

        public void launchActivity(Class<? extends Activity> clazz) {
            startActivity(new Intent(BaseActivity.this, clazz));
        }

        public void launchActivity(Class<? extends Activity> clazz, Bundle data) {
            launchActivity(clazz, data, 0);
        }

        public void launchActivity(Class<? extends Activity> clazz, Bundle data, int intentFlags) {
            startActivity(new Intent(BaseActivity.this, clazz).putExtras(data).addFlags(intentFlags));
        }

        public void launchActivityForResult(Class<? extends Activity> clazz, int requestCode) {
            startActivityForResult(new Intent(BaseActivity.this, clazz), requestCode);
        }

        public void launchService(Class<? extends Service> clazz, Bundle data){
            startService(new Intent(BaseActivity.this,clazz).putExtras(data));
        }

    }

}
