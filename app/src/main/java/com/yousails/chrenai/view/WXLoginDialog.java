package com.yousails.chrenai.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/8/16.
 */

public class WXLoginDialog extends ProgressDialog
{
    public WXLoginDialog(Context context) {
        super(context);
    }
    public WXLoginDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context)
    {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.login_dialog_layout);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

    }

//    public void setText(String str){
//        TextView contentView=(TextView)findViewById(R.id.loading_tview);
//        contentView.setText(str);
//    }


    @Override
    public void show()
    {
        super.show();
    }
}
