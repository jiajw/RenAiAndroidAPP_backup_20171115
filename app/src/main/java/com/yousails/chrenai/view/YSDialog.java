package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class YSDialog extends BaseDialog implements View.OnClickListener{
    private TextView title;
    private  Button btnConfirm;
    private  Button btnCancel;
    private YsDialogListener listener;

    private String titleString;
    private String confirmString;
    private String cancelString;

    public YSDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_ys);

        initView();
    }

    private void initView(){
        title=(TextView)findViewById(R.id.tv_title);
        btnConfirm=(Button)findViewById(R.id.btn_confirm);
        btnCancel=(Button)findViewById(R.id.btn_cancel);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    public void setTitle(String titleString){
        title.setText(titleString);
    }
    public void setConfirm(String confirmString){
        btnConfirm.setText(confirmString);
    }
    public void setCancel(String cancelString){
        btnCancel.setText(cancelString);
    }
    public void setOnYsDialogListenter(YsDialogListener listenter){
        this.listener = listenter;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                listener.onConfirm();
                break;
            case R.id.btn_cancel:
                listener.onCancel();
                break;
        }
    }

    public interface YsDialogListener{
        void onConfirm();
        void onCancel();
    }
}
