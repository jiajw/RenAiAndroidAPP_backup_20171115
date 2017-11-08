package com.yousails.chrenai.login.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/27.
 */

public class LoginBean extends AuthorizationBean {
    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

}
