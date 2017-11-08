package com.yousails.chrenai.person.bean;

import com.yousails.chrenai.login.bean.UserBean;

import java.io.Serializable;

/**
 * Created by liuwen on 2017/8/29.
 */

public class FeedbackBean implements Serializable{

    /**
     * id : 11
     * root_id : 0
     * activity_id : 39
     * user_id : 1
     * reply_count : 0
     * reply_id : 1
     * content : asdfasdfasdf
     * is_read : false
     * created_at : 2017-08-08 20:49:06
     * updated_at : 2017-08-08 20:49:06
     */

    private int id;
    private int root_id;
    private int activity_id;
    private int user_id;
    private int reply_count;
    private int reply_id;
    private String content;
    private boolean is_read;
    private String created_at;
    private String updated_at;
    private UserBean user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoot_id() {
        return root_id;
    }

    public void setRoot_id(int root_id) {
        this.root_id = root_id;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getReply_count() {
        return reply_count;
    }

    public void setReply_count(int reply_count) {
        this.reply_count = reply_count;
    }

    public int getReply_id() {
        return reply_id;
    }

    public void setReply_id(int reply_id) {
        this.reply_id = reply_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
