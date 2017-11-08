package com.yousails.chrenai.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * Created by Administrator on 2017/6/20.
 */

public class SexSelectedActivity extends BaseActivity{
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private TextView submitView;

    private ImageView maleView, femaleView;
    private RelativeLayout maleLayout, femaleLayout;
    private String from;
    private String sex;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_sex);
    }

    @Override
    protected void init() {
        from = getIntent().getStringExtra("from");
        sex = getIntent().getStringExtra("sex");
    }

    @Override
    protected void findViews() {

        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(R.string.sex_text);

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        submitView = (TextView) findViewById(R.id.txt_more);

        maleLayout = (RelativeLayout) findViewById(R.id.male_layout);
        maleView = (ImageView) findViewById(R.id.male_imageview);

        femaleLayout = (RelativeLayout) findViewById(R.id.female_layout);
        femaleView = (ImageView) findViewById(R.id.female_imageview);

        initView();
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        maleLayout.setOnClickListener(this);
        femaleLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);
    }

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
//                        Bundle bundle= msg.getData();
//                        String message=bundle.getString("message");

                        CustomToast.createToast(mContext,"更改性别失败");
                        break;

                    case 1:
                        CustomToast.createToast(mContext,"更改性别成功");

                        if ("男".equals(sex)) {
                            AppPreference.getInstance(mContext).writeGender("male");
                        } else {
                            AppPreference.getInstance(mContext).writeGender("female");
                        }

                        Intent intent = new Intent();
                        intent.putExtra("sex", sex);
                        setResult(2, intent);
                        finish();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                setResult();
                break;
            case R.id.male_layout:
//                if (maleView.getVisibility() == View.VISIBLE) {
//                    maleView.setVisibility(View.GONE);
//                    sex = "";
//                } else {
                    maleView.setVisibility(View.VISIBLE);
                    femaleView.setVisibility(View.GONE);
                    sex = "男";
//                }
                break;
            case R.id.female_layout:
//                if (femaleView.getVisibility() == View.VISIBLE) {
//                    femaleView.setVisibility(View.GONE);
//                    sex = "";
//                } else {
                    femaleView.setVisibility(View.VISIBLE);
                    maleView.setVisibility(View.GONE);
                    sex = "女";
//                }
                break;
            case R.id.btn_more:
                if (StringUtil.isEmpty(sex)) {
                    CustomToast.createToast(mContext, "请选择性别");
                } else {
                    if ("男".equals(sex)) {
                        updateSex("male");
                    } else {
                        updateSex("female");
                    }
                }
                break;
        }
    }

    private void initView() {

        if (StringUtil.isNotNull(sex)) {
            if ("男".equals(sex)) {
                maleView.setVisibility(View.VISIBLE);
            } else {
                femaleView.setVisibility(View.VISIBLE);
            }
        }

        if ("register".equals(from)) {
            sharedLayout.setVisibility(View.INVISIBLE);
        } else {
            submitView.setText("提交");
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            setResult();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setResult() {
        if ("register".equals(from)) {
            Intent intent = new Intent();
            intent.putExtra("sex", sex);
            setResult(1, intent);
        }
        finish();
    }


    /**
     * 更改性别
     */
    private void updateSex(String sex) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        String token = AppPreference.getInstance(mContext).readToken();

        RequestBody requestBody = new FormBody.Builder().add("sex", sex).build();
        OkHttpUtils.patch().url(ApiConstants.UPDATE_USERINFOR_API).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.optString("message");

                        Bundle bundle = new Bundle();
                        bundle.putString("message", message);

                        Message msg = new Message();
                        msg.what = 0;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);

                    } catch (Exception ee) {
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
}
