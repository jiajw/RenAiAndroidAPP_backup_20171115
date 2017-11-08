package com.yousails.chrenai.home.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/24.
 */

public class MetaBean implements Serializable{
    private String passed_count;
    private String deleted_count;
    private String rejected_count;
    private String applied_count;

    private PaginationBean pagination;

    public String getPassed_count() {
        return passed_count;
    }

    public void setPassed_count(String passed_count) {
        this.passed_count = passed_count;
    }

    public String getDeleted_count() {
        return deleted_count;
    }

    public void setDeleted_count(String deleted_count) {
        this.deleted_count = deleted_count;
    }

    public String getRejected_count() {
        return rejected_count;
    }

    public void setRejected_count(String rejected_count) {
        this.rejected_count = rejected_count;
    }

    public String getApplied_count() {
        return applied_count;
    }

    public void setApplied_count(String applied_count) {
        this.applied_count = applied_count;
    }

    public PaginationBean getPagination() {
        return pagination;
    }

    public void setPagination(PaginationBean pagination) {
        this.pagination = pagination;
    }
}
