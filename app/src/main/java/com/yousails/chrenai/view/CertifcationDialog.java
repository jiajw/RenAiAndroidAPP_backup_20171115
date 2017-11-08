package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.utils.StringUtil;

/**
 * Created by Administrator on 2017/6/19.
 */

public class CertifcationDialog extends BaseDialog{
    private RelativeLayout closedLayout;
//    private ImageView closedView;
    private TextView contentView;
    private  Button submitBtn;
    private String type;


    private View.OnClickListener l;
    public CertifcationDialog(Context context) {
        super(context);
    }
    public CertifcationDialog(Context context,String type,View.OnClickListener onClickListener) {
        super(context);
        this.type=type;
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
        closedLayout.setVisibility(View.GONE);
//        closedView=(ImageView)findViewById(R.id.iv_closed);
        RelativeLayout headLayout=(RelativeLayout)findViewById(R.id.head_layout);
        headLayout.setVisibility(View.GONE);
        contentView=(TextView)findViewById(R.id.content_tview);
        submitBtn=(Button)findViewById(R.id.tv_submit);

        if(StringUtil.isNotNull(type)&&"person".equals(type)){
            contentView.setText("还未实名认证，快去实名认证开通足迹，获得工时记录功能。");
        }else{
            contentView.setText(type+"还未实名认证");
        }

        submitBtn.setText("确认");
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
