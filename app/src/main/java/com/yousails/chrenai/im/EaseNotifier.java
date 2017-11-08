/************************************************************
 *  * Hyphenate CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of Hyphenate Inc.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from Hyphenate Inc.
 */
package com.yousails.chrenai.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseSettingsProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.EasyUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.ui.ActivitDetailActivity;
import com.yousails.chrenai.home.ui.ChatActivity;
import com.yousails.chrenai.person.ui.ActivityFeedbackActivity;
import com.yousails.chrenai.person.ui.AddRegisterActivity;
import com.yousails.chrenai.person.ui.AssistantActivity;
import com.yousails.chrenai.person.ui.CanceledRegistActivity;
import com.yousails.chrenai.person.ui.ConversationListActivity;
import com.yousails.chrenai.person.ui.MyActsActivity;
import com.yousails.chrenai.person.ui.MyEnjoyActivity;
import com.yousails.chrenai.person.ui.RegisterActivity;
import com.yousails.chrenai.person.ui.ReplyActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;

import static com.yousails.chrenai.config.ModelApplication.mContext;

/**
 * new message notifier class
 * 
 * this class is subject to be inherited and implement the relative APIs
 */
public class EaseNotifier {
    private final static String TAG = "notify";
    Ringtone ringtone = null;

    protected final static String[] msg_eng = { "sent a message", "sent a picture", "sent a voice",
                                                "sent location message", "sent a video", "sent a file", "%1 contacts sent %2 messages"
                                              };
    protected final static String[] msg_ch = { "发来一条消息", "发来一张图片", "发来一段语音", "发来位置信息", "发来一个视频", "发来一个文件",
                                               "%1个联系人发来%2条消息"
                                             };

    protected static int notifyID = 0525; // start notification id
    protected static int foregroundNotifyID = 0555;

    protected NotificationManager notificationManager = null;

    protected HashSet<String> fromUsers = new HashSet<String>();
    protected int notificationNum = 0;

    protected Context appContext;
    protected String packageName;
    protected String[] msgs;
    protected long lastNotifiyTime;
    protected AudioManager audioManager;
    protected Vibrator vibrator;
    protected EaseNotificationInfoProvider notificationInfoProvider;

    public EaseNotifier() {
    }
    
