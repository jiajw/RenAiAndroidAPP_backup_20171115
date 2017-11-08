package com.yousails.chrenai.utils;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/20.
 */

public class CustomToast extends Toast {
    public CustomToast(Context context) {
        super(context);
    }

    /**
     * 可以随意定义样式
     * @param context
     * @param layout
     * @param resid
     * @param s
     */
    public static void createToast(Context context, int layout, int resid,
                                   CharSequence s) {
        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(layout, null);
        TextView tv = (TextView) view.findViewById(resid);
        tv.setText(s);
        createMiddleToast(context, view).show();
    }

    private static CustomToast createMiddleToast(Context context,View view){
        CustomToast toast = createToast(context);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    private static CustomToast createToast(Context context){
        CustomToast toast = new CustomToast(context);
        return toast;
    }

    private static View creatView(Context context,CharSequence s) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout view = new LinearLayout(context);
        View inflate = View.inflate(context, R.layout.toast_layout, null);
        TextView tView = (TextView) inflate.findViewById(R.id.text0);
        tView.setText(s);
        view.addView(inflate);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setPadding(20, 20, 20, 20);
        view.setGravity(Gravity.CENTER);// 内容居中
        view.setLayoutParams(params);
        return view;
    }

    /**
     * 自定义toast
     * @param context
     * @param s
     * @param s
     */
    public static void createToast(Context context,CharSequence s) {
        View view = creatView(context,s);
        createMiddleToast(context, view).show();
    }


    private static boolean isMainThread() {
        Looper looper = Looper.myLooper();
        return looper != null && looper == Looper.getMainLooper();
    }

    public static void show(Context context, int resid, int type) {
        if (isMainThread()) {
            Toast.makeText(context, resid, type).show();
        }
    }

    public static void show(Context context, int resid) {
        show(context, resid, Toast.LENGTH_SHORT);
    }

}
