package com.yousails.chrenai.publish.bean;

import com.yousails.chrenai.bean.HttpBaseBean;

import java.util.List;

/**
 * Created by sym on 17/7/26.
 */

public class PublishBean extends HttpBaseBean {

    /**
     * id : 38
     * user_id : 1
     * title : 酷兔兔
     * city_id : 1
     * district_code : 110105
     * district : 朝阳区
     * cover_image_id : 234
     * category_id : 9
     * is_global : false
     * started_at : 2017-07-28 12:20:00
     * ended_at : 2017-07-30 12:20:00
     * address : 龙湖长楹天街西区
     * description : 兔兔www控制我<font size="5">腾讯游戏五呀旅游</font>
     * limit : 2000
     * is_chargeable : true
     * sign_type : once
     * record_time : 2
     * application_count : 0
     * additional_application_count : 0
     * participant_count : 0
     * participant_working_hours : 0
     * participant_percent : 0.00
     * view_count : 0
     * share_count : 0
     * amount : 0.00
     * coordinate : 116.605014,39.930108
     * is_finished : false
     * is_top : false
     * is_hot : false
     * application_config : []
     * deleted_reason :
     * created_at : 2017-07-27 13:56:13
     * updated_at : 2017-07-27 13:56:13
     * deleted_at : null
     * cover_image : http://oq6x42vnt.bkt.clouddn.com/uploads/assets/2017/07/1_15010427785109.jpg
     * application_total : 0
     * distance : null
     */

    private int id;
    private int user_id;
    private String title;
    private int city_id;
    private int district_code;
    private String district;
    private String cover_image_id;
    private int category_id;
    private boolean is_global;
    private String started_at;
    private String ended_at;
    private String address;
    private String description;
    private int limit;
    private boolean is_chargeable;
    private String sign_type;
    private int record_time;
    private int application_count;
    private int additional_application_count;
    private int participant_count;
    private int participant_working_hours;
    private String participant_percent;
    private int view_count;
    private int share_count;
    private String amount;
    private String coordinate;
    private boolean is_finished;
    private boolean is_top;
    private boolean is_hot;
    private String deleted_reason;
    private String created_at;
    private String updated_at;
    private Object deleted_at;
    private String cover_image;
    private int application_total;
    private Object distance;
    private List<?> application_config;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getDistrict_code() {
        return district_code;
    }

    public void setDistrict_code(int district_code) {
        this.district_code = district_code;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCover_image_id() {
        return cover_image_id;
    }

    public void setCover_image_id(String cover_image_id) {
        this.cover_image_id = cover_image_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public boolean isIs_global() {
        return is_global;
    }

    public void setIs_global(boolean is_global) {
        this.is_global = is_global;
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

    public boolean isIs_chargeable() {
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

    public int getRecord_time() {
        return record_time;
    }

    public void setRecord_time(int record_time) {
        this.record_time = record_time;
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

    public int getParticipant_count() {
        return participant_count;
    }

    public void setParticipant_count(int participant_count) {
        this.participant_count = participant_count;
    }

    public int getParticipant_working_hours() {
        return participant_working_hours;
    }

    public void setParticipant_working_hours(int participant_working_hours) {
        this.participant_working_hours = participant_working_hours;
    }

    public String getParticipant_percent() {
        return participant_percent;
    }

    public void setParticipant_percent(String participant_percent) {
        this.participant_percent = participant_percent;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public int getShare_count() {
        return share_count;
    }

    public void setShare_count(int share_count) {
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

    public boolean isIs_finished() {
        return is_finished;
    }

    public void setIs_finished(boolean is_finished) {
        this.is_finished = is_finished;
    }

    public boolean isIs_top() {
        return is_top;
    }

    public void setIs_top(boolean is_top) {
        this.is_top = is_top;
    }

    public boolean isIs_hot() {
        return is_hot;
    }

    public void setIs_hot(boolean is_hot) {
        this.is_hot = is_hot;
    }

    public String getDeleted_reason() {
        return deleted_reason;
    }

    public void setDeleted_reason(String deleted_reason) {
        this.deleted_reason = deleted_reason;
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

    public Object getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Object deleted_at) {
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

    public Object getDistance() {
        return distance;
    }

    public void setDistance(Object distance) {
        this.distance = distance;
    }

    public List<?> getApplication_config() {
        return application_config;
    }

    public void setApplication_config(List<?> application_config) {
        this.application_config = application_config;
    }
}
