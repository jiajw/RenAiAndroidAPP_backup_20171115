package com.yousails.chrenai.im;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.yousails.chrenai.app.ui.ActivityManager;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.bean.EMUser;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;
import com.yousails.chrenai.login.ui.LoginActivity;
import com.yousails.chrenai.view.DropDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yousails.chrenai.config.ModelApplication.mContext;
import static com.yousails.chrenai.thread.ThreadHelper.runOnUiThread;

/**
 * Created by Administrator on 2017/8/2.
 */

public class DemoHelper {

    /**
     * data sync listener
     */
    public interface DataSyncListener {
        /**
         * sync complete
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";

    private EaseUI easeUI;

    private EaseNotifier notifier = null;
    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;

    private static Map<String, EaseUser> contactList;


    private UserProfileManager userProManager;

    private static DemoHelper instance = null;



    private String username;

    private Context appContext;

    private DemoHelper() {
    }

    public synchronized static DemoHelper getInstance() {
        if (instance == null) {
            instance = new DemoHelper();
        }
        if(contactList == null) {
            contactList = new HashMap<>();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context
     *            application context
     */
    public void init(Context context) {
        EMOptions options = initChatOptions();

        //use default options if options is null
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;

            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            notifier = new EaseNotifier();
            notifier.init(appContext);
            registerConnectionListener();
            registerMessageListener();
            //initialize preference manager
//            PreferenceManager.init(context);
            //initialize profile manager
//            getUserProfileManager().init(context);
            //set Call options
//            setCallOptions();

        }
    }


    private EMOptions initChatOptions(){
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        //you need apply & set your own id if you want to use google cloud messaging.
        options.setGCMNumber("324169311137");
        //you need apply & set your own id if you want to use Mi push notification
        options.setMipushConfig("2882303761517426801", "5381742660801");
        //you need apply & set your own id if you want to use Huawei push notification
        options.setHuaweiPushAppId("10492024");



        return options;
    }

    public EaseNotifier getNotifier(){
        return notifier;
    }
    private void registerConnectionListener() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                Log.e("login.onConnected","ok");

            }

            @Override
            public void onDisconnected(final int error) {
                Log.e("login.onDisconnected","error:"+error);
                final DropDialog[] dropDialog = new DropDialog[1];
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(error == EMError.USER_REMOVED){
                            // 显示帐号已经被移除
                        }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                            // 显示帐号在其他设备登录
                            clearUserInfor();
                            final Activity taskTop = ActivityManager.getScreenManager().currentActivity();
                            Log.e("tasktop",taskTop.getLocalClassName());
                            if (taskTop == null) return;

                            dropDialog[0] = new DropDialog(taskTop, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dropDialog[0].dismiss();
                                    ActivityManager.getScreenManager().popAllActivityExceptPackageName("app.ui.XXMainActivity");
                                    //ActivityManager.getScreenManager().popAllActivity();
                                    Intent intent=new Intent(appContext,LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    appContext.startActivity(intent);
                                }
                            });
                            dropDialog[0].setCancelable(false);
                            dropDialog[0].show();

                            /*new AlertDialog.Builder(taskTop)
                                    .setTitle("提示")
                                    .setMessage("您的帐号已在别处登录")
                                    .setPositiveButton("确认", new     DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            ActivityManager.getScreenManager().popAllActivityExceptPackageName("app.ui.XXMainActivity");
                                            //ActivityManager.getScreenManager().popAllActivity();
                                            Intent intent=new Intent(appContext,LoginActivity.class);
                                            appContext.startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("", null)
                                    .setCancelable(false)
                                    .show();
                            */

                        } else {
                            /*if (NetUtils.hasNetwork(appContext))
                            //连接不到聊天服务器
                            else*/
                            //当前网络不可用，请检查网络设置
                        }
                    }
                });
            }
        });
    }
    /**
     * 清空用户登录信息
     */
    private void clearUserInfor(){
        //保存到sharepreference
        String userId= AppPreference.getInstance(appContext).readUerId();
        AppPreference.getInstance(appContext).writePhone(userId,"");

        AppPreference.getInstance(appContext).writeUserId("");
        AppPreference.getInstance(appContext).writeLastUserId("");
        AppPreference.getInstance(appContext).writeUserName("");
        AppPreference.getInstance(appContext).writeAvatar("");
        AppPreference.getInstance(appContext).writeGender("male");
        AppPreference.getInstance(appContext).writeReligion("");
        AppPreference.getInstance(appContext).writeIsVip("");
        AppPreference.getInstance(appContext).writeCertification("");
        AppPreference.getInstance(appContext).writeWorkHours("");
        AppPreference.getInstance(appContext).writeLevel("");
        AppPreference.getInstance(appContext).writeEMName("");
        AppPreference.getInstance(appContext).writeEMPwd("");

        AppPreference.getInstance(appContext).setLogin(false);
        AppPreference.getInstance(appContext).setLogout(true);

        AppPreference.getInstance(appContext).writeToken("");
        AppPreference.getInstance(appContext).writeExpired("");
        AppPreference.getInstance(appContext).writeRefreshExpired("");

        AppPreference.getInstance(appContext).writeWXToken("");
        AppPreference.getInstance(appContext).writeWXOpenId("");
        AppPreference.getInstance(appContext).writeWXRefreshToken("");
    }
    private void registerMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                EaseAtMessageHelper.get().parseMessages(messages);
                for (EMMessage message : messages) {
                    Log.e("username",message.getStringAttribute("username",""));
                    Log.e("avatar",message.getStringAttribute("avatar",""));
                    Log.e("nickname",message.getStringAttribute("nickname",""));
                    String username = null;
                    // group message
                    if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                        username = message.getTo();
                    } else {
                        // single chat message
                        username = message.getFrom();
                    }

                    // if the message is for current conversation

                    notifier.onNewMsg(message);

                }
            }
            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }
            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
            }
            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }
        });
    }

    protected void setEaseUIProviders() {
        //set user avatar to circle shape
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(1);
        easeUI.setAvatarOptions(avatarOptions);

        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

    }

    public void addContact(EMUser user){
        EaseUser eu = new EaseUser(user.getIm_username());
        eu.setAvatar(user.getAvatar());
        eu.setNickname(user.getName());
        contactList.put(user.getIm_username(),eu);
    }

    public EaseUser getContact(String username){
        return contactList.get(username);
    }

    private EaseUser getUserInfo(String username){
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;

        if(username.equals(EMClient.getInstance().getCurrentUser()))
        user= getUserProfileManager().getCurrentUserInfo();

        if(contactList.get(username)!=null){
            user = contactList.get(username);
        }
        // if user is not in your contacts, set inital letter for him/her
        if(user == null){
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }
}
