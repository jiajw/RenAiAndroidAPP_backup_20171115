package com.yousails.chrenai.person.listener;

import com.yousails.chrenai.person.bean.RegisterInforBean;

/**
 * Created by Administrator on 2017/8/24.
 */

public interface OnPaymentListener {

    //允许报名
    void onPayment(RegisterInforBean registerInforBean,String type);

}
