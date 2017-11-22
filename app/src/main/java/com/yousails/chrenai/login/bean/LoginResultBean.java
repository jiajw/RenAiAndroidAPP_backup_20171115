package com.yousails.chrenai.login.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/18.
 */

public class LoginResultBean implements Serializable {
    private String token;
    private String expired_at;
    private String refresh_expired_at;
    private UserBean user;
    private MetaBean meta;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }

    public String getRefresh_expired_at() {
        return refresh_expired_at;
    }

    public void setRefresh_expired_at(String refresh_expired_at) {
        this.refresh_expired_at = refresh_expired_at;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "LoginResultBean{" +
                "token='" + token + '\'' +
                ", expired_at='" + expired_at + '\'' +
                ", refresh_expired_at='" + refresh_expired_at + '\'' +
                ", user=" + user +
                ", meta=" + meta +
                '}';
    }
}
