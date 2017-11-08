package com.yousails.chrenai.home.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 * 用户报名数据
 */

public class EnrollBean implements Serializable {
    private String id;
    private String activity_id;
    private String user_id;
    private String name;
    private String phone;
    private String operator_id;
    private boolean is_entrusted;
    private String entrust_title;
    private String amount;
    private List<AppConfigBean> content;
    private String status;// 报名状态
    private boolean is_additional;// 是否是追加报名

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }

    public boolean is_entrusted() {
        return is_entrusted;
    }

    public void setIs_entrusted(boolean is_entrusted) {
        this.is_entrusted = is_entrusted;
    }

    public String getEntrust_title() {
        return entrust_title;
    }

    public void setEntrust_title(String entrust_title) {
        this.entrust_title = entrust_title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<AppConfigBean> getContent() {
        return content;
    }

    public void setContent(List<AppConfigBean> content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean is_additional() {
        return is_additional;
    }

    public void setIs_additional(boolean is_additional) {
        this.is_additional = is_additional;
    }
}
