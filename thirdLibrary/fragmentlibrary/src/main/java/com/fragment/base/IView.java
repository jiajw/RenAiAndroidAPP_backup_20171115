package com.fragment.base;

/**
 * Created by sym on 17/4/15.
 */

public interface IView {

    public abstract void dialogOn(String content);

    public abstract void dialogOn();

    public abstract void dialogOff();

    public abstract void showToast(Object content);

    public abstract void updateUI(Object data, Object... status);
}
