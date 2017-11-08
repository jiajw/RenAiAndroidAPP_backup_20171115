package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class ValidateDialog extends BaseDialog{
    private EditText codeEditText;
    private ProgressBar progressBar;
    private ImageView codeImageView;
    private TextView submitView;
    private TextView cancleView;
    private Context context;
    private View view;

    private View.OnClickListener l;
    public ValidateDialog(Context context) {
        super(context);
    }
    public ValidateDialog(Context context,View.OnClickListener onClickListener) {
        super(context);
        this.context=context;
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.login_validate_dialog2, null);

        this.view=view;
//        setContentView(R.layout.login_validate_dialog);
        setContentView(view);
//        setAttributes(getWindow().getWindowManager().getDefaultDisplay()
//                .getWidth() * 3 / 4, Gravity.CENTER);
        initView();
        setListener();
    }

    private void initView(){
        progressBar = (ProgressBar)view.findViewById(R.id.pbar_loading);
        codeImageView=(ImageView)view.findViewById(R.id.iv_validate_code);
        codeEditText=(EditText)view.findViewById(R.id.et_validate_code);
        submitView=(TextView)view.findViewById(R.id.tv_submit);
        cancleView=(TextView)view.findViewById(R.id.tv_cancle);
    }

    private void setListener(){
        submitView.setOnClickListener(l);
        codeImageView.setOnClickListener(l);
        cancleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /*public void submit(View.OnClickListener l){
        submitView.setOnClickListener(l);
    }

    public void getValidateCode(View.OnClickListener l){
        codeImageView.setOnClickListener(l);
    }*/

    public String getCode(){
         return  codeEditText.getText().toString().trim();
    }

    public ImageView getImgeView(){
        return codeImageView;
//        return (ImageView)findViewById(R.id.iv_validate_code);
    }

    public EditText getCodeEditText(){
       return codeEditText;
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }

}
