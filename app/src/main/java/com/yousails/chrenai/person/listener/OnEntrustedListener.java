package com.yousails.chrenai.person.listener;

import com.yousails.chrenai.person.bean.RegisterInforBean;

/**
 * Created by Administrator on 2017/8/24.
 */

public interface OnEntrustedListener {

    //委托管理  type:  entrust/editor/cancel
    void onEntrusted(RegisterInforBean registerInforBean, String type);

}
