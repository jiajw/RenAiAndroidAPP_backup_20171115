package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/8/29.
 */

public class AttenStatisActivity extends BaseActivity{
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;

    private RelativeLayout title_statistics_layout;
    private TextView totalCountView ;//名额
    private TextView regCountView ;//报名
    private TextView reachedCountView;//已到
    private TextView unreachedCountView;//未到
    private RelativeLayout paymentLyout;//缴费情况
    private RelativeLayout entrustedLayout;//委托管理
    private RelativeLayout feedbackLayout;//收到反馈

    private String aId;
    private String sign_type;

    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_attendance_statistics);
    }

    @Override
    protected void init() {

        aId=getIntent().getStringExtra("id");
        sign_type=getIntent().getStringExtra("sign_type");
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("活动统计");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.GONE);

        title_statistics_layout=(RelativeLayout)findViewById(R.id.title_statistics_layout);
        totalCountView = (TextView) findViewById(R.id.total_count_tview);//名额
        regCountView = (TextView) findViewById(R.id.register_count_tview);//报名
        reachedCountView =(TextView) findViewById(R.id.reached_count_tview);//已到
        unreachedCountView =(TextView) findViewById(R.id.unreached_count_tview);//未到

        paymentLyout= (RelativeLayout) findViewById(R.id.payment_layout);//缴费情况
        entrustedLayout= (RelativeLayout) findViewById(R.id.entrusted_layout);//委托管理
        feedbackLayout= (RelativeLayout) findViewById(R.id.feedback_layout);//收到反馈


    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        paymentLyout.setOnClickListener(this);
        entrustedLayout.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        title_statistics_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.payment_layout:
                Intent intent=new Intent(mContext,PaymentManageActivity.class);
                intent.putExtra("cid",aId);
                startActivity(intent);
                break;
            case R.id.title_statistics_layout:
                Intent signIntent=new Intent(mContext,SignInActivity.class);
                signIntent.putExtra("cid",aId);
                if(StringUtil.isNotNull(sign_type)){
                    signIntent.putExtra("sign_type", sign_type);
                }else{
                    signIntent.putExtra("sign_type", "");
                }
                startActivity(signIntent);
                break;
            case R.id.entrusted_layout:
                break;
            case R.id.feedback_layout:
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

                        break;
                    case 1:
                        Bundle bundle=msg.getData();
                        String totalCount=bundle.getString("totalCount");
                        String regCount=bundle.getString("regCount");
                        String reachedCount= bundle.getString("reachedCount");
                        String unreachedCount=bundle.getString("unreachedCount");

                        totalCountView.setText("名额:"+totalCount);
                        regCountView.setText(regCount);
                        reachedCountView.setText(reachedCount);
                        unreachedCountView .setText(unreachedCount);
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {
        getRecords();
    }


    /**
     * 活动签到数据统计
     */
    private void getRecords(){
       if(StringUtil.isEmpty(aId)){
           return;
       }
        String token= AppPreference.getInstance(mContext).readToken();
        String url= ApiConstants.GET_COMMENTS_API+aId+"/records/stats";

        OkHttpUtils.get().url(url).addHeader("Authorization",token).addParams("id",aId).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.print(response);
                if(StringUtil.isNotNull(response)){
                    try{
                        JSONObject jsonObject=new JSONObject(response);
                        String totalCount=jsonObject.optString("limit");
                        String regCount=jsonObject.optString("application_total_count");
                        String reachedCount=jsonObject.optString("reached_count");
                        String unreachedCount=jsonObject.optString("unreached_count");

                        Bundle bundle=new Bundle();
                        bundle.putString("totalCount",totalCount);
                        bundle.putString("regCount",regCount);
                        bundle.putString("reachedCount",reachedCount);
                        bundle.putString("unreachedCount",unreachedCount);
                        Message message=new Message();
                        message.what=1;
                        message.setData(bundle);
                        mHandler.sendMessage(message);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });
    }


}
