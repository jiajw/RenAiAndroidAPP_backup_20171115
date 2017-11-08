package com.yousails.chrenai.person.listener;

import com.yousails.chrenai.person.bean.RegisterInforBean;

/**
 * Created by Administrator on 2017/8/24.
 */

public interface OnEnrollListener {

    //允许报名
    void onPassed( RegisterInforBean registerInforBean );
   //拒绝报名
    void onRejected( RegisterInforBean registerInforBean );

    void OnLongClick(RegisterInforBean registerInforBean);

    void OnItemClick(RegisterInforBean registerInforBean);
}
