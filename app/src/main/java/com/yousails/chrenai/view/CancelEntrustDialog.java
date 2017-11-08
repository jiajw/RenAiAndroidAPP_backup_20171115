package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/6/19.
 */

public class CancelEntrustDialog extends BaseDialog{
    private  Button quitBtn;
    private  Button cancelBtn;


    private View.OnClickListener l;
    public CancelEntrustDialog(Context context) {
        super(context);
    }
    public CancelEntrustDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cancel_entrust_dialog);

        initView();
        setListener();
    }

    private void initView(){
        quitBtn=(Button)findViewById(R.id.quit_btn);
        cancelBtn=(Button)findViewById(R.id.cancel_btn);

    }

    private void setListener(){
        cancelBtn.setOnClickListener(l);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
