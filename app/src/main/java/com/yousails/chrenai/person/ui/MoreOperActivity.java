package com.yousails.chrenai.person.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.person.bean.AuthorityBean;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.NoDoubleClickUtils;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.SignDialog;
import com.yousails.chrenai.zxing.camera.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 活动管理
 * Created by Administrator on 2017/8/23.
 */

public class MoreOperActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;

    private RelativeLayout scanLayout;
    private RelativeLayout registInforLayout;
    private RelativeLayout exceedRegistLayout;
    private RelativeLayout cancelRegistLayout;
    private RelativeLayout signInLayout;
    private RelativeLayout paymentLayout;
    private LinearLayout paymentDivider;

    private TextView registInforView;
    private TextView addRegistView;
    private TextView cancelRegistView;
    private TextView signInView;

    private SignDialog signDialog;
    private String cId;
    private String title;
    private boolean isChargeable;
    private String from="publish";

    private ActivitiesBean activity;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_more_operation);
    }

    @Override
    protected void init() {
        cId = getIntent().getStringExtra("cid");
        title = getIntent().getStringExtra("title");
        isChargeable = getIntent().getBooleanExtra("chargeable", false);
        from= getIntent().getStringExtra("from");
        activity = (ActivitiesBean)getIntent().getSerializableExtra("activity");
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("活动管理");

        scanLayout = (RelativeLayout) findViewById(R.id.scan_relayout);
        registInforLayout = (RelativeLayout) findViewById(R.id.regist_infor_relayout);
        exceedRegistLayout = (RelativeLayout) findViewById(R.id.exceed_regist_relayout);
        cancelRegistLayout = (RelativeLayout) findViewById(R.id.cancel_regist_relayout);
        signInLayout = (RelativeLayout) findViewById(R.id.sign_in_relayout);
        paymentLayout = (RelativeLayout) findViewById(R.id.payment_relayout);
        paymentDivider = (LinearLayout) findViewById(R.id.payment_divider);


        registInforView = (TextView) findViewById(R.id.regist_infor_tview);
        addRegistView = (TextView) findViewById(R.id.add_regist_tview);
        cancelRegistView = (TextView) findViewById(R.id.cancel_regist_tview);
        signInView = (TextView) findViewById(R.id.sign_in_tview);

        if (isChargeable) {
            paymentDivider.setVisibility(View.VISIBLE);
            paymentLayout.setVisibility(View.VISIBLE);
        }

        if("enjoy".equals(from)){

            scanLayout.setTag(0);
            registInforLayout.setTag(0);
            exceedRegistLayout.setTag(0);
            cancelRegistLayout.setTag(0);
            signInLayout.setTag(0);
            paymentLayout.setTag(0);


            if(activity!=null){
                List<AuthorityBean> authorityBeenList= activity.getUserPerms().getData();
                if(authorityBeenList!=null&&authorityBeenList.size()>0){
                    for(AuthorityBean authorityBean:authorityBeenList){
                        String displayName=authorityBean.getDisplay_name();

                        if("查看报名信息".equals(displayName)){
                            registInforLayout.setTag(1);
                        }else if("查看取消报名".equals(displayName)){
                            cancelRegistLayout.setTag(1);
                        }else if("超出报名审核".equals(displayName)){
                            exceedRegistLayout.setTag(1);
                        }else if("签到/工时审核".equals(displayName)){
                            signInLayout.setTag(1);
                            scanLayout.setTag(1);
                        }else if("缴费".equals(displayName)){
                            paymentLayout.setTag(1);
                        }
                    }
                }
            }
        }



    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        scanLayout.setOnClickListener(this);
        registInforLayout.setOnClickListener(this);
        exceedRegistLayout.setOnClickListener(this);
        cancelRegistLayout.setOnClickListener(this);
        signInLayout.setOnClickListener(this);
        paymentLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.scan_relayout:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if("publish".equals(from)){
                        if(activity.getSign_type().equals("manual")){
                            ToastUtils.showLong(MoreOperActivity.this,"请手工签到！");
                        }else{
                            cameraTask();
                        }
                    }else{
                        int tag=(int)scanLayout.getTag();
                        if(0==tag){
                            Toast.makeText(mContext,"您无权执行此操作",Toast.LENGTH_SHORT).show();
                        }else{
                            if(activity.getSign_type().equals("manual")){
                                ToastUtils.showLong(MoreOperActivity.this,"请手工签到！");
                            }else{
                                cameraTask();
                            }
                        }
                    }
                }

                break;
            case R.id.regist_infor_relayout:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if("publish".equals(from)){
                        startActivity(RegisterActivity.class);
                    }else{
                        int tag=(int)registInforLayout.getTag();
                        if(0==tag){
                            Toast.makeText(mContext,"您无权执行此操作",Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(RegisterActivity.class);
                        }
                    }
                }

                break;
            case R.id.exceed_regist_relayout:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if("publish".equals(from)){
                        startActivity(AddRegisterActivity.class);
                    }else{
                        int tag=(int)exceedRegistLayout.getTag();
                        if(0==tag){
                            Toast.makeText(mContext,"您无权执行此操作",Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(AddRegisterActivity.class);
                        }
                    }
                }

                break;
            case R.id.cancel_regist_relayout:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if("publish".equals(from)){
                        startActivity(CanceledRegistActivity.class);
                    }else{
                        int tag=(int)cancelRegistLayout.getTag();
                        if(0==tag){
                            Toast.makeText(mContext,"您无权执行此操作",Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(CanceledRegistActivity.class);
                        }
                    }
                }

                break;
            case R.id.sign_in_relayout:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if("publish".equals(from)){
                        startActivity(SignInActivity.class);
                    }else{
                        int tag=(int)signInLayout.getTag();
                        if(0==tag){
                            Toast.makeText(mContext,"您无权执行此操作",Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(SignInActivity.class);
                        }
                    }
                }

                break;
            case R.id.payment_relayout:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if("publish".equals(from)){
                        startActivity(PaymentManageActivity.class);
                    }else{
                        int tag=(int)paymentLayout.getTag();
                        if(0==tag){
                            Toast.makeText(mContext,"您无权执行此操作",Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(PaymentManageActivity.class);
                        }
                    }
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
                    case 1:
                        Bundle bundle = msg.getData();
                        int limit = bundle.getInt("limit");
                        int totalCount = bundle.getInt("totalCount");
                        int pendCount = bundle.getInt("pendCount");
                        int canceledCount = bundle.getInt("canceledCount");
                        int recordCount = bundle.getInt("recordCount");

                        if (limit == 0) {
                            registInforView.setText("报名信息(" + totalCount + "/不限)");
                        } else {
                            registInforView.setText("报名信息(" + totalCount + "/" + limit + ")");
                        }

                        addRegistView.setText("追加报名(" + pendCount + ")");
                        cancelRegistView.setText("取消报名(" + canceledCount + ")");
                        signInView.setText("签到/工时(" + recordCount + "/" + totalCount + ")");

                        break;
                }
            }
        };
    }

    @Override
    public void initData() {
        getActStatistic();
    }


    /**
     * 跳转页面
     *
     * @param cls
     */
    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        intent.putExtra("cid", cId);
        if(activity!=null){
            intent.putExtra("sign_type", activity.getSign_type());
        }else{
            intent.putExtra("sign_type","");
        }
        startActivity(intent);
    }


    /**
     * 活动数据统计
     */
    private void getActStatistic() {
        if (!NetUtil.detectAvailable(mContext)) {
            //    Toast.makeText(mContext,"请链接网络",Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(cId)) {
            return;
        }
        String url = ApiConstants.GET_COMMENTS_API + cId + "/stats";
        String token = AppPreference.getInstance(mContext).readToken();
        OkHttpUtils.get().url(url).addHeader("Authorization", token).addParams("id", cId).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int limit = jsonObject.optInt("limit");
                        int totalCount = jsonObject.optInt("application_total_count");
                        int pendCount = jsonObject.optInt("additional_pending_count");
                        int canceledCount = jsonObject.optInt("application_canceled_count");
                        int recordCount = jsonObject.optInt("record_count");

                        Bundle bundle = new Bundle();
                        bundle.putInt("limit", limit);
                        bundle.putInt("totalCount", totalCount);
                        bundle.putInt("pendCount", pendCount);
                        bundle.putInt("canceledCount", canceledCount);
                        bundle.putInt("recordCount", recordCount);

                        Message msg = new Message();
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private static final int RC_CAMERA_PERM = 124;

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            Intent capture = new Intent();
            capture.setClass(mContext, CaptureActivity.class);
            capture.putExtra("activity_id", cId);
            capture.putExtra("activity_name", title);
            startActivityForResult(capture, 9009);
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "扫码功能需要您的权限才能使用",
                    RC_CAMERA_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        //finish();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            //new AppSettingsDialog.Builder(this).build().show();
            cameraTask();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9009 && resultCode == RESULT_OK) {
            final String user_id = data.getStringExtra("user_id");
            final String activty_id = data.getStringExtra("activity_id");
            final String activity_name = data.getStringExtra("activity_name");
            getUser(user_id, activty_id, activity_name);
        }
    }
    private String signUserName="";
    private void getUser(final String userId, final String activityId, final String activityName) {
        Map<String, String> params = new HashMap<>();
        params.put("id", userId);

        OkHttpUtils.get().url(ApiConstants.GET_USER + userId).addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                Log.e("error", response);
                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        Toast.makeText(mContext, "用户信息不存在", Toast.LENGTH_LONG).show();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.e("response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.optString("name");
                    signUserName = name;
                    userSignStats(activityId,userId);
                    /*signDialog = new SignDialog(mContext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userSign(activityId, userId);
                            signDialog.dismiss();
                        }
                    }, "是否确认为 用户" + name + "\n" + activityName + " 签到？");
                    signDialog.show();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private String signMsg = "签到成功";
    private void userSignStats(final String activityId,final String userId){
        OkHttpUtils.get().url(ApiConstants.USER_SIGN+activityId+"/users/"+userId+"/records/stats").addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                Log.e("userSignStats",response.toString());
                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        String message = jsonObj.optString("message");
                        ToastUtils.showLong(MoreOperActivity.this,message);


                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.e("aaa",response);
                    JSONObject jsonObject = new JSONObject(response);
                    int total = jsonObject.optInt("total");
                    if(activity.getSign_type().equals("once")){
                        if(total==0){
                            signDialog = new SignDialog(mContext, new SignDialog.SignDialogListener() {
                                @Override
                                public void onConfirm() {
                                    userSign(activityId,userId);
                                    signDialog.dismiss();
                                }

                                @Override
                                public void onCancel() {
                                    signDialog.dismiss();
                                }
                            },"是否确认为 用户"+signUserName+"\n"+activity.getTitle()+" 签到？");
                            signDialog.show();
                            signMsg = "签到成功";
                        }else{
                            ToastUtils.showLong(MoreOperActivity.this,"用户已签到！");
                        }
                    }else if(activity.getSign_type().equals("twice")){
                        if(total%2==0){
                            signDialog = new SignDialog(mContext, new SignDialog.SignDialogListener() {
                                @Override
                                public void onConfirm() {
                                    userSign(activityId,userId);
                                    signDialog.dismiss();
                                }

                                @Override
                                public void onCancel() {
                                    signDialog.dismiss();
                                }
                            },"是否确认为 用户"+signUserName+"\n"+activity.getTitle()+" 签到？");
                            signDialog.show();
                            signMsg = "签到成功";
                        }else{
                            signDialog = new SignDialog(mContext, new SignDialog.SignDialogListener() {
                                @Override
                                public void onConfirm() {
                                    userSign(activityId,userId);
                                    signDialog.dismiss();
                                }

                                @Override
                                public void onCancel() {
                                    signDialog.dismiss();
                                }
                            },"是否确认为 用户"+signUserName+"\n"+activity.getTitle()+" 退出？");
                            signDialog.show();
                            signMsg = "退出成功";
                        }
                    }else{
                        ToastUtils.showLong(MoreOperActivity.this,"请手工签到！");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void userSign(String activityId, String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", activityId);
        params.put("user_id", userId);

        OkHttpUtils.post().url(ApiConstants.USER_SIGN + activityId + "/records").addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        String message = jsonObj.optString("message");
                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        //finish();

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.e("aaa", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.optString("message");
                    Toast.makeText(mContext, signMsg, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
