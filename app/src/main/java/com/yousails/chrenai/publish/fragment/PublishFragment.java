package com.yousails.chrenai.publish.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.common.ScreenUtil;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.ui.SearchActivity;
import com.yousails.chrenai.login.ui.AuthActivity;
import com.yousails.chrenai.login.ui.LoginActivity;
import com.yousails.chrenai.login.ui.RegInforActivity;
import com.yousails.chrenai.login.ui.RegisterActivity;
import com.yousails.chrenai.publish.Activity.EditorActivity;
import com.yousails.chrenai.publish.ui.PublishActivity;
import com.yousails.chrenai.publish.widget.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CheckCertifDialog;
import com.yousails.chrenai.view.RegisterErrorDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/6/30.
 */

public class PublishFragment extends BaseFragment {

    private Button btnPublish;
    private ImageView publishView;
    private CheckCertifDialog checkCertifDialog;

//    TimePickerDialog mDialogAll;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_publish, container, false);
        findViews();
        return mRootView;
    }


    public void findViews() {
        btnPublish = (Button) mRootView.findViewById(R.id.btn_publish);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishActivity.launch(getContext());
            }
        });

        Button testButton = (Button) mRootView.findViewById(R.id.btn_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomToast toast = CustomToast.makeText(getContext(), "测试", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER);
                toast.show();
//                int screenWidth = ScreenUtil.getScreenWidth(getContext());
//
//                SuperToast st = SuperToast.create(getContext(), "测试", 3000);
//                st.setWidth(200);
//                st.setHeight(300);
//                st.setTextColor(Color.WHITE);
//                st.setColor(Color.parseColor("#aa000000"));
//                st.setGravity(Gravity.CENTER);
//                st.setAnimations(Style.ANIMATIONS_SCALE);
//                st.show();

//                AddApplyItemActivity.launchForResult(getActivity(), 3, 1);

//                List<String> day = new ArrayList<>();
//                day.add("5月6日 周x");
//                day.add("5月7日 周x");
//                day.add("5月8日 周x");
//                day.add("5月9日 周x");
//                day.add("5月10日 周x");
//                day.add("5月11日 周x");
//                day.add("5月12日 周x");
//                day.add("5月13日 周x");
//                day.add("5月14日 周x");
//                day.add("5月15日 周x");
//                day.add("5月16日 周x");
//                day.add("5月17日 周x");
//                day.add("5月18日 周x");
//
//                List<String> date = new ArrayList<String>();
//                date.add("上午");
//                date.add("下午");
//
//                List<String> hours = new ArrayList<String>();
//                hours.add("1");
//                hours.add("2");
//                hours.add("3");
//                hours.add("4");
//                hours.add("5");
//                hours.add("6");
//                hours.add("7");
//                hours.add("8");
//                hours.add("9");
//                hours.add("10");
//                hours.add("11");
//                hours.add("12");
//
//
//                List<String> min = new ArrayList<String>();
//                min.add("0");
//                min.add("1");
//                min.add("2");
//                min.add("3");
//                min.add("4");
//                min.add("5");
//                min.add("6");
//                min.add("7");
//                min.add("8");
//                min.add("9");
//                min.add("10");
//                min.add("11");
//                min.add("12");
//                min.add("13");
//                min.add("14");
//                min.add("15");
//                min.add("16");
//                min.add("17");
//                min.add("18");
//                min.add("19");
//                min.add("20");
//                min.add("21");
//                min.add("22");
//                min.add("23");
//                min.add("24");
//                min.add("25");
//                min.add("26");
//                min.add("27");
//                min.add("28");
//                min.add("29");
//                min.add("30");
//
//                CustomOptionPickerView<String> dialog = new CustomOptionPickerView<String>(getContext());
//                dialog.setTitle("测试标题");
//                dialog.setData(day, date, hours, min);
//                dialog.setIsCanOutSideCancelable(true);
//                dialog.show();

            }
        });

        mRootView.findViewById(R.id.richEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditorActivity.launchForResult(getActivity(), "哈哈哈");
            }
        });



        publishView=(ImageView)mRootView.findViewById(R.id.publish_view);

        publishView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCertif();
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfor();
    }

    /**
     * 判断是否实名认证
     */
    private void checkCertif(){
        boolean isLogin = AppPreference.getInstance(mContext).readLogin();
//        String certification=AppPreference.getInstance(getActivity()).readCertification();
//        String userId=AppPreference.getInstance(mContext).readUerId();
//        String status= AppPreference.getInstance(mContext).readStatus(userId);
        String isVip=AppPreference.getInstance(mContext).readIsVip();
        if (isLogin) {

            if(StringUtil.isNotNull(isVip)&&"1".equals(isVip)){
                PublishActivity.launch(getContext());
            }else{
                Toast.makeText(getContext(),"您没有活动发布权限",Toast.LENGTH_SHORT).show();
            }


            /*if (StringUtil.isNotNull(certification) && "1".equals(certification)) {
                PublishActivity.launch(getContext());
            } else {
                if(StringUtil.isNotNull(status)){
                    if("applied".equals(status)){
                        com.yousails.chrenai.utils.CustomToast.createToast(getActivity(),"审核中");
                    }else if("rejected".equals(status)){
                        showDialog();
                    }else{
                        PublishActivity.launch(getContext());
                    }
                }else{
                    showDialog();
                }

            }*/
        } else {
//            Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
            Intent loginIntent=new Intent(mContext,LoginActivity.class);
            loginIntent.putExtra("from","publish");
            startActivity(loginIntent);
        }

    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_submit:
                    checkCertifDialog.dismiss();

                    Intent intent=new Intent(getActivity(),AuthActivity.class);
                    startActivity(intent);

                    break;

            }
        }
    };

    private void showDialog() {
        if (checkCertifDialog == null) {
            checkCertifDialog = new CheckCertifDialog(mContext,"publish",onClickListener);
        }
        checkCertifDialog.show();
    }


    /**
     * 获取用户信息，用于判断用户是否是vip
     */
    private void getUserInfor() {
        boolean isLogin=AppPreference.getInstance(mContext).readLogin();
        if(!isLogin)return;

        if (!NetUtil.detectAvailable(mContext)) return;

        String token = AppPreference.getInstance(mContext).readToken();

        OkHttpUtils.get().url(ApiConstants.UPDATE_USERINFOR_API).addHeader("Authorization", token).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {}

            @Override
            public void onResponse(String response, int id) {
                System.out.println("----->" + response);
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean isVip = jsonObject.optBoolean("is_vip");
                        if(isVip){
                            AppPreference.getInstance(mContext).writeIsVip("1");
                        }else{
                            AppPreference.getInstance(mContext).writeIsVip("");
                        }

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }

            }
        });
    }


}
