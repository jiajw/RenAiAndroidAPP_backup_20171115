package com.yousails.chrenai.publish.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.common.ToastUtils;

/**
 * Author:WangKunHui
 * Date: 2017/7/19 16:09
 * Desc:
 * E-mail:life_artist@163.com
 */
public class SimpleHelpDialog extends Dialog {

    private ImageView closeButton;


    public SimpleHelpDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    public SimpleHelpDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.widget_help_dialog);
        initView();
    }

    private void initView() {

        closeButton = (ImageView) findViewById(R.id.iv_close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
