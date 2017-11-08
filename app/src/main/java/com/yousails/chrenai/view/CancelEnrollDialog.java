package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class CancelEnrollDialog extends BaseDialog{
    private  Button quitBtn;
    private  Button cancelBtn;


    private View.OnClickListener l;
    public CancelEnrollDialog(Context context) {
        super(context);
    }
    public CancelEnrollDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cancel_enroll_dialog);

        initView();
        setListener();
    }

    private void initView(){
        quitBtn=(Button)findViewById(R.id.quit_btn);
        cancelBtn=(Button)findViewById(R.id.cancel_btn);

    }

    private void setListener(){
        quitBtn.setOnClickListener(l);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
