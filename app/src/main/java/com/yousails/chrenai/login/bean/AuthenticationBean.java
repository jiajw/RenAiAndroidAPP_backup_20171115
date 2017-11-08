package com.yousails.chrenai.login.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/22.
 */

public class AuthenticationBean implements Serializable {
    private int id;
    private String name;
    private String identity_card_no;
    private int user_id;
    private int religion_id;//宗教
    private int degree_id;//学位
    private String work;
    private String skills;//特长
    private String status;
    private String card_front;
    private String card_back;
    private String card_person;
    private String reject_reason;//拒绝; 排斥; 抛弃
    private int admin_id;
    private String created_at;
    private String updated_at;
    private String authenticated_at;
    private String deleted_at;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity_card_no() {
        return identity_card_no;
    }

    public void setIdentity_card_no(String identity_card_no) {
        this.identity_card_no = identity_card_no;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getReligion_id() {
        return religion_id;
    }

    public void setReligion_id(int religion_id) {
        this.religion_id = religion_id;
    }

    public int getDegree_id() {
        return degree_id;
    }

    public void setDegree_id(int degree_id) {
        this.degree_id = degree_id;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCard_front() {
        return card_front;
    }

    public void setCard_front(String card_front) {
        this.card_front = card_front;
    }

    public String getCard_back() {
        return card_back;
    }

    public void setCard_back(String card_back) {
        this.card_back = card_back;
    }

    public String getCard_person() {
        return card_person;
    }

    public void setCard_person(String card_person) {
        this.card_person = card_person;
    }

    public String getReject_reason() {
        return reject_reason;
    }

    public void setReject_reason(String reject_reason) {
        this.reject_reason = reject_reason;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
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

    public String getAuthenticated_at() {
        return authenticated_at;
    }

    public void setAuthenticated_at(String authenticated_at) {
        this.authenticated_at = authenticated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
