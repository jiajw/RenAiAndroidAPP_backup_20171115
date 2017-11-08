package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.ui.ChatActivity;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.TimeUtil;
import com.yousails.chrenai.view.CertifcationDialog;
import com.yousails.chrenai.view.CircleImageView;

import static com.yousails.chrenai.R.id.chat_layout;

/**
 * Created by Administrator on 2017/8/10.
 */

public class PersonActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private CircleImageView headView;
    private TextView nameView;
    private ImageView vImageView;
    private TextView certifView;
    private RelativeLayout publishLayout;
    private LinearLayout release_divider_line;
    private LinearLayout release_child_layout;
    private RelativeLayout releaseNew;
    private RelativeLayout releaseCom;
    private RelativeLayout footMarkLayout;
    private LinearLayout chatLayout;
    private LinearLayout workHoursLayout;
    private TextView workHoursView;

    private UserBean userBean;
    private String im_userName = "";
    private boolean isLogin;
    private String name;
    private String avatar;
    private int working_hours;
    private boolean isCertif;
    private boolean isVip;

    private LinearLayout llEnjoy;
    private RelativeLayout enjoy_layout;
    private RelativeLayout enjoyNew;
    private RelativeLayout enjoyCom;

    private TextView level1,level2,level3;
    private TextView enjoyLable,releaseLable,footLable;

    private CertifcationDialog certifcationDialog;


    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_person);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        if (intent != null) {
            userBean = (UserBean) intent.getSerializableExtra("user");
            if (userBean != null) {
                im_userName = userBean.getIm_username();
                name=userBean.getName();
                avatar=userBean.getAvatar();
                isCertif=userBean.is_certificated();
                isVip=userBean.is_vip();
                working_hours=userBean.getWorking_hours();
            }
        }
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("个人主页");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);

        headView=(CircleImageView)findViewById(R.id.iv_head);
        nameView=(TextView)findViewById(R.id.name_tview);
        vImageView=(ImageView)findViewById(R.id.iv_vip);
        certifView=(TextView)findViewById(R.id.iv_ident);


        //他的发布
        publishLayout=(RelativeLayout)findViewById(R.id.release_layout);
        release_divider_line=(LinearLayout)findViewById(R.id.release_divider_line);
        release_child_layout=(LinearLayout)findViewById(R.id.release_child_layout);

        releaseNew = (RelativeLayout)findViewById(R.id.rl_release_new);
        releaseCom = (RelativeLayout)findViewById(R.id.rl_release_com);

        //足迹
        footMarkLayout=(RelativeLayout)findViewById(R.id.footmark_layout);
        //聊天
        chatLayout=(LinearLayout)findViewById(chat_layout);
        //行善时长
        workHoursLayout=(LinearLayout)findViewById(R.id.workhours_layout);
        workHoursView=(TextView)findViewById(R.id.workhours_tview);

        //参与
        llEnjoy = (LinearLayout) findViewById(R.id.llEnjoy);
        enjoy_layout = (RelativeLayout)findViewById(R.id.enjoy_layout);
        enjoyNew = (RelativeLayout)findViewById(R.id.rl_enjoy_new);
        enjoyCom = (RelativeLayout)findViewById(R.id.rl_enjoy_com);

        level1 = (TextView) findViewById(R.id.level1);
        level2 = (TextView) findViewById(R.id.level2);
        level3 = (TextView) findViewById(R.id.level3);

        enjoyLable = (TextView) findViewById(R.id.enjoy_label);
        releaseLable = (TextView) findViewById(R.id.release_label);
        footLable = (TextView) findViewById(R.id.foot_label);


       if(StringUtil.isNotNull(name)){
           nameView.setText(name);
       }

        if(StringUtil.isNotNull(avatar)){
            Glide.with(mContext).load(avatar).into(headView);
        }

        if(0!=working_hours){
            workHoursLayout.setVisibility(View.VISIBLE);
            workHoursView.setText(TimeUtil.getTimeByMinute(working_hours));
        }

        if(isCertif){
            certifView.setVisibility(View.VISIBLE);
        }

        if(isVip){
            vImageView.setVisibility(View.VISIBLE);
            publishLayout.setVisibility(View.VISIBLE);
            release_divider_line.setVisibility(View.VISIBLE);
            release_child_layout.setVisibility(View.VISIBLE);
        }else{
            publishLayout.setVisibility(View.GONE);
            release_divider_line.setVisibility(View.GONE);
            release_child_layout.setVisibility(View.GONE);
        }

        if(userBean.getId().equals(AppPreference.getInstance(PersonActivity.this).readUerId())){
            enjoyLable.setText("我的参与");
            releaseLable.setText("我的发布");
            footLable.setText("我的足迹");
            chatLayout.setVisibility(View.GONE);
        }else{
            enjoyLable.setText("TA的参与");
            releaseLable.setText("TA的发布");
            footLable.setText("TA的足迹");
        }


        String []levels = userBean.getLevel().split("-");
        level1.setText(levels[0]);
        level2.setText(levels[1]);
        level3.setText(levels[2]);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        publishLayout.setOnClickListener(this);
        footMarkLayout.setOnClickListener(this);
        chatLayout.setOnClickListener(this);
        releaseNew.setOnClickListener(this);
        releaseCom.setOnClickListener(this);
        enjoy_layout.setOnClickListener(this);
        enjoyNew.setOnClickListener(this);
        enjoyCom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.release_layout:
                startActivity(new Intent(this, MyActsActivity.class).putExtra("from","other").putExtra("isFinish",false).putExtra("user",""+userBean.getId()));
                break;
            case R.id.footmark_layout:
                if(userBean.getId().equals(AppPreference.getInstance(PersonActivity.this).readUerId())) {
                    if(isCertif){
                        Intent intent1 = new Intent(PersonActivity.this, FootMarkActivity.class);
                        startActivity(intent1);

                    }else{
                        showDialog("person");
                    }

                }else{
                    if(isCertif){
                        Intent intent1 = new Intent(PersonActivity.this, FootMarkActivity.class);
                        intent1.putExtra("user_id",userBean.getId());
                        startActivity(intent1);
                    }else{
                        showDialog(name);
                    }

                }


                break;
            case chat_layout:
                //先判断是否已登录
                isLogin = AppPreference.getInstance(mContext).readLogin();
                if (isLogin) {
                    Intent intent2 = new Intent(mContext, ChatActivity.class);
                    intent2.putExtra("userId", im_userName);
                    intent2.putExtra("nickname", name);
                    intent2.putExtra("chatType", 1);
                    startActivity(intent2);
                } else {
                    CustomToast.createToast(mContext, "请先登录");
                }

                break;

            case R.id.rl_release_new:
                startActivity(new Intent(this, MyActsActivity.class).putExtra("from","other").putExtra("isFinish",false).putExtra("user",""+userBean.getId()));
                break;
            case R.id.rl_release_com:
                startActivity(new Intent(this, MyActsActivity.class).putExtra("from","other").putExtra("isFinish",true).putExtra("user",""+userBean.getId()));
                break;
            case R.id.enjoy_layout:
                startActivity(new Intent(this, MyEnjoyActivity.class).putExtra("from","other").putExtra("isFinish",false).putExtra("user",""+userBean.getId()));
                break;
            case R.id.rl_enjoy_new:
                startActivity(new Intent(this, MyEnjoyActivity.class).putExtra("from","other").putExtra("isFinish",false).putExtra("user",""+userBean.getId()));
                break;
            case R.id.rl_enjoy_com:
                startActivity(new Intent(this, MyEnjoyActivity.class).putExtra("from","other").putExtra("isFinish",true).putExtra("user",""+userBean.getId()));
                break;
        }

    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    private void showDialog(String name) {
        if (certifcationDialog == null) {
            certifcationDialog = new CertifcationDialog(mContext,name,onClickListener);
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
}
