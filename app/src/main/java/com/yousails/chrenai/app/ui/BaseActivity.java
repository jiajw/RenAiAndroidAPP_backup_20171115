package com.yousails.chrenai.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.yousails.chrenai.config.ModelApplication;
import com.yousails.chrenai.im.DemoHelper;
import com.yousails.chrenai.login.bean.UserBean;


/**
 * Created by Administrator on 2017/6/16.
 */

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    /**
     * 全局应用Context
     */
    protected BaseActivity mContext;

    /**
     * TAG，动态生成类名
     */
    protected String TAG = "BaseActivity";


    /**
     * handler
     */
    protected Handler mHandler;
    protected ModelApplication application;
    protected UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        TAG = this.getClass().getName();
        mContext = this;
        application = ModelApplication.getInstance();
        application.getActivityManager().pushActivity(this);
        init();
        findViews();
        initViews();
        setListeners();
        handleMessage();
        initData();
//        Logger.i(TAG, "创建");
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        DemoHelper.getInstance().getNotifier().reset();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void initViews() {

    }

    /**
     * 设置布局
     */
    protected abstract void setContentView();

    /**
     * 初始化，setContentView后调用
     */
    protected abstract void init();

    /**
     * 查找视图。init后调用；
     */
    protected abstract void findViews();

    /**
     * 设置侦听器，findViews后调用
     */
    protected abstract void setListeners();

    /**
     * handler刷新界面，setListeners后调用
     */
    protected abstract void handleMessage();

    public abstract void initData();

    /**
     * ondestory中销毁activity前,释放activity中的message\request\com.yousails.chrenai.thread\注册侦听等占用资源的东西，
     * 避免空指针或内存泄露
     */
    protected void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        mContext = null;
    }

    /**
     * <p>
     * Title: onDestroy
     * </p>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        ModelApplication application = (ModelApplication) this.getApplication();
        application.getActivityManager().popActivity(this);
//        Logger.i(TAG, "销毁");
    }

    /**
     * <p>
     * Title: onClick
     * </p>
     * <p>
     * Description:点击事件逻辑处理
     * </p>
     */
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ModelApplication application = (ModelApplication) getApplication();
        application.getActivityManager().popActivity(this);
    }


    //视图相关
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }


}
