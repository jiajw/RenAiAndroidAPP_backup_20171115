package com.yousails.chrenai.person.bean;

import com.yousails.chrenai.login.bean.UserBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class RegisterInforBean implements Serializable{
    private String id;
    private String activity_id;
    private String user_id;
    private String operator_id;
    private String is_entrusted;
    private String entrust_title;
    private String amounts;
//    private String[] content;
//    private List<ContentBean> content;
    private String status;
    private boolean is_additional;
    private String description;
    private String cancel_reason;
    private String working_hours;
    private String created_at;
    private String updated_at;
    private UserBean operator;
    private UserBean user;
    private String pay_amount;
    private String refund_amount;


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

    public String getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }

    public String getIs_entrusted() {
        return is_entrusted;
    }

    public void setIs_entrusted(String is_entrusted) {
        this.is_entrusted = is_entrusted;
    }

    public String getEntrust_title() {
        return entrust_title;
    }

    public void setEntrust_title(String entrust_title) {
        this.entrust_title = entrust_title;
    }

    public String getAmounts() {
        return amounts;
    }

    public void setAmounts(String amounts) {
        this.amounts = amounts;
    }

//    public String[] getContent() {
//        return content;
//    }
//
//    public void setContent(String[] content) {
//        this.content = content;
//    }

//    public List<ContentBean> getContent() {
//        return content;
//    }
//
//    public void setContent(List<ContentBean> content) {
//        this.content = content;
//    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
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

    public UserBean getOperator() {
        return operator;
    }

    public void setOperator(UserBean operator) {
        this.operator = operator;
    }

    public String getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(String pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }
}
