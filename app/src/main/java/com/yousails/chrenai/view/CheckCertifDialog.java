package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.utils.StringUtil;

/**
 * Created by Administrator on 2017/6/19.
 */

public class CheckCertifDialog extends BaseDialog{
    private RelativeLayout closedLayout;
//    private ImageView closedView;
    private TextView contentView;
    private  Button submitBtn;
    private String from;


    private View.OnClickListener l;
    public CheckCertifDialog(Context context) {
        super(context);
    }
    public CheckCertifDialog(Context context,String from, View.OnClickListener onClickListener) {
        super(context);
        this.from=from;
        this.l=onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.check_certif_dialog);

        initView();
        setListener();
    }

    private void initView(){
        closedLayout=(RelativeLayout)findViewById(R.id.closed_layout);
//        closedView=(ImageView)findViewById(R.id.iv_closed);
        contentView=(TextView)findViewById(R.id.content_tview);
        submitBtn=(Button)findViewById(R.id.tv_submit);

        if(StringUtil.isNotNull(from)&&"detail".equals(from)){
            contentView.setText("抱歉需要实名认证才能发布讨论");
        }

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


    /**
     * 设置中间提示文字内容
     */
    public void setContent(String content){
        contentView.setText(content);
    }
}
