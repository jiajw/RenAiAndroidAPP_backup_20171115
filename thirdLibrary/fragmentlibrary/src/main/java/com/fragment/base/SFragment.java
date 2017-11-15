package com.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import test.longfor.com.fragmentlibrary.R;

/**
 * Created by sym on 2/29/16.
 */
public abstract class SFragment extends Fragment implements
        IFragmentLifeCycle,
        IView {

    protected Bundle data;
    protected int code;
    protected FragmentHelper helper;
    protected View rootView;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreated(LifeCyclePacket packet) {
        data = packet.data;
        code = packet.code;
    }


    public FragmentHelper getHelper() {
        return helper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        helper = new FragmentHelper(this);
        super.onCreate(savedInstanceState);
    }

    public View getRootView() {
        if (null != rootView) {
            return rootView;
        }
        return null;
    }

    @Override
    public void onCovered(LifeCyclePacket var1) {
    }

    @Override
    public void onUnveiled(LifeCyclePacket var1) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(getLayoutId(), null);
        rootView = viewGroup;
        if (viewGroup.getLayoutParams() == null) {
            viewGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        View.OnTouchListener listener = getOwnTouchListener();
        if (listener == null) {
            listener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            };
        }
        viewGroup.setOnTouchListener(listener);
        return viewGroup;
    }

    @Override
    public SFragmentActivity getContext() {
        return (SFragmentActivity) getActivity();
    }

    public SFragment getFragment() {
        return (SFragment) getParentFragment();
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    /**
     * @return 返回该fragment所要展示的布局文件的id
     */
    abstract protected int getLayoutId();

    protected View.OnTouchListener getOwnTouchListener() {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
    }


    protected void showToast(int resId) {
        String string = getString(resId);
        showToast(string);
    }

    protected void showToast(String content) {
        Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 方便子类toast
     *
     * @param msg
     */
    public void showToast(Object msg) {
        if (msg == null)
            return;
        if (msg instanceof String) {
            Toast.makeText(getActivity().getApplicationContext(), String.valueOf(msg), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), (int) msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param packet
     */
    public void popTopChildFragment(LifeCyclePacket packet) {
        popTop(packet);
    }

    /**
     * @param packet
     */
    public void popTop(LifeCyclePacket packet) {
        int count = getChildFragmentManager().getBackStackEntryCount();
        if (count > 0)
            getChildFragmentManager().popBackStackImmediate();

        SFragment topFragment = getTopChildFragment();
        if (null == topFragment) {
            onStackEmpty(packet);
        } else {
            topFragment.onUnveiled(packet);
        }
    }

    public SFragment getTopChildFragment() {
        SFragment topFragment = null;
        int count = getChildFragmentManager().getBackStackEntryCount();
        Log.i(TAG, "现在回退栈中的Fragment的个数是： " + count);
        if (count > 0) {
            String name = getChildFragmentManager().getBackStackEntryAt(count - 1).getName();
            topFragment = (SFragment) getChildFragmentManager().findFragmentByTag(name);
        }

        return topFragment;
    }

    public void popToChildRoot() {
        while (getChildFragmentManager().getBackStackEntryCount() > 0) {
            popTopChildFragment(null);
        }
    }

    public SFragment presentChildFragmentToPushStack(Class<?> cls, Bundle data, Boolean animated, int code){
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

    public void onStackEmpty(LifeCyclePacket packet) {

    }

    public void onStackFilled(LifeCyclePacket packet) {

    }

    public SFragment addTo(LifeCyclePacket packet) {
        Class<?> cls = packet.cls;
        if (cls == null) {
            return null;
        } else {

            try {
                SFragment topFragment = getTopChildFragment();
                if (topFragment == null) {
                    onStackFilled(packet);
                    Log.i(TAG, "topFragment 是 null");
                } else {
                    topFragment.onCovered(packet);
                    Log.i(TAG, "topFragment 不是 null");
                }

                String tag = getChildFragmentTag(packet);
                FragmentManager fm = getChildFragmentManager();
                cls.newInstance();
                SFragment fragment = (SFragment) cls.newInstance();
                fragment.onCreated(packet);

                FragmentTransaction ft = fm.beginTransaction();
                if (packet.listener != null) {
                    packet.listener.prepare(fragment, ft);
                }

                ft.add(getChildFragmentContainerId(), fragment, tag);
                ft.addToBackStack(tag);
                ft.commitAllowingStateLoss();
                return fragment;

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected String getChildFragmentTag(LifeCyclePacket packet){
        StringBuilder sb = new StringBuilder(packet.cls.toString());
        return sb.toString();
    }

    public abstract int getChildFragmentContainerId();
}
