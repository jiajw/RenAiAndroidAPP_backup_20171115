package com.yousails.chrenai.login.bean;

import java.io.Serializable;

/**
 * Created by liuwen on 2017/8/25.
 */

public class ActivityStatsBean implements Serializable{
    private int participated_feedback_unread_count;
    private int published_feedback_unread_count;
    private int participated_notification_count;
    private int published_notification_count;

    public int getParticipated_feedback_unread_count() {
        return participated_feedback_unread_count;
    }

    public void setParticipated_feedback_unread_count(int participated_feedback_unread_count) {
        this.participated_feedback_unread_count = participated_feedback_unread_count;
    }

    public int getPublished_feedback_unread_count() {
        return published_feedback_unread_count;
    }

    public void setPublished_feedback_unread_count(int published_feedback_unread_count) {
        this.published_feedback_unread_count = published_feedback_unread_count;
    }

    public int getParticipated_notification_count() {
        return participated_notification_count;
    }

    public void setParticipated_notification_count(int participated_notification_count) {
        this.participated_notification_count = participated_notification_count;
    }

    public int getPublished_notification_count() {
        return published_notification_count;
    }

    public void setPublished_notification_count(int published_notification_count) {
        this.published_notification_count = published_notification_count;
    }
}
