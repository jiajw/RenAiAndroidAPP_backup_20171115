package com.fragment.base;

/**
 * Created by sym on 17/4/14.
 */

public abstract class BasePresenter<T extends IView> {

    public T mView;

    public void attach(T mView) {
        this.mView = mView;
    }

    public void dettach() {
        mView = null;
    }
}
