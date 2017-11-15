package com.fragment.base;


import test.longfor.com.fragmentlibrary.R;

/**
 * Created by sym on 17/5/28.
 */

public abstract class BaseFragment extends SFragment {


    @Override
    public void dialogOn(String content) {

    }

    @Override
    public void dialogOn() {

    }

    @Override
    public void dialogOff() {

    }

    @Override
    public void updateUI(Object data, Object... status) {

    }

    public void refresh(String url){

    }

    @Override
    protected abstract int getLayoutId();

    @Override
    public int getChildFragmentContainerId() {
        return R.id.childContainer;
    }
}
