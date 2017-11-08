package com.yousails.chrenai.person.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.login.ui.LoginActivity;
import com.yousails.chrenai.person.ui.MyActsActivity;
import com.yousails.chrenai.person.ui.MyCodeActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.zxing.camera.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/8/7.
 */

public class MyEnjoyPageFragment extends BaseFragment implements View.OnClickListener{
    private LinearLayout settingLayout;
    private RelativeLayout chatLayout;

    private RelativeLayout personInforLayout;
    private RelativeLayout scanLayout;
    private RelativeLayout qrLayout;

    private LinearLayout joinLayout;
    private RelativeLayout newEnroLayout;
    private RelativeLayout endLayout;

    private RelativeLayout newRelease;
    private RelativeLayout endRelease;

    private RelativeLayout releaseLayout;
    private RelativeLayout footmarkLayout;

    private Button exitBtn;
    private TextView tipsView;
    private boolean  isLogin;
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_mypage, container, false);
        findViews();
        setListener();
        initView();
        return mRootView;
    }

    private void findViews(){
        tipsView=(TextView)mRootView.findViewById(R.id.tv_tips);
        exitBtn=(Button)mRootView.findViewById(R.id.exit_btn_submit);

        settingLayout=(LinearLayout)mRootView.findViewById(R.id.setting_layout);//设置
        chatLayout=(RelativeLayout)mRootView.findViewById(R.id.chat_layout);//回话

        personInforLayout=(RelativeLayout)mRootView.findViewById(R.id.person_infor_layout);//用户个人信息

        scanLayout=(RelativeLayout)mRootView.findViewById(R.id.scan_layout);//扫描
        qrLayout=(RelativeLayout)mRootView.findViewById(R.id.qr_layout);//二维码

        joinLayout=(LinearLayout)mRootView.findViewById(R.id.join_layout);//我的参与
        newEnroLayout=(RelativeLayout)mRootView.findViewById(R.id.new_enroll_layout);//新报名
        endLayout=(RelativeLayout)mRootView.findViewById(R.id.end_layout);//已结束

        releaseLayout=(RelativeLayout)mRootView.findViewById(R.id.release_layout);//我的发布

        footmarkLayout=(RelativeLayout)mRootView.findViewById(R.id.footmark_layout);//我的足迹


        newRelease=(RelativeLayout)mRootView.findViewById(R.id.release_new);//我的足迹
        endRelease=(RelativeLayout)mRootView.findViewById(R.id.release_end);//我的足迹

    }

    private void setListener(){
        exitBtn.setOnClickListener(this);

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

    private void initView(){
        isLogin= AppPreference.getInstance(mContext).readLogin();
        if(isLogin){
            tipsView.setText("登录成功");
            exitBtn.setText("退出");
        }else{
            tipsView.setText("尚未登录");
            exitBtn.setText("登录");
        }
    }

    /**
     * 清空用户登录信息
     */
    private void clearUserInfor(){
        //保存到sharepreference
        AppPreference.getInstance(mContext).writeUserId("");
        AppPreference.getInstance(mContext).writeLastUserId("");
        AppPreference.getInstance(mContext).writeUserName("");
        AppPreference.getInstance(mContext).writeAvatar("");
        AppPreference.getInstance(mContext).writeGender("male");
        // AppPreference.getInstance(mContext).writePhone(userBean.getId(), userBean.getPhone());
        AppPreference.getInstance(mContext).setLogin(false);
        AppPreference.getInstance(mContext).setLogout(true);
    }

    private void logout(){
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("ss","退出成功");
            }

            @Override
            public void onError(int i, String s) {
                Log.e("ss","退出失败"+s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    private OptionsPickerView activityOption;


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.exit_btn_submit:
                if(isLogin){
                    clearUserInfor();
                    initView();
                    logout();
                }else{
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
                break;
            case R.id.setting_layout:
                break;
            case R.id.chat_layout:
                break;
            case R.id.person_infor_layout:
                break;
            case R.id.scan_layout:
                getUserActivities();
                break;
            case R.id.qr_layout:
                Intent intent=new Intent(getActivity(),MyCodeActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.join_layout:
                break;
            case R.id.new_enroll_layout:

                break;
            case R.id.end_layout:
                break;
            case R.id.release_new:
                getActivity().startActivity(new Intent(getActivity(), MyActsActivity.class).putExtra("isFinish",false));
                break;
            case R.id.release_end:
                getActivity().startActivity(new Intent(getActivity(), MyActsActivity.class).putExtra("isFinish",true));
                break;
            case R.id.release_layout:
                break;
            case R.id.footmark_layout:
                break;
        }
    }

    private void getUserActivities(){
        Map<String, String> params = new HashMap<>();
        params.put("is_finished", "false");

        OkHttpUtils.get().url(ApiConstants.USER_ACTIVITIES).addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
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
                        Toast.makeText(getActivity(),"您当前没有未完成活动",Toast.LENGTH_SHORT).show();
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
                Intent capture = new Intent();
                capture.setClass(getActivity(), CaptureActivity.class);
                capture.putExtra("activity_id",activitiesList.get(position).getId());
                startActivity(capture);
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

}
