package com.yousails.chrenai.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.login.bean.ActivityStatsBean;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.view.BadgeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.yousails.chrenai.config.ModelApplication.mContext;


/**
*@Author :liuwen
*@Date :2017/8/25
*@Des :
*@version :
*/

public class BadgeViewUtil {
    public static BadgeViewUtil.Builder builder(Context context){
        return new BadgeViewUtil.Builder(context);
    }
    public static class Builder {
        private Context mContext;
        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         *@Name :setBadge
         *@Function :根据不同类型显示角标状态
         *@Param :[showType, badgeView]
         *@Return :com.yousails.chrenai.utils.BadgeViewUtil.Builder
         *@Exception :
         *@Author :liuwen
         *@Date :2017/8/25
         */
        public Builder setBadge(BadgeType showType,final BadgeView badgeView){

            if(showType == BadgeType.MainUserTab){

                final int notificationCount = getNotification();
                badgeView.setTextColor(Color.parseColor("#d3321b"));

                getActivity(new ASBCallBack() {
                    @Override
                    public void onResponse(ActivityStatsBean bean) {
                        //if(bean.getParticipated_notification_count()+bean.getParticipated_feedback_unread_count()+bean.getPublished_notification_count()+bean.getPublished_feedback_unread_count()+notificationCount>0){
                            //badgeView.setBadgeCount(activityCount+notificationCount);
                        if(bean.getParticipated_feedback_unread_count()+bean.getPublished_feedback_unread_count()+notificationCount>0){
                            badgeView.setBadgeCount(1);
                        }else{
                            badgeView.setBadgeCount(0);
                        }
                    }
                });
            }else if(showType == BadgeType.UserNotification){
                int notificationCount = getNotification();
                if(notificationCount>0){
                    badgeView.setTextColor(Color.parseColor("#FFFFFF"));
                    badgeView.setBadgeCount(notificationCount);
                }else{
                    badgeView.setBadgeCount(0);
                }

            }
            return this;
        }
        public Builder setMyFragmentBadge( BadgeView badgeView,final BadgeView publishBadge,final BadgeView enjoyBadge){

                int notificationCount = getNotification();
                if(notificationCount>0){
                    badgeView.setTextColor(Color.parseColor("#FFFFFF"));
                    badgeView.setBadgeCount(notificationCount);
                }else{
                    badgeView.setBadgeCount(0);
                }
                getActivity(new ASBCallBack() {
                    @Override
                    public void onResponse(ActivityStatsBean bean) {
                        enjoyBadge.setTextColor(Color.parseColor("#d3321b"));
                        publishBadge.setTextColor(Color.parseColor("#d3321b"));
                        if(bean.getParticipated_feedback_unread_count()>0){
                            enjoyBadge.setBadgeCount(1);

                        }else{
                            enjoyBadge.setBadgeCount(0);

                        }
                        if(bean.getPublished_feedback_unread_count()>0){
                            publishBadge.setBadgeCount(1);
                        }else {
                            publishBadge.setBadgeCount(0);
                        }
                    }
                });

            return this;
        }
        private ActivityStatsBean getActivity(final ASBCallBack callback) {
            final ActivityStatsBean[] bean = {new ActivityStatsBean()};
            final String[] token = {AppPreference.getInstance(mContext).readToken()};
            Map<String, String> params = new HashMap<>();
            params.put("include", "notificaitons,activity_stats");
            OkHttpUtils.get().url(ApiConstants.UPDATE_USERINFOR_API).addHeader("Authorization", token[0]).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, String response, Exception e, int id) {
                }

                @Override
                public void onResponse(String response, int id) {
                    if (StringUtil.isNotNull(response)) {
                        try {
                            Type type = new TypeToken<UserBean>() {
                            }.getType();
                            UserBean user = new Gson().fromJson(response, type);
                            bean[0] = user.getActivity_stats();
                            callback.onResponse(user.getActivity_stats());

                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }

                }
            });
            return bean[0];
        }

        private int getNotification(){
            int count = 0;
            Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
            List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
            synchronized (conversations) {
                for (EMConversation conversation : conversations.values()) {
                    if (conversation.getAllMessages().size() != 0) {
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    }
                }
            }
            try {
                sortConversationByLastChatTime(sortList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Pair<Long, EMConversation> sortItem : sortList) {

                count += sortItem.second.getUnreadMsgCount();
            }
            return count;
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
    }
    public interface ASBCallBack {
        void onResponse(ActivityStatsBean bean);
    }

    public static enum BadgeType {
        MainUserTab,//主页用户tab
        UserNotification,//用户页右上通知
        MyEnjoyEnd,//我的参与完成
        MyPublishEnd,//我的发布完成
        MyPublishFeedback;//我的发布意见反馈

        private BadgeType() {
        }
    }
}
