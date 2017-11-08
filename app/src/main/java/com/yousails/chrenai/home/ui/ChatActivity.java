package com.yousails.chrenai.home.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;

/**
 * Created by Administrator on 2017/8/3.
 */

public class ChatActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private FrameLayout contentFrame;
    private EaseChatFragment chatFragment;

    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void init() {
//        getIntent().getStringExtra()
//        intent2.putExtra("userId", "");
//        intent2.putExtra("chatType", "");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        //titleView.setText("在线咨询");
        titleView.setText(TextUtils.isEmpty(getIntent().getStringExtra("nickname"))?"在线咨询":getIntent().getStringExtra("nickname"));
        //titleView.setText(DemoHelper.getInstance().getContact(getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID)).getNickname());

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);

        contentFrame=(FrameLayout)findViewById(R.id.container);

        chatFragment = new EaseChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        chatFragment.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {
                //我的信息，一般本地自己取出来
                message.setAttribute("username", AppPreference.getInstance(ModelApplication.mContext).readEMName());
                message.setAttribute("avatar", AppPreference.getInstance(ModelApplication.mContext).readAvatar());
                message.setAttribute("nickname", AppPreference.getInstance(ModelApplication.mContext).readUserName());

            }

            @Override
            public void onEnterToChatDetails() {

            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;

        }
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }
}
