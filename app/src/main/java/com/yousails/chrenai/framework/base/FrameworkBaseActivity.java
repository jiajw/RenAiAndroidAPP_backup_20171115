package com.yousails.chrenai.framework.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.yousails.chrenai.framework.util.ToastUtil;

/**
 * 重构基类
 * User: jiajinwu
 * Date: 2017-10-27
 * Time: 13:31
 * 修改备注：
 * version:
 */


public abstract class FrameworkBaseActivity extends AppCompatActivity implements View.OnClickListener {


    public Context mContext;

    //是否允许全屏
    private boolean allowFullScreen;
    //设置沉浸式状态栏
    private boolean isSetStatusBar;
    //是否允许屏幕旋转
    private boolean isAllowScreenRote = false;


    //初始化视图
    public abstract void initContentView();

    //初始化视图
    public abstract void initView();

    //初始化数据
    public abstract void initData();

    //点击事件
    public abstract void processClick(View view);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (allowFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (isSetStatusBar) {
            steepStatusBar();
        }
        if (isAllowScreenRote) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (savedInstanceState != null) {
            initIntentParam(savedInstanceState);
        } else if (getIntent() != null && getIntent().getExtras() != null) {
            initIntentParam(getIntent().getExtras());
        }

        initContentView();
    }

    private void steepStatusBar() {

        //沉浸式状态栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        initData();
    }

    //设置是否全屏
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.allowFullScreen = allowFullScreen;
    }

    //是否设置沉浸式状态栏
    public void setSetStatusBar(boolean setStatusBar) {
        isSetStatusBar = setStatusBar;
    }

    //是否允许屏幕旋转
    public void setAllowScreenRote(boolean allowScreenRote) {
        isAllowScreenRote = allowScreenRote;
    }

    /**
     * findViewById
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findView(int id) {
        return (T) findView(id);
    }

    /**
     * intent传参
     *
     * @param savedInstanceState
     */
    protected void initIntentParam(Bundle savedInstanceState) {
    }

    @Override
    public void onClick(View v) {
        if (fastClick())
            processClick(v);

    }


    //点击事件1s内只会响应一次,防止快速点击
    private boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }


    public void toast(String str) {
        ToastUtil.makeTextCenter(mContext, str);
    }

    public void toast(int strId) {
        ToastUtil.makeTextCenter(mContext, strId);
    }

    //显示软键盘
    public void showInputMethod() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
        }

    }

    //隐藏软键盘
    public void disInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //点击键盘之外的空白处，隐藏软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            disInputMethod();

        }
        return super.onTouchEvent(event);
    }

    //startActivity
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));

    }

    public void startActivityForResult(Class<?> clz, int requestCode) {
        startActivityForResult(new Intent(this, clz), requestCode);

    }

    //startActivity of Bundle{
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent = intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    //startActivityForResult of Bundle
    public void startActivityForResult(Class<?> clz, Bundle bundle, int requestCode) {

        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent = intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);

    }


}
