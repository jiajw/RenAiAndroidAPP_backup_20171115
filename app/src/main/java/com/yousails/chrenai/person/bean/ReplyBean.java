package com.yousails.chrenai.person.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwen on 2017/8/29.
 */

public class ReplyBean implements Serializable{

    /**
     * id : 7
     * root_id : 0
     * activity_id : 39
     * user_id : 1
     * reply_count : 2
     * reply_id : 1
     * content : asdfasdfasdf
     * is_read : false
     * created_at : 2017-08-08 19:21:58
     * updated_at : 2017-08-08 19:22:14
     * replies : {"data":[{"id":8,"root_id":7,"activity_id":39,"user_id":1,"reply_count":0,"reply_id":1,"content":"asdfasdfasdf","is_read":false,"created_at":"2017-08-08 19:22:04","updated_at":"2017-08-08 19:22:04","user":{"id":1,"name":"管理员","phone":"13281898731","avatar":"http://chrenai.dev/uploads/assets/avatar_female.png","sex":"female","is_certificated":true,"is_vip":true,"is_banned":false,"working_hours":0,"notification_count":0,"registration_id":0,"wechat_openid":null,"wechat_unionid":null,"created_at":"2017-07-17 10:32:36","updated_at":"2017-08-01 23:31:53","im_username":"chrenai_1","level":"0-0-0"}},{"id":9,"root_id":7,"activity_id":39,"user_id":1,"reply_count":0,"reply_id":1,"content":"asdfasdfasdf","is_read":false,"created_at":"2017-08-08 19:22:14","updated_at":"2017-08-08 19:22:14","user":{"id":1,"name":"管理员","phone":"13281898731","avatar":"http://chrenai.dev/uploads/assets/avatar_female.png","sex":"female","is_certificated":true,"is_vip":true,"is_banned":false,"working_hours":0,"notification_count":0,"registration_id":0,"wechat_openid":null,"wechat_unionid":null,"created_at":"2017-07-17 10:32:36","updated_at":"2017-08-01 23:31:53","im_username":"chrenai_1","level":"0-0-0"}}]}
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
    private RepliesBean replies;

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

    public RepliesBean getReplies() {
        return replies;
    }

    public void setReplies(RepliesBean replies) {
        this.replies = replies;
    }

    public static class RepliesBean {
        private List<FeedbackBean> data;

        public List<FeedbackBean> getData() {
            return data;
        }

        public void setData(List<FeedbackBean> data) {
            this.data = data;
        }

    }
}
