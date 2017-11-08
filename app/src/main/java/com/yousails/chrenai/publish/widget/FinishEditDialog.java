package com.yousails.chrenai.publish.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yousails.chrenai.R;

/**
 * Author:WangKunHui
 * Date: 2017/7/19 16:09
 * Desc:
 * E-mail:life_artist@163.com
 */
public class FinishEditDialog extends Dialog {

    private TextView content;

    private TextView navigateButton;

    private TextView positiveButton;

    public FinishEditDialog(Context context) {
        super(context, R.style.CustomDialog);

        this.setContentView(R.layout.widget_edit_dialog);
        initView();
    }

    public FinishEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initView() {
        content = (TextView) findViewById(R.id.tv_content);
        navigateButton = (TextView) findViewById(R.id.tv_navigate);
        positiveButton = (TextView) findViewById(R.id.tv_positive);

    }

    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    public void setOnNavigateListener(View.OnClickListener listener) {
        navigateButton.setOnClickListener(listener);
    }

}
