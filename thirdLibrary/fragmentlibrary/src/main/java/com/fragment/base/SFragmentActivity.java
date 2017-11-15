package com.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import test.longfor.com.fragmentlibrary.R;

/**
 * Created by sym on 2/29/16.
 */
public abstract class SFragmentActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();

    abstract public int getFragmentContainerId();

    abstract public Context getApplicationContext();

    public void onStackEmpty(LifeCyclePacket packet) {

    }

    public void onStackFilled(LifeCyclePacket packet) {

    }

    /**
     * 在指定view处显示键盘
     *
     * @param view
     */
    public void showKeyboardAtView(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboardForCurrentFous() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public SFragment pushFragmentToPushStack(Class<?> cls, Bundle data, Boolean animated, int code) {
        LifeCyclePacket packet = new LifeCyclePacket();
        packet.cls = cls;
        packet.data = data;
        packet.animated = animated.booleanValue();
        packet.code = code;
        if (animated.booleanValue()) {
            packet.listener = new LifeCyclePacket.PacketPreparer() {
                @Override
                public void prepare(SFragment fragment, FragmentTransaction ft) {
                    //添加推入动画
                    ft.setCustomAnimations(
                            R.anim.pc_fragment_silde_left_in,
                            R.anim.pc_fragment_silde_left_out,
                            R.anim.pc_fragment_silde_left_in,
                            R.anim.pc_fragment_silde_left_out);
                }
            };
        }
        return this.addTo(packet);
    }

    public SFragment presentFragmentToPushStack(Class<?> cls, Bundle data, Boolean animated, int code) {
        LifeCyclePacket packet = new LifeCyclePacket();
        packet.cls = cls;
        packet.data = data;
        packet.animated = animated.booleanValue();
        packet.code = code;
        if (animated.booleanValue()) {
            packet.listener = new LifeCyclePacket.PacketPreparer() {
                @Override
                public void prepare(SFragment fragment, FragmentTransaction ft) {
                    //添加推入动画
                    ft.setCustomAnimations(
                            R.anim.pc_fragment_silde_up_in,
                            R.anim.pc_fragment_silde_down_out,
                            R.anim.pc_fragment_silde_up_in,
                            R.anim.pc_fragment_silde_down_out);
                }
            };
        }
        return this.addTo(packet);
    }


    public SFragment addTo(LifeCyclePacket packet) {
        Class<?> cls = packet.cls;
        if (cls == null) {
            return null;
        } else {

            try {
                SFragment topFragment = getTopFragment();
                if (topFragment == null) {
                    onStackFilled(packet);
                    Log.i(TAG, "topFragment 是 null");
                } else {
                    topFragment.onCovered(packet);
                    Log.i(TAG, "topFragment 不是 null");
                }

                String tag = getFragmentTag(packet);
                FragmentManager fm = getSupportFragmentManager();
                cls.newInstance();
                SFragment fragment = (SFragment) cls.newInstance();
                fragment.onCreated(packet);

                FragmentTransaction ft = fm.beginTransaction();
                if (packet.listener != null) {
                    packet.listener.prepare(fragment, ft);
                }

                ft.add(getFragmentContainerId(), fragment, tag);
                ft.addToBackStack(tag);
                ft.commitAllowingStateLoss();
                return fragment;

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getFragmentTag(LifeCyclePacket packet) {
        StringBuilder sb = new StringBuilder(packet.cls.toString());
        return sb.toString();
    }

    public SFragment getTopFragment() {
        SFragment topFragment = null;
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.i(TAG, "现在回退栈中的Fragment的个数是： " + count);
        if (count > 0) {
            String name = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
            topFragment = (SFragment) getSupportFragmentManager().findFragmentByTag(name);
        }

        return topFragment;
    }

    public void popTopFragment(LifeCyclePacket packet) {
        popTop(packet);
    }

    /**
     * @param packet
     */
    public void popTop(LifeCyclePacket packet) {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0)
            getSupportFragmentManager().popBackStackImmediate();

        SFragment topFragment = getTopFragment();
        if (null == topFragment) {
            onStackEmpty(packet);
        } else {
            topFragment.onUnveiled(packet);
        }
    }

    public void popToRoot() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            popTopFragment(null);
        }
    }

    /**
     * 显示toast
     *
     * @param resId
     * @return
     */
    public Toast showToast(int resId) {
        Toast toast = Toast.makeText(this, getString(resId), Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }

    public Toast showToast(String word) {
        Toast toast = Toast.makeText(getApplicationContext(), word, Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }

    @Override
    public void onBackPressed() {

        boolean close = true;

        SFragment topFragment = getTopFragment();
        if (null != topFragment) {

            close = topFragment.onBackPressed();
        }

        if (close) {
            super.onBackPressed();
            topFragment = getTopFragment();

            if (null == topFragment) {
                onStackEmpty(null);
            } else {
                topFragment.onUnveiled(null);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        //参考文章 ：http://my.oschina.net/u/1011854/blog/469138
    }
}
