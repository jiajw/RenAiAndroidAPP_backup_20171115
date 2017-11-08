package com.yousails.chrenai.publish.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.common.ScreenUtil;

/**
 * Author:WangKunHui
 * Date: 2017/7/18 18:20
 * Desc:
 * E-mail:life_artist@163.com
 */
public class CustomToast {

    private Toast mToast;

    private CustomToast(Context context, CharSequence text, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_custom_toast, null);
        TextView textView = (TextView) v.findViewById(R.id.tv_content);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtil.getScreenWidth(context) - 250, 300);

        textView.setText(text);
        textView.setLayoutParams(params);

        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.CENTER, 0, -200);

        mToast.setView(v);
    }

    public static CustomToast makeText(Context context, CharSequence text, int duration) {
        return new CustomToast(context, text, duration);
    }

    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }
    }

    public void setGravity(int gravity) {
        if (mToast != null) {
            mToast.setGravity(gravity, 0, 0);
        }
    }

}
