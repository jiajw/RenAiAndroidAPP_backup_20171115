package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.ui.ActivitDetailActivity;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.umeng.socialize.utils.DeviceConfig.context;
import static com.yousails.chrenai.R.id.recyclerView;
import static com.yousails.chrenai.R.id.webView;

/**
 * Created by Administrator on 2017/8/10.
 */

public class AssistantActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;

    private RecyclerView recycler;
    List<EMMessage> messages;
    MessageAdapter adapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_assistant);
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
        titleView.setText("活动助手");

        recycler = (RecyclerView) findViewById(R.id.recycler);


        //EMConversation conversation = EMClient.getInstance().chatManager().getConversation("activity_assistant");
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation("activity_assistant", EaseCommonUtils.getConversationType(1), true);
        conversation.markAllMessagesAsRead();
        //获取此会话的所有消息
        messages = conversation.getAllMessages();
        int pagesize = 20;
        int msgCount = messages != null ? messages.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            //messages = conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
            messages = conversation.loadMoreMsgFromDB(msgId,pagesize);
        }

//          Collections.reverse(messages);
        Log.e("messages.count",messages.size()+"");

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        adapter=new MessageAdapter();
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(EMMessage message) {
                try {
                    String sTitle = message.getStringAttribute("title");
                    String sMessage = message.getStringAttribute("message");
                    String sType = message.getStringAttribute("type");
                    String activity_id = message.getStringAttribute("activity_id");
                    String url = message.getStringAttribute("url");
                    gotoWhere(activity_id,url);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

        //recycler.smoothScrollBy(distance,duration);
        //recycler.scrollToPosition(messages.size());
        recycler.smoothScrollToPosition(messages.size());

    }


    private void gotoWhere(String aid,String url) {


        String[] urls = url.replace("chrenai://","").split("\\u003F");
        String type = urls[0];
        if("activityDetails".equals(type)){
            startActivity(aid);
        }else if("myRealese".equals(type)){
            String[] paras = urls[1].split("=");
            boolean isFinish;
            if("new".equals(paras[1])){
                isFinish = false;
            }else{
                isFinish = true;
            }
            startActivity(new Intent(AssistantActivity.this, MyActsActivity.class).putExtra("from","mine").putExtra("isFinish",isFinish).putExtra("user",""));
        }else if("applyList".equals(type)){
            startActivity( new Intent(mContext, RegisterActivity.class).putExtra("cid",aid));
        }else if("addApplyList".equals(type)){
            String[] paras = urls[1].split("=");
            startActivity( new Intent(mContext, AddRegisterActivity.class).putExtra("cid",aid).putExtra("status",paras[1]));
        }else if("cancelApplyList".equals(type)){
            startActivity( new Intent(mContext, CanceledRegistActivity.class).putExtra("cid",aid));
        }else if("myActivityFeedbackList".equals(type)){
            startActivity(new Intent(mContext,ActivityFeedbackActivity.class).putExtra("id",aid));
        }else if("Feedback".equals(type)){
            startActivity(new Intent(mContext,ReplyActivity.class).putExtra("id",aid));
        }else if("myParticipation".equals(type)){
            String[] paras = urls[1].split("=");
            boolean isFinish;
            if("new".equals(paras[1])){
                isFinish = false;
            }else{
                isFinish = true;
            }
            startActivity(new Intent(AssistantActivity.this, MyEnjoyActivity.class).putExtra("from","mine").putExtra("isFinish",isFinish).putExtra("user",""));
        }




    }
    private void startActivity(String aid){
        final boolean isLogin=AppPreference.getInstance(mContext).readLogin();
        final String longitude=AppPreference.getInstance(mContext).readLongitude();
        final String latitude=AppPreference.getInstance(mContext).readLatitude();
        final String mToken=AppPreference.getInstance(mContext).readToken();
        Map<String, String> params = new HashMap<>();
        params.put("user_coordinate",longitude+","+latitude);
        if(isLogin){
            params.put("token",mToken);
        }
        params.put("include", "user,category");
        OkHttpUtils.get().url(ApiConstants.GET_ACTIVITIES_API+"/"+aid).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        Type type = new TypeToken<ActivitiesBean>() {
                        }.getType();
                        ActivitiesBean activitiesBean = new Gson().fromJson(response, type);
                        Intent intent = new Intent(mContext, ActivitDetailActivity.class);
                        intent.putExtra("bean",activitiesBean);
                        String url ="";
                        if(isLogin){
                            url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?token=" + mToken+"&user_coordinate="+longitude+","+latitude;
                        }else{
                            url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?user_coordinate="+longitude+","+latitude;
                        }

                        intent.putExtra("url", url);
                        mContext.startActivity(intent);

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }

            }
        });
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

    class MessageAdapter extends  RecyclerView.Adapter<MessageViewHolder>{
        private OnItemClickListener mListener;

        public void setOnItemClickListener(OnItemClickListener li) {
            mListener = li;
        }
        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(AssistantActivity.this).inflate(R.layout.item_assistant,parent,false);
            MessageViewHolder nvh=new MessageViewHolder(v);
            return nvh;
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            final EMMessage message = messages.get(position);
            try {
                holder.title.setText(message.getStringAttribute("title"));
                holder.content.setText(message.getStringAttribute("message"));

            } catch (HyphenateException e) {
                e.printStackTrace();
            }

            if (mListener == null) return;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(message);
                }
            });
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
    class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView content;
        private TextView go;

        public MessageViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            go = (TextView) itemView.findViewById(R.id.go);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(EMMessage message);
    }

}
