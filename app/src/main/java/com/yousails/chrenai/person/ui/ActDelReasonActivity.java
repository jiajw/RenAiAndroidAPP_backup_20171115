package com.yousails.chrenai.person.ui;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.login.listener.MaxLengthWatcher;
import com.yousails.chrenai.login.listener.TextChangeListener;
import com.yousails.chrenai.person.ActDelEvent;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.RequestBody;

import static com.yousails.chrenai.home.ui.EnrollActivity.JSON;

/**
 * Created by Administrator on 2017/8/10.
 */

public class ActDelReasonActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private EditText contentView;

    private ActivitiesBean activity;

    @Override
    protected void setContentView() {
       setContentView(R.layout.activity_act_del_reason);
    }

    @Override
    protected void init() {
        activity = (ActivitiesBean) getIntent().getSerializableExtra("activity");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("活动删除原因");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        TextView submitView=(TextView)findViewById(R.id.txt_more);
        submitView.setText("完成");
        submitView.setTextColor(getResources().getColor(R.color.main_blue_color));

        contentView=(EditText)findViewById(R.id.et_textarea);


    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
            //先判断是否登录
            boolean isLogin=AppPreference.getInstance(mContext).readLogin();
            if(isLogin){
                String content=contentView.getText().toString().trim();
                if (StringUtil.isEmpty(content)) {
                    CustomToast.createToast(mContext,"请填写删除原因");
                }else{
                    deleteActivity(activity);
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
                      CustomToast.createToast(mContext,"发送失败!");
                        break;

                    case 1:
                        CustomToast.createToast(mContext,"感谢您的反馈");
                        finish();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {

    }

    private void deleteActivity(ActivitiesBean bean){
        String token = AppPreference.getInstance(mContext).readToken();
        Map<String, String> params = new HashMap<>();
        params.put("reason", contentView.getText().toString());
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(params));


        OkHttpUtils.delete().url(ApiConstants.BASE_ACTIVITY_API+bean.getId()).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                finish();
                EventBus.getDefault().post(new ActDelEvent());
            }

            @Override
            public void onResponse(String response, int id) {
                //if (StringUtil.isNotNull(response)) {
                    finish();
                    EventBus.getDefault().post(new ActDelEvent());
                //}
            }
        });
    }
}
