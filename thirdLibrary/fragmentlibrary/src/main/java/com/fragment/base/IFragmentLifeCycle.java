package com.fragment.base;

/**
 * Created by sym on 2/29/16.
 */
public interface IFragmentLifeCycle {

    void onCreated(LifeCyclePacket packet);

    /**
     * @return 是否允许点击返回键
     */
    boolean onBackPressed();

    void onCovered(LifeCyclePacket var1);

    void onUnveiled(LifeCyclePacket var1);
}
