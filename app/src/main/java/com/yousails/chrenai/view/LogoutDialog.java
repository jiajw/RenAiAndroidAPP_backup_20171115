package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class LogoutDialog extends BaseDialog{
    private  Button quitBtn;
    private  Button cancelBtn;


    private View.OnClickListener l;
    public LogoutDialog(Context context) {
        super(context);
    }
    public LogoutDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.logout_dialog);

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
