package com.yousails.chrenai.login.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/27.
 */

public class RegisterBean extends UserBean {
    private AuthorizationBean authorization;

    public AuthorizationBean getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthorizationBean authorization) {
        this.authorization = authorization;
    }
}
