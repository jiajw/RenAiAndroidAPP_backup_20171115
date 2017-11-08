package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yousails.chrenai.R;

/**
 * Created by Administrator on 2017/8/10.
 */

public class DropDialog extends BaseDialog {

    private Button submitBtn;


    private View.OnClickListener l;
    public DropDialog(Context context) {
        super(context);
    }
    public DropDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drop_dialog);

        initView();
        setListener();
    }

    private void initView(){
        submitBtn=(Button)findViewById(R.id.tv_submit);
    }

    private void setListener(){
        submitBtn.setOnClickListener(l);
    }
}
