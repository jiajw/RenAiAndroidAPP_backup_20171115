package com.yousails.chrenai.home.bean;

import com.bigkoo.pickerview.model.IPickerViewData;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.person.bean.AuthorityBean;
import com.yousails.chrenai.person.bean.UserPermsBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/5.
 */

public class ActivitiesBean implements Serializable,IPickerViewData {
    private String id;
    private String user_id;
    private String title;
    private String city_id;
    private String district_code;
    private String cover_image_id;
    private String category_id;
    private String started_at;
    private String ended_at;
    private String address;
    private String description;
    private int limit;
    private boolean is_chargeable;
    private String sign_type;
    private int application_count;
    private int additional_application_count;
    private String participant_count;
    private String participant_working_hours;
    private String participant_percent;
    private String view_count;
    private String share_count;
    private String amount;
    private String coordinate;
    private boolean is_started;
    private boolean is_finished;
    private boolean is_top;
    private boolean is_hot;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private String cover_image;
    private int application_total;
    private String district;
    private String distance;
    private List<AppConfigBean>application_config;
    private CategoryBean category;
    private UserBean user;
    private EnrollBean userApplication;
    private UserPermsBean userPerms;

    private String address_description;
    private String record_time;
    private boolean is_global;

    private int unread_feedback_count;
    private int notification_count;

    public int getUnread_feedback_count() {
        return unread_feedback_count;
    }

    public void setUnread_feedback_count(int unread_feedback_count) {
        this.unread_feedback_count = unread_feedback_count;
    }

    public int getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(int notification_count) {
        this.notification_count = notification_count;
    }

    public boolean is_global() {
        return is_global;
    }

    public void setIs_global(boolean is_global) {
        this.is_global = is_global;
    }

    public String getAddress_description() {
        return address_description;
    }

    public void setAddress_description(String address_description) {
        this.address_description = address_description;
    }

    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getDistrict_code() {
        return district_code;
    }

    public void setDistrict_code(String district_code) {
        this.district_code = district_code;
    }

    public String getCover_image_id() {
        return cover_image_id;
    }

    public void setCover_image_id(String cover_image_id) {
        this.cover_image_id = cover_image_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getEnded_at() {
        return ended_at;
    }

    public void setEnded_at(String ended_at) {
        this.ended_at = ended_at;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean is_chargeable() {
        return is_chargeable;
    }

    public void setIs_chargeable(boolean is_chargeable) {
        this.is_chargeable = is_chargeable;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public int getApplication_count() {
        return application_count;
    }

    public void setApplication_count(int application_count) {
        this.application_count = application_count;
    }

    public int getAdditional_application_count() {
        return additional_application_count;
    }

    public void setAdditional_application_count(int additional_application_count) {
        this.additional_application_count = additional_application_count;
    }

    public String getParticipant_count() {
        return participant_count;
    }

    public void setParticipant_count(String participant_count) {
        this.participant_count = participant_count;
    }

    public String getParticipant_working_hours() {
        return participant_working_hours;
    }

    public void setParticipant_working_hours(String participant_working_hours) {
        this.participant_working_hours = participant_working_hours;
    }

    public String getParticipant_percent() {
        return participant_percent;
    }

    public void setParticipant_percent(String participant_percent) {
        this.participant_percent = participant_percent;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getShare_count() {
        return share_count;
    }

    public void setShare_count(String share_count) {
        this.share_count = share_count;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public boolean is_started() {
        return is_started;
    }

    public void setIs_started(boolean is_started) {
        this.is_started = is_started;
    }

    public boolean is_finished() {
        return is_finished;
    }

    public void setIs_finished(boolean is_finished) {
        this.is_finished = is_finished;
    }

    public boolean is_top() {
        return is_top;
    }

    public void setIs_top(boolean is_top) {
        this.is_top = is_top;
    }

    public boolean is_hot() {
        return is_hot;
    }

    public void setIs_hot(boolean is_hot) {
        this.is_hot = is_hot;
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

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public int getApplication_total() {
        return application_total;
    }

    public void setApplication_total(int application_total) {
        this.application_total = application_total;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<AppConfigBean> getApplication_config() {
        return application_config;
    }

    public void setApplication_config(List<AppConfigBean> application_config) {
        this.application_config = application_config;
    }

    public CategoryBean getCategory() {
        return category;
    }

    public void setCategory(CategoryBean category) {
        this.category = category;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public EnrollBean getUserApplication() {
        return userApplication;
    }

    public void setUserApplication(EnrollBean userApplication) {
        this.userApplication = userApplication;
    }

    @Override
    public String getPickerViewText() {
        return getTitle();
    }

    public UserPermsBean getUserPerms() {
        return userPerms;
    }

    public void setUserPerms(UserPermsBean userPerms) {
        this.userPerms = userPerms;
    }
}
