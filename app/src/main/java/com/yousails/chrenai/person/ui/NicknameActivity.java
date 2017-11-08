package com.yousails.chrenai.person.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.login.listener.MaxLengthWatcher;
import com.yousails.chrenai.login.listener.TextChangeListener;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/11.
 */

public class NicknameActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private EditText contentView;
    private TextView limitNum;
    private TextView tipsView;
    private String content;
    private String nickName;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_nickname);
    }

    @Override
    protected void init() {
        // 注册对象
        EventBus.getDefault().register(this);
        nickName=getIntent().getStringExtra("name");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("昵称");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        TextView submitView=(TextView)findViewById(R.id.txt_more);
        submitView.setText("提交");
        submitView.setTextColor(getResources().getColor(R.color.main_blue_color));

        contentView=(EditText)findViewById(R.id.et_textarea);
        tipsView=(TextView)findViewById(R.id.tv_error_tips);

        if(StringUtil.isNotNull(nickName)){
            contentView.setText(nickName);
            contentView.setSelection(nickName.length());
        }

        limitNum=(TextView)findViewById(R.id.textarea_right);
        limitNum.setText("可输入20字");

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);
        contentView.addTextChangedListener(new MaxLengthWatcher(20, contentView,editTextListener));
    }

    TextChangeListener editTextListener=new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
            int length=editable.length();
            if(length>0){
                limitNum.setVisibility(View.GONE);
                tipsView.setText("");
            }else{
                limitNum.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
                //先判断是否登录
                boolean isLogin= AppPreference.getInstance(mContext).readLogin();
                if(isLogin){
                    content=contentView.getText().toString().trim();
                    if (StringUtil.isEmpty(content)) {
                        CustomToast.createToast(mContext,"请填写昵称");
                    }else{
                        updateNickName(content);
                    }
                }else{
                    CustomToast.createToast(mContext,"请先登录");
                }
                break;
        }
    }

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Bundle bundle= msg.getData();
                        String message=bundle.getString("message");
                        tipsView.setText(message);
//                        CustomToast.createToast(mContext,"修改失败");
                        break;

                    case 1:
                        CustomToast.createToast(mContext,"更改昵称成功");
                        AppPreference.getInstance(mContext).writeUserName(content);
                        //更新昵称
                        EventBus.getDefault().post("updatename");
                        finish();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {

    }


    /**
     * 更改昵称
     */
    private void updateNickName(String name) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        String token=AppPreference.getInstance(mContext).readToken();

        RequestBody requestBody = new FormBody.Builder().add("name", name).build();
        OkHttpUtils.patch().url(ApiConstants.UPDATE_USERINFOR_API).addHeader("Authorization",token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if(StringUtil.isNotNull(response)&&response.contains("message")){
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String message=jsonObject.optString("message");

                        Bundle bundle=new Bundle();
                        bundle.putString("message",message);

                        Message msg=new Message();
                        msg.what=0;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);

                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                }


//                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(1);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateNickname(String message){

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销
        EventBus.getDefault().unregister(this);
    }
}
