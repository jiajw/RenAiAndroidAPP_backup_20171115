package com.yousails.chrenai.person.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.login.ui.LoginActivity;

/**
 * Created by Administrator on 2017/6/16.
 */

public class PersonalFragment extends BaseFragment {
    private Button exitBtn;
    private TextView tipsView;
    private boolean isLogin;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_person, container, false);
        findViews();
        return mRootView;
    }


    private void findViews() {
        tipsView = (TextView) mRootView.findViewById(R.id.tv_tips);
        exitBtn = (Button) mRootView.findViewById(R.id.exit_btn_submit);

        initView();


        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    clearUserInfor();
                    initView();
                    logout();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }

            }
        });

    }

    @Override
    public void initData() {

    }

    private void initView() {
        isLogin = AppPreference.getInstance(mContext).readLogin();
        if (isLogin) {
            tipsView.setText("登录成功");
            exitBtn.setText("退出");
        } else {
            tipsView.setText("尚未登录");
            exitBtn.setText("登录");
        }
    }

    /**
     * 清空用户登录信息
     */
    private void clearUserInfor() {
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

    private void logout() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("ss", "退出成功");
            }

            @Override
            public void onError(int i, String s) {
                Log.e("ss", "退出失败" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
