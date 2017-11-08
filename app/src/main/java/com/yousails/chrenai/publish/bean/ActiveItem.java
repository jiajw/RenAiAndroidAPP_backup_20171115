package com.yousails.chrenai.publish.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/19 10:43
 * Desc: 活动对象
 * E-mail:life_artist@163.com
 */
public class ActiveItem implements Serializable {

    /**
     * title : 施粥活动
     * category_id : 1
     * cover_image_id : 1
     * started_at : 2017-07-01 12:00:00
     * ended_at : 2017-07-01 18:00:00
     * coordinate : 76.015919,39.47232
     * address : 北京市海淀区中关村大街27号1101-08室
     * description : 值此端午佳节来临之际...
     * limit : 10
     * is_chargeable : true
     * sign_type : twice
     * is_global : true
     * record_time : 200
     * application_config : [{"title":"性别","type":"radio","options":["男","女"],"required":true},{"title":"姓名","type":"text"}]
     */

    private String title;
    private int category_id;
    private int cover_image_id;
    private String started_at;
    private String ended_at;
    private String coordinate;
    private String address;
    private String address_description;

    public String getAddress_description() {
        return address_description;
    }

    public void setAddress_description(String address_description) {
        this.address_description = address_description;
    }


    private String description;
    private int limit;
    private Boolean is_chargeable;
    private String sign_type;
    private boolean is_global;
    private String record_time;

    private List<ActiveApplyItem> application_config;

    private ImageUploadEntity imageUploadEntity;

    private Category category;


    public boolean is_global() {
        return is_global;
    }

    public void setIs_global(boolean is_global) {
        this.is_global = is_global;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getCover_image_id() {
        return cover_image_id;
    }

    public void setCover_image_id(int cover_image_id) {
        this.cover_image_id = cover_image_id;
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

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
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

    public Boolean isIs_chargeable() {
        return is_chargeable;
    }

    public void setIs_chargeable(Boolean is_chargeable) {
        this.is_chargeable = is_chargeable;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }


    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }

    public List<ActiveApplyItem> getApplication_config() {
        return application_config;
    }

    public void setApplication_config(List<ActiveApplyItem> application_config) {
        this.application_config = application_config;
    }

    public ImageUploadEntity getImageUploadEntity() {
        return imageUploadEntity;
    }

    public void setImageUploadEntity(ImageUploadEntity imageUploadEntity) {
        this.imageUploadEntity = imageUploadEntity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
