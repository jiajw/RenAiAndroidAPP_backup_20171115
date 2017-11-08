package com.yousails.chrenai.person.bean;

import com.yousails.chrenai.home.bean.PaginationBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/25.
 */

public class PaymentMetaBean implements Serializable {
    private String pay_payments_count;
    private String refound_payments_count;
    private String no_payments_count;
    private String amount;
    private PaginationBean pagination;

    public String getPay_payments_count() {
        return pay_payments_count;
    }

    public void setPay_payments_count(String pay_payments_count) {
        this.pay_payments_count = pay_payments_count;
    }

    public String getRefound_payments_count() {
        return refound_payments_count;
    }

    public void setRefound_payments_count(String refound_payments_count) {
        this.refound_payments_count = refound_payments_count;
    }

    public String getNo_payments_count() {
        return no_payments_count;
    }

    public void setNo_payments_count(String no_payments_count) {
        this.no_payments_count = no_payments_count;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public PaginationBean getPagination() {
        return pagination;
    }

    public void setPagination(PaginationBean pagination) {
        this.pagination = pagination;
    }
}
