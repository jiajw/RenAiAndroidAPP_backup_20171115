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

public class SignDialog extends BaseDialog implements View.OnClickListener{
    private  Button btnConfirm;
    private Button btnCancel;
    private TextView dialogTxt;


    private SignDialogListener listener;
    private String dialogString;
    public SignDialog(Context context) {
        super(context);
    }
    public SignDialog(Context context, SignDialogListener onClickListener,String dialogString) {
        super(context);
        this.dialogString = dialogString;
        this.listener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_sign);

        initView();
        setText();
    }

    private void initView(){
        btnConfirm=(Button)findViewById(R.id.btn_confirm);
        btnCancel=(Button)findViewById(R.id.btn_cancel);
        dialogTxt = (TextView)findViewById(R.id.dialog_txt);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
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

    private void setListener(SignDialogListener listener){
        this.listener = listener;
    }
    private void setText(){
        dialogTxt.setText(dialogString);
    }

    public interface SignDialogListener{
        void onConfirm();
        void onCancel();
    }

}
