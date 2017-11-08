package com.yousails.chrenai.person.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.login.ui.AuthActivity;
import com.yousails.chrenai.person.ui.ConversationListActivity;
import com.yousails.chrenai.person.ui.FootMarkActivity;
import com.yousails.chrenai.person.ui.MyActsActivity;
import com.yousails.chrenai.person.ui.MyCodeActivity;
import com.yousails.chrenai.person.ui.MyEnjoyActivity;
import com.yousails.chrenai.person.ui.PersonProfileActivity;
import com.yousails.chrenai.person.ui.SettingActivity;
import com.yousails.chrenai.utils.BadgeViewUtil;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.BadgeView;
import com.yousails.chrenai.view.CertifcationDialog;
import com.yousails.chrenai.view.CheckCertifDialog;
import com.yousails.chrenai.view.CircleImageView;
import com.yousails.chrenai.view.SignDialog;
import com.yousails.chrenai.zxing.camera.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/8/7.
 */

public class MyPageFragment extends BaseFragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks{
    private LinearLayout settingLayout;
    private RelativeLayout chatLayout;

    private TextView nameView;
    private TextView centifView;
    private ImageView vipView;
    private CircleImageView headView;

    private RelativeLayout personInforLayout;
    private RelativeLayout scanLayout;
    private RelativeLayout qrLayout;

    private LinearLayout joinLayout;
    private RelativeLayout newEnroLayout;
    private RelativeLayout endLayout;

    private RelativeLayout newRelease;
    private RelativeLayout endRelease;

    private RelativeLayout releaseLayout;
    private View dividerLin;
    private LinearLayout release_mine_layout;
    private RelativeLayout footmarkLayout;

    private CertifcationDialog certifcationDialog;

    private boolean  isLogin;


    private TextView level1,level2,level3;

    ActivitiesBean scanActivity=new ActivitiesBean();

