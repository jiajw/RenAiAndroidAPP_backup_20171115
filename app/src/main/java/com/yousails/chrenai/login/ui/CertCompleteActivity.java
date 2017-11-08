package com.yousails.chrenai.login.ui;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.config.ModelApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/8/8.
 */

public class CertCompleteActivity extends BaseActivity{
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private Button backHomeBtn;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_certific_complete);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        backLayout.setVisibility(View.GONE);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("认证完成");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.GONE);

        backHomeBtn=(Button)findViewById(R.id.exit_btn_submit);

    }

    @Override
    protected void setListeners() {
        backHomeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exit_btn_submit:

                ModelApplication application = (ModelApplication) getApplication();
                application.getActivityManager().popAllActivity();

                Intent intent=new Intent(mContext,MainActivity.class);
                startActivity(intent);
                finish();

                //关掉实名认证页面
//                EventBus.getDefault().post("finish");
                break;
        }
    }

    @Override
    protected void handleMessage() {

    }


    @Override
    public void initData() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}
