package com.yousails.chrenai.home.bean;

import com.yousails.chrenai.login.bean.UserBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/18.
 */

public class CommentBean implements Serializable {
    private String id;
    private String root_id;
    private String user_id;
    private String activity_id;
    private String mention_user_id;

    private String vote_count;
    private String reply_count;
    private String content;
    private String deleted_at;
    private String created_at;
    private String updated_at;
    private boolean is_voted;
    private String created_at_diff;

    private UserBean user;
    private UserBean mentionUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoot_id() {
        return root_id;
    }

    public void setRoot_id(String root_id) {
        this.root_id = root_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getMention_user_id() {
        return mention_user_id;
    }

    public void setMention_user_id(String mention_user_id) {
        this.mention_user_id = mention_user_id;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getReply_count() {
        return reply_count;
    }

    public void setReply_count(String reply_count) {
        this.reply_count = reply_count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
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

    public boolean is_voted() {
        return is_voted;
    }

    public void setIs_voted(boolean is_voted) {
        this.is_voted = is_voted;
    }

    public String getCreated_at_diff() {
        return created_at_diff;
    }

    public void setCreated_at_diff(String created_at_diff) {
        this.created_at_diff = created_at_diff;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public UserBean getMentionUser() {
        return mentionUser;
    }

    public void setMentionUser(UserBean mentionUser) {
        this.mentionUser = mentionUser;
    }

}