    BadgeView notificationBadge,publishBadge,enjoyBadge;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_mypage, container, false);

        findViews();
        setListener();
        initView();
        return mRootView;
    }

    private static final int RC_CAMERA_PERM = 124;
    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        String[] perms = { Manifest.permission.CAMERA };
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Intent capture = new Intent();
            capture.setClass(getActivity(), CaptureActivity.class);
            capture.putExtra("activity_id",scanActivity.getId());
            capture.putExtra("activity_name",scanActivity.getTitle());
            startActivityForResult(capture,9009);
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

    private void findViews(){

        View target = mRootView.findViewById(R.id.chat_layout);
        notificationBadge = new BadgeView(getActivity());
        notificationBadge.setTargetView(target);
        notificationBadge.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);

        publishBadge = new BadgeView(getActivity());
        publishBadge.setTargetView(mRootView.findViewById(R.id.tv_release_end));
        publishBadge.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);

        enjoyBadge = new BadgeView(getActivity());
        enjoyBadge.setTargetView(mRootView.findViewById(R.id.tv_enjoy_end));
        enjoyBadge.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);

        BadgeViewUtil.builder(getActivity()).setMyFragmentBadge(notificationBadge,publishBadge,enjoyBadge);

        nameView = (TextView) mRootView.findViewById(R.id.name_tview);
        centifView = (TextView) mRootView.findViewById(R.id.iv_ident);
        vipView=(ImageView) mRootView.findViewById(R.id.iv_vip);
        headView = (CircleImageView) mRootView.findViewById(R.id.iv_head);

        settingLayout=(LinearLayout)mRootView.findViewById(R.id.setting_layout);//设置
        chatLayout=(RelativeLayout)mRootView.findViewById(R.id.chat_layout);//回话

        personInforLayout=(RelativeLayout)mRootView.findViewById(R.id.person_infor_layout);//用户个人信息

        scanLayout=(RelativeLayout)mRootView.findViewById(R.id.scan_layout);//扫描
        qrLayout=(RelativeLayout)mRootView.findViewById(R.id.qr_layout);//二维码

        joinLayout=(LinearLayout)mRootView.findViewById(R.id.join_layout);//我的参与
        newEnroLayout=(RelativeLayout)mRootView.findViewById(R.id.new_enroll_layout);//新报名
        endLayout=(RelativeLayout)mRootView.findViewById(R.id.end_layout);//已结束

        releaseLayout=(RelativeLayout)mRootView.findViewById(R.id.release_layout);//我的发布
        dividerLin=(View)mRootView.findViewById(R.id.divider_line);
        release_mine_layout=(LinearLayout)mRootView.findViewById(R.id.release_mine_layout);

        footmarkLayout=(RelativeLayout)mRootView.findViewById(R.id.footmark_layout);//我的足迹

        newRelease=(RelativeLayout)mRootView.findViewById(R.id.release_new);//我的足迹
        endRelease=(RelativeLayout)mRootView.findViewById(R.id.release_end);//我的足迹


        level1 = (TextView) mRootView.findViewById(R.id.level1);
        level2 = (TextView) mRootView.findViewById(R.id.level2);
        level3 = (TextView) mRootView.findViewById(R.id.level3);
    }

    private void setListener(){

        settingLayout.setOnClickListener(this);
        chatLayout.setOnClickListener(this);

        personInforLayout.setOnClickListener(this);

        scanLayout.setOnClickListener(this);
        qrLayout.setOnClickListener(this);

        joinLayout.setOnClickListener(this);
        newEnroLayout.setOnClickListener(this);
        endLayout.setOnClickListener(this);

        releaseLayout.setOnClickListener(this);
        footmarkLayout.setOnClickListener(this);

        newRelease.setOnClickListener(this);
        endRelease.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    SignDialog signDialog;
    String name = "";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==9009&&resultCode==RESULT_OK){
            final String user_id = data.getStringExtra("user_id");
            final String activty_id = data.getStringExtra("activity_id");
            final String activity_name = data.getStringExtra("activity_name");
            getUser(user_id,activty_id,activity_name);
            /*signDialog = new SignDialog(mContext, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userSign(activty_id,user_id);
                    signDialog.dismiss();
                }
            },"是否确认为 用户"+user_id+"\n"+activity_name+" 签到？");
            signDialog.show();*/
        }

    }
    private String signUserName="";
    private void getUser(final String userId,final String activityId,final String activityName){
        Map<String, String> params = new HashMap<>();
        params.put("id", userId);

        OkHttpUtils.get().url(ApiConstants.GET_USER+userId).addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                Log.e("error",response);
                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        Toast.makeText(getActivity(),"用户信息不存在",Toast.LENGTH_LONG).show();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.e("response",response);
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.optString("name");
                    signUserName = name;
                    userSignStats(activityId,userId);
                    /*signDialog = new SignDialog(mContext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userSign(activityId,userId);
                            signDialog.dismiss();
                        }
                    },"是否确认为 用户"+name+"\n"+activityName+" 签到？");
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
                        ToastUtils.showLong(getActivity(),message);


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
                    if(scanActivity.getSign_type().equals("once")){
                        if(total==0){
                            signDialog = new SignDialog(mContext,
                                    new SignDialog.SignDialogListener() {
                                        @Override
                                        public void onConfirm() {
                                            userSign(activityId,userId);
                                            signDialog.dismiss();
                                        }

                                        @Override
                                        public void onCancel() {
                                            signDialog.dismiss();
                                        }
                                    }, "是否确认为 用户" + signUserName + "\n" + scanActivity.getTitle() + " 签到？");
                            signDialog.show();
                            signMsg = "签到成功";
                        }else{
                            ToastUtils.showLong(getActivity(),"用户已签到！");
                        }
                    }else if(scanActivity.getSign_type().equals("twice")){
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
                            },"是否确认为 用户"+signUserName+"\n"+scanActivity.getTitle()+" 签到？");
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
                            },"是否确认为 用户"+signUserName+"\n"+scanActivity.getTitle()+" 退出？");
                            signDialog.show();
                            signMsg = "退出成功";
                        }
                    }else{
                        ToastUtils.showLong(getActivity(),"请手工签到！");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void userSign(String activityId,String userId){
        Map<String, String> params = new HashMap<>();
        params.put("id",activityId);
        params.put("user_id", userId);

        OkHttpUtils.post().url(ApiConstants.USER_SIGN+activityId+"/records").addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        String message = jsonObj.optString("message");
                        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                        //finish();

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.optString("message");
                    Toast.makeText(getActivity(),signMsg,Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        isLogin = AppPreference.getInstance(mContext).readLogin();
        String certification = AppPreference.getInstance(mContext).readCertification();
        String isVip = AppPreference.getInstance(mContext).readIsVip();
        String userName = AppPreference.getInstance(mContext).readUserName();
        String headUrl = AppPreference.getInstance(mContext).readAvatar();
        AppPreference.getInstance(mContext).readEMName();

        String []levels = AppPreference.getInstance(mContext).readLevel().split("-");
        level1.setText(levels[0]);
        level2.setText(levels[1]);
        level3.setText(levels[2]);

        if (isLogin) {
            nameView.setText(userName);
        } else {
            nameView.setText("未登录");
        }

        if (StringUtil.isNotNull(headUrl)) {
            Glide.with(mContext).load(headUrl).into(headView);
        } else {
            String sex = AppPreference.getInstance(mContext).readGender();
            if ("male".equals(sex)) {
                headView.setImageResource(R.drawable.ic_avatar);
            } else {
                headView.setImageResource(R.drawable.ic_avatar_woman);
            }

        }

        if (StringUtil.isNotNull(isVip) && "1".equals(isVip)) {

            vipView.setVisibility(View.VISIBLE);
            releaseLayout.setVisibility(View.VISIBLE);
            dividerLin.setVisibility(View.VISIBLE);
            release_mine_layout.setVisibility(View.VISIBLE);

        } else {
            vipView.setVisibility(View.GONE);
            releaseLayout.setVisibility(View.GONE);
            dividerLin.setVisibility(View.GONE);
            release_mine_layout.setVisibility(View.GONE);
        }

        if (StringUtil.isNotNull(certification) && "1".equals(certification)) {

            centifView.setVisibility(View.VISIBLE);
        } else {
            centifView.setVisibility(View.GONE);
        }

    }


    private OptionsPickerView activityOption;


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.setting_layout:
                Intent settingIntent = new Intent(mContext, SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.chat_layout:
                //notificationBadge.setBadgeCount(0);
                getActivity().startActivity(new Intent(getActivity(), ConversationListActivity.class));
                break;
            case R.id.person_infor_layout:
                Intent proIntent = new Intent(mContext, PersonProfileActivity.class);
                startActivity(proIntent);
                break;
            case R.id.scan_layout:
                getUserActivities();
                break;
            case R.id.qr_layout:
                Intent intent=new Intent(getActivity(),MyCodeActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.join_layout:
                getActivity().startActivity(new Intent(getActivity(), MyEnjoyActivity.class).putExtra("from","mine").putExtra("isFinish",false).putExtra("user",""));
                break;
            case R.id.new_enroll_layout:
                getActivity().startActivity(new Intent(getActivity(), MyEnjoyActivity.class).putExtra("from","mine").putExtra("isFinish",false).putExtra("user",""));
                break;
            case R.id.end_layout:
                getActivity().startActivity(new Intent(getActivity(), MyEnjoyActivity.class).putExtra("from","mine").putExtra("isFinish",true).putExtra("user",""));
                break;
            case R.id.release_new:
                getActivity().startActivity(new Intent(getActivity(), MyActsActivity.class).putExtra("from","mine").putExtra("isFinish",false).putExtra("user",""));
                break;
            case R.id.release_end:
                getActivity().startActivity(new Intent(getActivity(), MyActsActivity.class).putExtra("from","mine").putExtra("isFinish",true).putExtra("user",""));
                break;
            case R.id.release_layout:
                getActivity().startActivity(new Intent(getActivity(), MyActsActivity.class).putExtra("from","mine").putExtra("isFinish",false).putExtra("user",""));
                break;
            case R.id.footmark_layout:
                if (isLogin) {

                    String certification=AppPreference.getInstance(getActivity()).readCertification();
                    if (StringUtil.isNotNull(certification) && "1".equals(certification)) {
                        Intent intent1 = new Intent(getActivity(), FootMarkActivity.class);
                        startActivity(intent1);

                    } else {
                        showDialog();
                    }

                } else {
                    CustomToast.createToast(mContext, "请先登录");
                }
                break;
        }
    }


    private void showDialog() {
        if (certifcationDialog == null) {
            certifcationDialog = new CertifcationDialog(mContext,"person",onClickListener);
        }
        certifcationDialog.show();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_submit:
                    certifcationDialog.dismiss();

                    break;
            }
        }
    };


    private void getUserActivities(){
        Map<String, String> params = new HashMap<>();
        params.put("permission", "check_sign_in");
        params.put("sign_type", "once,twice");

        OkHttpUtils.get().url(ApiConstants.USER_ACTIVITIES_MANAGED).addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        String message = jsonObj.optString("message");


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
                    JSONObject paggeArray= jsonObject.optJSONObject("meta").optJSONObject("pagination");
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    Type pageType = new TypeToken<PaginationBean>() {}.getType();
                    PaginationBean  paginationBean = new Gson().fromJson(paggeArray.toString(), pageType);
                    int total= 0;
                    if(paginationBean!=null){
                        total=Integer.valueOf(paginationBean.getTotal());
                    }
                    if(total>0){
                        Type type = new TypeToken<ArrayList<ActivitiesBean>>() {
                        }.getType();
                        ArrayList<ActivitiesBean> activitiesList = new Gson().fromJson(jsonArray.toString(), type);
                        showActivityOption(activitiesList);
                    }else{
                        Toast.makeText(getActivity(),"暂无可扫码的活动",Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private Dialog activitiesDialog;
    private void showActivityOption(final ArrayList<ActivitiesBean> activitiesList){
         activitiesDialog = new Dialog(getActivity(), R.style.BottomDialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_my_activities, null);
        activitiesDialog.setContentView(contentView);
        TextView tvAdd = (TextView) contentView.findViewById(R.id.tv_add);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activitiesDialog.dismiss();
            }
        });
        ListView my_activities = (ListView) contentView.findViewById(R.id.my_activities);
        ActivityAdapter adapter = new ActivityAdapter(activitiesList);
        my_activities.setAdapter(adapter);
        my_activities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activitiesDialog.dismiss();
                scanActivity = activitiesList.get(position);
                cameraTask();

            }
        });
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        activitiesDialog.getWindow().setGravity(Gravity.BOTTOM);
        activitiesDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        activitiesDialog.show();
    }

    class ActivityAdapter extends BaseAdapter{
        ArrayList<ActivitiesBean> mActivitiesList;
        private LayoutInflater mInflater;
        public ActivityAdapter(ArrayList<ActivitiesBean> activitiesList){
            mActivitiesList = activitiesList;
            mInflater = LayoutInflater.from(getActivity());
        }
        @Override
        public int getCount() {
            return mActivitiesList.size();
        }
        @Override
        public Object getItem(int position) {
            return mActivitiesList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_my_activities, null);
                holder.title = (TextView)convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.title.setText((String)mActivitiesList.get(position).getTitle());
            return convertView;
        }
    }
    public final class ViewHolder{
        public TextView title;
    }


    /**
     * 更改昵称
     */
    public void updateNickname() {
        nameView.setText(AppPreference.getInstance(mContext).readUserName());
    }

    /**
     * 更改头像
     */
    public void updateAvatar() {
        String headUrl = AppPreference.getInstance(mContext).readAvatar();
        Glide.with(mContext).load(headUrl).into(headView);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.e("isVisibleToUser",""+isVisibleToUser);
        if (isVisibleToUser){

        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        Log.e("onResume","true");
        BadgeViewUtil.builder(getActivity()).setMyFragmentBadge(notificationBadge,publishBadge,enjoyBadge);

        super.onResume();
    }
}