    /**
     * this function can be override
     * @param context
     * @return
     */
    public EaseNotifier init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = appContext.getApplicationInfo().packageName;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            msgs = msg_ch;
        } else {
            msgs = msg_eng;
        }

        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        
        return this;
    }
    
    /**
     * this function can be override
     */
    public void reset(){
        resetNotificationCount();
        cancelNotificaton();
    }

    void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }
    
    void cancelNotificaton() {
        if (notificationManager != null)
            notificationManager.cancel(notifyID);
    }

    /**
     * handle the new message
     * this function can be override
     * 
     * @param message
     */
    public synchronized void onNewMsg(EMMessage message) {
        if(EaseCommonUtils.isSilentMessage(message)){
            return;
        }
        EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
        if(!settingsProvider.isMsgNotifyAllowed(message)){
            return;
        }
        
        // check if app running background
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in backgroud");
            sendNotification(message, false);
        } else {
            sendNotification(message, true);

        }
        

    }
    
    public synchronized void onNewMesg(List<EMMessage> messages) {
        if(EaseCommonUtils.isSilentMessage(messages.get(messages.size()-1))){
            return;
        }
        EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
        if(!settingsProvider.isMsgNotifyAllowed(null)){
            return;
        }
        // check if app running background
        if (!EasyUtils.isAppRunningForeground(appContext)) {
            EMLog.d(TAG, "app is running in backgroud");
            sendNotification(messages, false);
        } else {
            sendNotification(messages, true);
        }
        vibrateAndPlayTone(messages.get(messages.size()-1));
    }

    /**
     * send it to notification bar
     * This can be override by subclass to provide customer implementation
     * @param messages
     * @param isForeground
     */
    protected void sendNotification (List<EMMessage> messages, boolean isForeground){
        for(EMMessage message : messages){
            if(!isForeground){
                notificationNum++;
                fromUsers.add(message.getFrom());
            }
        }
        sendNotification(messages.get(messages.size()-1), isForeground, false);
    }
    
    protected void sendNotification (EMMessage message, boolean isForeground){
        sendNotification(message, isForeground, true);
    }
    
    /**
     * send it to notification bar
     * This can be override by subclass to provide customer implementation
     * @param message
     */
    protected void sendNotification(EMMessage message, boolean isForeground, boolean numIncrease) {
        String username = message.getFrom();
        try {
            String notifyText = username + " ";
            
            PackageManager packageManager = appContext.getPackageManager();
            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
            
            // notification title
            String contentTitle = appname;
            if (notificationInfoProvider != null) {
                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
                String customCotentTitle = notificationInfoProvider.getTitle(message);
                if (customNotifyText != null){
                    notifyText = customNotifyText;
                }
                    
                if (customCotentTitle != null){
                    contentTitle = customCotentTitle;
                }   
            }

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                                                                        .setSmallIcon(appContext.getApplicationInfo().icon)
                                                                        .setWhen(System.currentTimeMillis())
                                                                        .setAutoCancel(true);

            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (notificationInfoProvider != null) {
                msgIntent = notificationInfoProvider.getLaunchIntent(message);
            }




            //PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent = null;
            try{
                String title = message.getStringAttribute("title");
                if(TextUtils.isEmpty(title)){
                    pendingIntent = PendingIntent.getActivity(appContext, notifyID, new Intent(appContext, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, message.getFrom()).putExtra("nickname",message.getStringAttribute("nickname","")),PendingIntent.FLAG_UPDATE_CURRENT);

                }else{
                    String sTitle = message.getStringAttribute("title");
                    String sMessage = message.getStringAttribute("message");
                    String sType = message.getStringAttribute("type");
                    String activity_id = message.getStringAttribute("activity_id");
                    String url = message.getStringAttribute("url");
                    Intent intent = gotoWhere(activity_id,url);
                    pendingIntent = PendingIntent.getActivity(appContext, notifyID, intent,PendingIntent.FLAG_UPDATE_CURRENT);

                }
            }catch (Exception e){
                pendingIntent = PendingIntent.getActivity(appContext, notifyID, new Intent(appContext, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, message.getFrom()).putExtra("nickname",message.getStringAttribute("nickname","")),PendingIntent.FLAG_UPDATE_CURRENT);

            }
            
            

            if(numIncrease){
                // prepare latest event info section
                if(!isForeground){
                    notificationNum++;
                    fromUsers.add(message.getFrom());
                }
            }

            /*int fromUsersNum = fromUsers.size();
            String summaryBody = msgs[6].replaceFirst("%1", Integer.toString(fromUsersNum)).replaceFirst("%2",Integer.toString(notificationNum));
            
            if (notificationInfoProvider != null) {
                // lastest text
                String customSummaryBody = notificationInfoProvider.getLatestText(message, fromUsersNum,notificationNum);
                if (customSummaryBody != null){
                    summaryBody = customSummaryBody;
                }
                
                // small icon
                int smallIcon = notificationInfoProvider.getSmallIcon(message);
                if (smallIcon != 0){
                    mBuilder.setSmallIcon(smallIcon);
                }
            }*/

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker(notifyText);
           // mBuilder.setContentText(summaryBody);
            mBuilder.setContentText(message.getStringAttribute("nickname").toString()+"发来一条消息");
            mBuilder.setContentIntent(pendingIntent);
            // mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();

            Log.e("isForeground",isForeground+"");
            if (isForeground) {
                //notificationManager.notify((int) System.currentTimeMillis(), notification);
            } else {
                notificationManager.notify((int) System.currentTimeMillis(), notification);
                vibrateAndPlayTone(message);
            }

        } catch (Exception e) {
            Log.e("exception",e.getMessage());
            e.printStackTrace();
        }
    }


    private Intent gotoWhere(String aid,String url) {
        Intent intent = null;

        String[] urls = url.replace("chrenai://","").split("\\u003F");
        String type = urls[0];
        if("activityDetails".equals(type)){
            intent = activityIntent(aid);
        }else if("myRealese".equals(type)){
            String[] paras = urls[1].split("=");
            boolean isFinish;
            if("new".equals(paras[1])){
                isFinish = false;
            }else{
                isFinish = true;
            }
            intent = new Intent(appContext, MyActsActivity.class).putExtra("from","mine").putExtra("isFinish",isFinish).putExtra("user","");

        }else if("applyList".equals(type)){
            intent= new Intent(mContext, RegisterActivity.class).putExtra("cid",aid);
        }else if("addApplyList".equals(type)){
            String[] paras = urls[1].split("=");
            intent = new Intent(mContext, AddRegisterActivity.class).putExtra("cid",aid).putExtra("status",paras[1]);
        }else if("cancelApplyList".equals(type)){
            intent = new Intent(mContext, CanceledRegistActivity.class).putExtra("cid",aid);
        }else if("myActivityFeedbackList".equals(type)){
            intent = new Intent(mContext,ActivityFeedbackActivity.class).putExtra("id",aid);
        }else if("Feedback".equals(type)){
            intent = new Intent(mContext,ReplyActivity.class).putExtra("id",aid);
        }else if("myParticipation".equals(type)){
            String[] paras = urls[1].split("=");
            boolean isFinish;
            if("new".equals(paras[1])){
                isFinish = false;
            }else{
                isFinish = true;
            }
            intent = new Intent(mContext, MyEnjoyActivity.class).putExtra("from","mine").putExtra("isFinish",isFinish).putExtra("user","");
        }

        return intent;

    }

    private Intent activityIntent(String aid){
        final Intent[] intent = {null};
        final boolean isLogin= AppPreference.getInstance(mContext).readLogin();
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
                         intent[0] = new Intent(mContext, ActivitDetailActivity.class);
                        intent[0].putExtra("bean",activitiesBean);
                        String url ="";
                        if(isLogin){
                            url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?token=" + mToken+"&user_coordinate="+longitude+","+latitude;
                        }else{
                            url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?user_coordinate="+longitude+","+latitude;
                        }

                        intent[0].putExtra("url", url);
                        //mContext.startActivity(intent);

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }

            }
        });
        return intent[0];
    }

    /**
     * vibrate and  play tone
     */
    public void vibrateAndPlayTone(EMMessage message) {
        if(message != null){
            if(EaseCommonUtils.isSilentMessage(message)){
                return;
            } 
        }
        
        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
            // received new messages within 2 seconds, skip play ringtone
            return;
        }
        
        try {
            lastNotifiyTime = System.currentTimeMillis();
            
            // check if in silent mode
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                EMLog.e(TAG, "in slient mode now");
                return;
            }
            EaseSettingsProvider settingsProvider = EaseUI.getInstance().getSettingsProvider();
            if(settingsProvider.isMsgVibrateAllowed(message)){
                long[] pattern = new long[] { 0, 180, 80, 120 };
                vibrator.vibrate(pattern, -1);
            }

            if(settingsProvider.isMsgSoundAllowed(message)){
                if (ringtone == null) {
                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                    if (ringtone == null) {
                        EMLog.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                        return;
                    }
                }
                
                if (!ringtone.isPlaying()) {
                    String vendor = Build.MANUFACTURER;
                    
                    ringtone.play();
                    // for samsung S3, we meet a bug that the phone will
                    // continue ringtone without stop
                    // so add below special handler to stop it after 3s if
                    // needed
                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                        Thread ctlThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        ctlThread.run();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * set notification info Provider
     * 
     * @param provider
     */
    public void setNotificationInfoProvider(EaseNotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface EaseNotificationInfoProvider {
        /**
         * set the notification content, such as "you received a new image from xxx"
         * 
         * @param message
         * @return null-will use the default text
         */
        String getDisplayedText(EMMessage message);

        /**
         * set the notification content: such as "you received 5 message from 2 contacts"
         * 
         * @param message
         * @param fromUsersNum- number of message sender
         * @param messageNum -number of messages
         * @return null-will use the default text
         */
        String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

        /**
         * 设置notification标题
         * 
         * @param message
         * @return null- will use the default text
         */
        String getTitle(EMMessage message);

        /**
         * set the small icon
         * 
         * @param message
         * @return 0- will use the default icon
         */
        int getSmallIcon(EMMessage message);

        /**
         * set the intent when notification is pressed
         * 
         * @param message
         * @return null- will use the default icon
         */
        Intent getLaunchIntent(EMMessage message);
    }
}
