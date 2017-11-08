package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class AllowEnrollDialog extends BaseDialog{
    private  RelativeLayout closedLayout;
    private  Button quitBtn;
    private  Button cancelBtn;

    private View.OnClickListener l;
    public AllowEnrollDialog(Context context) {
        super(context);
    }
    public AllowEnrollDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.allow_enroll_dialog);

        initView();
        setListener();
    }

    private void initView(){
        quitBtn=(Button)findViewById(R.id.quit_btn);
        cancelBtn=(Button)findViewById(R.id.cancel_btn);
        closedLayout=(RelativeLayout)findViewById(R.id.closed_layout);

    }

    private void setListener(){
        quitBtn.setOnClickListener(l);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        closedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
