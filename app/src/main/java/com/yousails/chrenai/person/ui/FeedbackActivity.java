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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/10.
 */

public class FeedbackActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private EditText contentView;
    private TextView limitNum;


    @Override
    protected void setContentView() {
       setContentView(R.layout.activity_write);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("意见反馈");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        TextView submitView=(TextView)findViewById(R.id.txt_more);
        submitView.setText("提交");
        submitView.setTextColor(getResources().getColor(R.color.main_blue_color));

        contentView=(EditText)findViewById(R.id.et_textarea);

        limitNum=(TextView)findViewById(R.id.textarea_right);
        limitNum.setText("可输入200字");

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);
        contentView.addTextChangedListener(new MaxLengthWatcher(200, contentView,editTextListener));
    }

    TextChangeListener editTextListener=new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
            int length=editable.length();
            if(length>0){
                limitNum.setVisibility(View.GONE);
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
            boolean isLogin=AppPreference.getInstance(mContext).readLogin();
            if(isLogin){
                String content=contentView.getText().toString().trim();
                if (StringUtil.isEmpty(content)) {
                    CustomToast.createToast(mContext,"请填写反馈内容");
                }else{
                    sendFeedback( content);
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

    /**
     * 意见反馈
     */
    private void sendFeedback(String content){

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        String token=AppPreference.getInstance(mContext).readToken();
        Map<String, String> params = new HashMap<>();
        params.put("content", content);

        OkHttpUtils.post().url(ApiConstants.POST_FEEDBACK_API).addHeader("Authorization",token).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(1);
            }
        });

    }
}
