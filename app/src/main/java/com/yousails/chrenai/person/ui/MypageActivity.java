package com.yousails.chrenai.person.ui;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;

/**
 * Created by Administrator on 2017/8/4.
 */

public class MypageActivity extends BaseActivity{
    private LinearLayout settingLayout;
    private RelativeLayout chatLayout;



    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_mypage);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void findViews() {

        settingLayout=(LinearLayout)findViewById(R.id.setting_layout);
        chatLayout=(RelativeLayout)findViewById(R.id.chat_layout);


    }

    @Override
    protected void setListeners() {
        settingLayout.setOnClickListener(this);
        chatLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.setting_layout:
                finish();
                break;
            case R.id.chat_layout:
                break;
        }
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }
}
