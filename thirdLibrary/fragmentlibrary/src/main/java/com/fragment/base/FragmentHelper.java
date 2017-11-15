package com.fragment.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


/**
 * Created by sym on 2/29/16.
 */
public class FragmentHelper {
    private SFragment holderFragment;

    public FragmentHelper(SFragment fragment) {
        holderFragment = fragment;
    }

    /**
     * 显示键盘
     */
    public void showKeyboardAtView(View view) {
        SFragmentActivity context = holderFragment.getContext();
        if (null != context) {
            context.showKeyboardAtView(view);
        }
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboardForCurrentFocus() {
        SFragmentActivity context = holderFragment.getContext();
        if (null != context) {
            context.hideKeyboardForCurrentFous();
        }
    }

    /**
     * 返回上一个页面需要携带参数
     *
     * @param packet
     */
    public void popTopFragment(LifeCyclePacket packet) {
        SFragmentActivity context = holderFragment.getContext();
        if (null != context) {
            context.popTopFragment(packet);
        }
    }

    /**
     * 返回上一个fragment不需要携带参数
     */
    public void popTopFragment() {
        SFragmentActivity context = holderFragment.getContext();
        if (null != context) {
            context.popTopFragment(null);
        }
    }

    public void popTopChildFragment() {

        SFragment parentFragment = holderFragment.getFragment();
        if (null != parentFragment) {
            parentFragment.popTopChildFragment(null);
        }
    }

    public void popToRoot() {
        SFragmentActivity context = this.holderFragment.getContext();
        if (null != context) {
            context.popToRoot();
        }
    }

    /**
     * 给fragment添加了推入动画 从左到右
     */
    public SFragment pushFragmentToPushStack(Class<?> cls, Bundle data, boolean animated, int code) {
        SFragmentActivity context = this.holderFragment.getContext();
        if (null != context) {
            return context.pushFragmentToPushStack(cls, data, animated, code);
        }
        return null;
    }

    /**
     * 添加推入动画 从下到上
     *
     * @param cls
     * @param data
     * @param animated
     * @param code
     * @return
     */
    public SFragment presentFragmentToPushStack(Class<?> cls, Bundle data, boolean animated, int code) {
        SFragmentActivity context = this.holderFragment.getContext();
        if (null != context) {
            return context.presentFragmentToPushStack(cls, data, animated, code);
        }
        return null;
    }

    /**
     * 添加推入动画 从下到上
     *
     * @param cls
     * @param data
     * @param animated
     * @param code
     * @return
     */
    public SFragment presentChildFragmentToPushStack(Class<?> cls, Bundle data, boolean animated, int code) {
        SFragment context = this.holderFragment.getFragment();
        if (null != context) {
            return context.presentChildFragmentToPushStack(cls, data, animated, code);
        }
        return null;
    }


    public SFragment presentFragmentToPushStack(Class<?> cls, Bundle data, boolean animated) {
        return presentFragmentToPushStack(cls, data, animated, -1);
    }

    public SFragment presentChildFragmentToPushStack(Class<?> cls, Bundle data, boolean animated) {
        return presentChildFragmentToPushStack(cls, data, animated, -1);
    }

    /**
     * 显示Toast
     */
    public Toast showToast(String word) {
        if (holderFragment.getContext() != null) {
            return holderFragment.getContext().showToast(word);
        }
        return null;
    }

    /**
     * 显示Toast
     */
    public Toast showToast(int wordId) {
        if (holderFragment.getContext() != null) {
            return holderFragment.getContext().showToast(wordId);
        }
        return null;
    }
}
