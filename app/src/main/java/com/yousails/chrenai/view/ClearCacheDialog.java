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

public class ClearCacheDialog extends BaseDialog {

    private ImageView closedView;
    private Button submitBtn;


    private View.OnClickListener l;
    public ClearCacheDialog(Context context) {
        super(context);
    }
    public ClearCacheDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.clear_cache_dialog);

        initView();
        setListener();
    }

    private void initView(){
        closedView=(ImageView)findViewById(R.id.iv_closed);
        submitBtn=(Button)findViewById(R.id.tv_submit);
    }

    private void setListener(){
        submitBtn.setOnClickListener(l);
        closedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
