package com.yousails.chrenai.login.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/22.
 */

public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String realname;
    private String phone;
    private String avatar;//头像
    private String sex;
    private boolean is_certificated;// 是否实名认证
    private boolean is_vip;
    private boolean is_banned;// 禁止; 取缔 、是否被加入黑名单
    private int working_hours;//工作时长
    private int notification_count;//通知数
    private int registration_id;//注册 登记  jpush
    private String wechat_openid;
    private String wechat_unionid;
    private String created_at;
    private String updated_at;
    private String im_username;
    private String level;//用户等级 星级
    private String religion_id;//宗教
    private String religion_name;
    private ActivityStatsBean activity_stats;
    private NotificationsBean notifications;

    //性别
    private String gender;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public String getGender() {

        if (!TextUtils.isEmpty(getSex())) {

            if ("female".equals(getSex())) {
                this.gender = "女";
            } else if ("male".equals(getSex())) {
                this.gender = "男";
            }
        }
        return gender;
    }

    public void setGender(String gender) {

        if (!TextUtils.isEmpty(getSex())) {

            if ("female".equals(getSex())) {
                this.gender = "女";
            } else if ("male".equals(getSex())) {
                this.gender = "男";
            }
            this.gender = gender;
        }

    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean is_certificated() {
        return is_certificated;
    }

    public void setIs_certificated(boolean is_certificated) {
        this.is_certificated = is_certificated;
    }

    public boolean is_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public boolean is_banned() {
        return is_banned;
    }

    public void setIs_banned(boolean is_banned) {
        this.is_banned = is_banned;
    }

    public int getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(int working_hours) {
        this.working_hours = working_hours;
    }

    public int getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(int registration_id) {
        this.registration_id = registration_id;
    }

    public int getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(int notification_count) {
        this.notification_count = notification_count;
    }

    public String getWechat_openid() {
        return wechat_openid;
    }

    public void setWechat_openid(String wechat_openid) {
        this.wechat_openid = wechat_openid;
    }

    public String getWechat_unionid() {
        return wechat_unionid;
    }

    public void setWechat_unionid(String wechat_unionid) {
        this.wechat_unionid = wechat_unionid;
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

    public String getIm_username() {
        return im_username;
    }

    public void setIm_username(String im_username) {
        this.im_username = im_username;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getReligion_id() {
        return religion_id;
    }

    public void setReligion_id(String religion_id) {
        this.religion_id = religion_id;
    }

    public String getReligion_name() {
        return religion_name;
    }

    public void setReligion_name(String religion_name) {
        this.religion_name = religion_name;
    }

    public ActivityStatsBean getActivity_stats() {
        return activity_stats;
    }

    public void setActivity_stats(ActivityStatsBean activity_stats) {
        this.activity_stats = activity_stats;
    }

    public NotificationsBean getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationsBean notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", realname='" + realname + '\'' +
                ", phone='" + phone + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex='" + sex + '\'' +
                ", is_certificated=" + is_certificated +
                ", is_vip=" + is_vip +
                ", is_banned=" + is_banned +
                ", working_hours=" + working_hours +
                ", notification_count=" + notification_count +
                ", registration_id=" + registration_id +
                ", wechat_openid='" + wechat_openid + '\'' +
                ", wechat_unionid='" + wechat_unionid + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", im_username='" + im_username + '\'' +
                ", level='" + level + '\'' +
                ", religion_id='" + religion_id + '\'' +
                ", religion_name='" + religion_name + '\'' +
                ", activity_stats=" + activity_stats +
                ", notifications=" + notifications +
                '}';
    }
}
