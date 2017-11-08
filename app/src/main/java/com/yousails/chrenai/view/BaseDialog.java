package com.yousails.chrenai.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        super(context, R.style.LoadingDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setAttributes() {
        setAttributes(getWindow().getWindowManager().getDefaultDisplay()
                .getWidth(), Gravity.CENTER);
    }

    protected void setAttributes(int width, int gravity) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.gravity = gravity;
        params.alpha = 1.0f; // 设置本身透明度
        params.dimAmount = 0.5f; // 设置黑暗度
        window.setAttributes(params);
        this.setCanceledOnTouchOutside(false);
    }

}
