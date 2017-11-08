package com.yousails.chrenai.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.yousails.chrenai.config.ModelApplication;


/**
 * Created by Administrator on 2017/6/22.
 */

public class UiUtil {
    /** 屏幕分辨率：屏幕高度 */
    public static int SCREEN_HEIGHT = 0;

    /** 屏幕分辨率：屏幕宽度 */
    public static int SCREEN_WIDTH = 0;

    /** 屏幕密度 */
    public static float DENSITY;
    /** 用" 点/英-per-inch"屏幕密度 */
    public static int DENSITY_DPI;

    public static String TAG = "UiUtil";

    public static void initUiParams(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;

        DENSITY = context.getResources().getDisplayMetrics().density;
        DENSITY_DPI = context.getResources().getDisplayMetrics().densityDpi;

        String uiInfo = new StringBuilder().append("[")
                .append("screen_width=" + SCREEN_WIDTH)
                .append(",screen_height=" + SCREEN_HEIGHT)
                .append(",density=" + DENSITY)
                .append(",density_dpi=" + DENSITY_DPI)
                .append("]").toString();
        Logger.d(TAG,uiInfo);

    }

    /**
     * 获取Activity 的根view,setContentView(View view)
     *
     * @param activity
     * @return
     */
    public static View getRootView(Activity activity) {
        return ((ViewGroup) activity.findViewById(android.R.id.content))
                .getChildAt(0);
    }

    /**
     * 扩大view的点击区域,每个parent只能委派(Delegate)一个child,如果委派多个最后一个child起作用
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public static void expandViewTouchDelegate(final View view, final int top,
                                               final int bottom, final int left, final int right) {
        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);
                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;
                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);
                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



    public static Context getContext() {
        return ModelApplication.mContext;
    }

    /** 获取资源 */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /** 获取文字 */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /** 获取dimen */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /** 获取drawable */
    public static Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }

    /** 获取颜色 */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /** 获取颜色选择器 */
    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

    public static boolean isMainThread() {
        Looper looper = Looper.myLooper();
        return looper != null && looper == Looper.getMainLooper();
    }
}
