package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.bean.EMUser;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.home.ui.ChatActivity;
import com.yousails.chrenai.im.DemoHelper;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by liuwen on 2017/8/11.
 */

public class ConversationListActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;

    private RelativeLayout xianer;
    private RelativeLayout system;
    private RelativeLayout activity;

    EaseConversationList conversationListView;
    List<EMConversation> conversations;

    private String im_usernames="";

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_my_conversation);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getUsers();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            if(messages.size()>0){
                for (EMMessage message : messages) {
                    //EaseUI.getInstance().getNotifier().onNewMsg(message);
                }
                getUsers();
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @Override
    protected void findViews() {


        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText(R.string.txt_my_conversation);

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);

        xianer = (RelativeLayout)findViewById(R.id.xianer);
        ((ImageView)xianer.findViewById(R.id.avatar)).setImageResource(R.drawable.avatar_xianer);
        ((TextView)xianer.findViewById(R.id.name)).setText("贤二机器僧");
        ((TextView)xianer.findViewById(R.id.message)).setText("");
        ((TextView)xianer.findViewById(R.id.time)).setText("");
        xianer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConversationListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, "xianer").putExtra("nickname","贤二机器僧"));
            }
        });
        system = (RelativeLayout)findViewById(R.id.system);
        ((ImageView)system.findViewById(R.id.avatar)).setImageResource(R.drawable.avatar_system);
        ((TextView)system.findViewById(R.id.name)).setText("官方通知");
        ((TextView)system.findViewById(R.id.message)).setText("");
        ((TextView)system.findViewById(R.id.time)).setText("");
        system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConversationListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, "system_notification").putExtra("nickname","官方通知"));
            }
        });
        activity = (RelativeLayout)findViewById(R.id.activity);
        ((ImageView)activity.findViewById(R.id.avatar)).setImageResource(R.drawable.avatar_activity);
        ((TextView)activity.findViewById(R.id.name)).setText("活动助手");
        ((TextView)activity.findViewById(R.id.message)).setText("");
        ((TextView)activity.findViewById(R.id.time)).setText("");


        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ConversationListActivity.this,AssistantActivity.class));
            }
        });



        /*
        //fragment方式加载会话列表
        EaseConversationListFragment conversationListFragment = new EaseConversationListFragment();
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Log.e("TalkActivity", EaseConstant.EXTRA_USER_ID +" "+ conversation.conversationId());
                startActivity(new Intent(ConversationListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()).putExtra("nickname",DemoHelper.getInstance().getContact(conversation.conversationId()).getNickname()));
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.container, conversationListFragment).commit();*/


        //会话列表控件 listview 方式
        conversationListView = (EaseConversationList) findViewById(R.id.list);
        /*conversations = loadConversationList();
        //conversations = EMClient.getInstance().chatManager().getConversationsByType(EMConversation.EMConversationType.Chat);

        //初始化，参数为会话列表集合
        conversationListView.init(conversations);

        //刷新列表
        conversationListView.refresh();*/
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversations.get(position);
                conversation.markAllMessagesAsRead();
                //conversationListView.notify();
                //startActivity(new Intent(ConversationListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
                startActivity(new Intent(ConversationListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()).putExtra("nickname",DemoHelper.getInstance().getContact(conversation.conversationId()).getNickname()));
            }
        });

        getUsers();


    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
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
    protected List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {

                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //im_usernames+=conversation.conversationId()+",";
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            if(sortItem.second.conversationId().equals("xianer")){
                ((TextView)xianer.findViewById(R.id.message)).setText(sortItem.second.getLastMessage().getBody().toString().replace("txt","").replace(":","").replace("\"",""));
                ((TextView)xianer.findViewById(R.id.time)).setText("刚刚");
                continue;
            }else if(sortItem.second.conversationId().equals("system_notification")){
                ((TextView)system.findViewById(R.id.message)).setText(sortItem.second.getLastMessage().getBody().toString());
                ((TextView)system.findViewById(R.id.time)).setText("刚刚");
                continue;
            }else if(sortItem.second.conversationId().equals("activity_assistant")){
                try {

                    ((TextView)activity.findViewById(R.id.message)).setText(sortItem.second.getLastMessage().getStringAttribute("message"));
                    ((TextView)activity.findViewById(R.id.time)).setText(DateUtils.getTimestampString((new Date(sortItem.second.getLastMessage().getMsgTime()))));
                    if (sortItem.second.getUnreadMsgCount() > 0) {
                        // show unread message count
                        ((TextView)activity.findViewById(R.id.unread_msg_number)).setText(String.valueOf(sortItem.second.getUnreadMsgCount()));
                        ((TextView)activity.findViewById(R.id.unread_msg_number)).setVisibility(View.VISIBLE);
                    } else {
                        ((TextView)activity.findViewById(R.id.unread_msg_number)).setVisibility(View.INVISIBLE);
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                continue;
            }
            list.add(sortItem.second);
        }
        return list;
    }
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }


    private void getUsers(){
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        synchronized (allConversations) {
            for (String s : allConversations.keySet()) {
                im_usernames += s + ",";
            }
        }
        Log.e("im_usernames",im_usernames);
            Map<String, String> params = new HashMap<>();
            params.put("im_usernames", im_usernames);

            OkHttpUtils.get().url(ApiConstants.EM_USERS_API).addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, String response, Exception e, int id) {
                }
                @Override
                public void onResponse(String response, int id) {
                    Log.e("im_usernames",response);
                    if (StringUtil.isNotNull(response)) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.optJSONArray("data");

                            Type type = new TypeToken<ArrayList<EMUser>>() {
                            }.getType();
                            ArrayList<EMUser> activitiesList = new Gson().fromJson(jsonArray.toString(), type);



                            for (EMUser emUser : activitiesList) {
                                EaseUser user = new EaseUser(emUser.getIm_username());
                                user.setAvatar(emUser.getAvatar());
                                user.setNickname(emUser.getName());
                                //EaseCommonUtils.setUserInitialLetter(user);
                                DemoHelper.getInstance().addContact(emUser);
                            }
                            conversations = loadConversationList();
                            //        conversations = EMClient.getInstance().chatManager().getConversationsByType(EMConversation.EMConversationType.Chat);

                            //初始化，参数为会话列表集合
                            conversationListView.init(conversations);
                            //刷新列表
                            conversationListView.refresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
}
