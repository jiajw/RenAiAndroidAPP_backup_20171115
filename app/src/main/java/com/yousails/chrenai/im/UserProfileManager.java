package com.yousails.chrenai.im;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;

/**
 * Created by Administrator on 2017/8/7.
 */

public class UserProfileManager {
    /**
     * application context
     */
    protected Context mContext = null;

    private EaseUser currentUser;

    public UserProfileManager() {
    }

    public synchronized EaseUser getCurrentUserInfo() {
        if (currentUser == null) {
            String username = EMClient.getInstance().getCurrentUser();
            currentUser = new EaseUser(username);
            String nick = getCurrentUserNick();
            currentUser.setNick((nick != null) ? nick : username);
            currentUser.setAvatar(getCurrentUserAvatar());
        }
        return currentUser;
    }

    private String getCurrentUserNick() {
//        return PreferenceManager.getInstance().getCurrentUserNick();

        return AppPreference.getInstance(ModelApplication.mContext).readUserName();
    }

    private String getCurrentUserAvatar() {
//        return PreferenceManager.getInstance().getCurrentUserAvatar();

        return AppPreference.getInstance(ModelApplication.mContext).readAvatar();
    }

}
