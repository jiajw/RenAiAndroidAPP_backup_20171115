package com.yousails.chrenai.framework.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.framework.util.CommonUtil;

/**
 * Fragment基类
 * 添加懒加载业务处理
 */
public abstract class TabBaseFragment extends Fragment implements View.OnClickListener {

    protected RelativeLayout titleBarRL;
    protected Activity mActivity;

    private View contentView;

    //UI 是否准备好，因为加载后需要更新Ui，如果UI还没有inflate就不需要加载数据
    protected boolean mIsPrepared;

    protected boolean mIsInited;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("==getUserVisibleHint===" + getUserVisibleHint());
        if (isVisibleToUser) {
            lazyLoad();
        }
    }

    private void lazyLoad() {
        if (getUserVisibleHint() && mIsPrepared) {
            loadData();
        }

    }

    /*lifecycle:01*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Fragment与Activity关联时调用，可以通过该方法获取Activity引用
        mActivity = (Activity) context;
    }

    /*lifecycle:02*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fragment被创建时被调用
        LogUtil.e("====onCreate===");

    }

    /*lifecycle:03*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //创建Fragment布局
        contentView = initView(inflater, container);
        mIsPrepared = true;
        LogUtil.e("====onCreateView===");
//        lazyLoad();
        return contentView;
    }

    /*lifecycle:04*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //当Activity完成onCreate()时调用
//        this.savedInstanceState = savedInstanceState;
        LogUtil.e("====onActivityCreated===");
        if (!mIsInited) {
            initData();
            mIsInited = true;
        }


    }

    /*lifecycle:05*/
    @Override
    public void onStart() {
        super.onStart();
        //当Fragment可见时调用
    }

    /*lifecycle:06*/
    @Override
    public void onResume() {
        super.onResume();
        //当Fragment可见且可交互时调用
    }

    /*lifecycle:07*/
    @Override
    public void onPause() {
        super.onPause();
        //当Fragment可见当不可交互时调用
    }

    /*lifecycle:08*/
    @Override
    public void onStop() {
        super.onStop();
        //当Fragment不可见时调用
    }

    /*lifecycle:09*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Fragment的UI从视图结构中移除时调用
        LogUtil.e("====onDestroyView===");

    }

    /*lifecycle:10*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁Fragment时调用
        LogUtil.e("====onDestroy===");
    }

    /*lifecycle:11*/
    @Override
    public void onDetach() {
        super.onDetach();
        //当Fragment 与Activity解除关联时调用
        LogUtil.e("====onDetach===");

    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initData();

    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFastDoubleClick()) {
            return;
        }
        processClick(v);
    }

    protected abstract void processClick(View v);
}