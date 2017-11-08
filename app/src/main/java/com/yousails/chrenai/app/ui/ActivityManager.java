package com.yousails.chrenai.app.ui;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/6/16.
 */

public class ActivityManager {
    private static Stack<Activity> activityStack;
    private static ActivityManager instance;
    /**
     * TAG
     */
    protected final String TAG = "ActivityManager";

    private ActivityManager() {
    }

    public static ActivityManager getScreenManager() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public int getActivitySize() {
        if (activityStack != null && !activityStack.empty()) {
            return activityStack.size();
        }
        return 0;
    }

    /**
     * 退出栈顶Activity
     *
     * @param @param activity
     * @return void
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
//            Logger.i(TAG, "销毁Activity:%s", activity.getClass().getName());
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 获得当前栈顶Activity
     *
     * @param @return
     * @return Activity
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack != null && !activityStack.empty())
            activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 将当前Activity推入栈中
     *
     * @param @param activity
     * @return void
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
//        Logger.i(TAG, "添加Activity:%s", activity.getClass().getName());
    }

    /**
     * 退出栈中class上面的所有Activity。
     *
     * @param @param cls
     * @return void
     */
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    /**
     * 退出栈中除了包名外的所有Activity，
     *
     * @param @param packageName
     * @return void
     */
    public void popAllActivityExceptPackageName(String packageName) {
        Stack<Activity> save = new Stack<Activity>();
        for (Activity activity : activityStack) {
            if (activity != null) {
                String name = activity.getLocalClassName();
                Log.e("activity.name",name);
                if (!name.contains(packageName)) {
                    //popActivity(activity);
                    save.add(activity);
                    activity.finish();
                }
            }
        }
        activityStack.removeAll(save);
    }

    /**
     * 退出栈中所有Activity
     *
     * @param
     * @return void
     */
    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }
}
