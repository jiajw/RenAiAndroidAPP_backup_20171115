package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class RegisterErrorDialog extends BaseDialog{
    private RelativeLayout closedLayout;
    //    private ImageView closedView;
    private  Button submitBtn;


    private View.OnClickListener l;
    public RegisterErrorDialog(Context context) {
        super(context);
    }
    public RegisterErrorDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_error_dialog);

        initView();
        setListener();
    }

    private void initView(){
        closedLayout=(RelativeLayout)findViewById(R.id.closed_layout);
//        closedView=(ImageView)findViewById(R.id.iv_closed);
        submitBtn=(Button)findViewById(R.id.tv_submit);
    }

    private void setListener(){
        submitBtn.setOnClickListener(l);
        closedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
