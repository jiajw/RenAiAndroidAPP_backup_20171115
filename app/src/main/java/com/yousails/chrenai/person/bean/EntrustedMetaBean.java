package com.yousails.chrenai.person.bean;

import com.yousails.chrenai.home.bean.PaginationBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/25.
 */

public class EntrustedMetaBean implements Serializable {
    private String entrusted_count;
    private String not_entrusted_count;
    private PaginationBean pagination;

    public String getEntrusted_count() {
        return entrusted_count;
    }

    public void setEntrusted_count(String entrusted_count) {
        this.entrusted_count = entrusted_count;
    }

    public String getNot_entrusted_count() {
        return not_entrusted_count;
    }

    public void setNot_entrusted_count(String not_entrusted_count) {
        this.not_entrusted_count = not_entrusted_count;
    }

    public PaginationBean getPagination() {
        return pagination;
    }

    public void setPagination(PaginationBean pagination) {
        this.pagination = pagination;
    }
}
