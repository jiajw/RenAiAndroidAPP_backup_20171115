package com.yousails.chrenai.framework.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Toast;

import com.yousails.chrenai.config.ModelApplication;


/**
 * User: jiajinwu
 * Date: 2017-11-01
 * Time: 10:03
 * 修改备注：
 * version:
 */


public class ToastUtil {


    private static Toast toast;


    public static void makeToast(Context context, String msg) {
        makeToast(msg, context);
    }

    public static void makeToast(String msg) {
        Context context = ModelApplication.getInstance().getApplicationContext();
        makeToast(msg, context);
    }

    public static void makeToast(Context context, int resId) {
        makeToast(context.getString(resId), context);
    }

    public static void makeToast(int resId) {
        Context context = ModelApplication.getInstance().getApplicationContext();
        makeToast(context.getString(resId), context);
    }


    private static void makeToast(String msg, Context context) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


    public static void makeTextTop(Context context, String msg) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, (int) (dm.heightPixels / 3.7));// 这样使Toast显示在屏幕中上位置
        } else {
            toast.setText(msg);
        }

        toast.show();
    }

    public static void makeTextCenter(Context context, String msg) {
        //DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);// 这样使Toast显示在屏幕中间位置
        } else {
            toast.setText(msg);
        }

        toast.show();
    }

    public static void makeTextCenter(Context context, int resId) {
        //DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (toast == null) {
            toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);// 这样使Toast显示在屏幕中间位置
        } else {
            toast.setText(context.getString(resId));
        }

        toast.show();
    }

    public static Toast showToast(Context context, String text, ToastUtil.Duration duration) {
        Toast toast = Toast.makeText(context, text, (duration == Duration.SHORT ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT));
        toast.setGravity(17, 0, 0);
        toast.show();
        return toast;
    }

    public static enum Duration {
        SHORT,
        LONG;

        private Duration() {
        }
    }

//    public static void showCustomerToast(Context context, String content ,int layoutId) {
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.toast_custome, null);
//        TextView tv = (TextView) view.findViewById(R.id.text);
//        tv.setText(content);
//
//        //防止多次点击按钮出现很多toast一直不消失
//        if (toast != null) {
//            toast.setView(view);
//        } else {
//            toast = new Toast(context);
//            toast.setView(view);
//            toast.setDuration(Toast.LENGTH_SHORT);
//        }
//        toast.show();
//    }


}
