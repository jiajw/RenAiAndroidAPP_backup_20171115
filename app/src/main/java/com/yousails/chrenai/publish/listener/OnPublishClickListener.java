package com.yousails.chrenai.publish.listener;

import android.view.View;

import com.yousails.chrenai.home.bean.ActivitiesBean;

/**
 * Author:WangKunHui
 * Date: 2017/7/14 11:26
 * Desc:
 * E-mail:life_artist@163.com
 */
public interface OnPublishClickListener {

    void onItemClick(ActivitiesBean activitiesBean);

    //编辑
    void editor(ActivitiesBean activitiesBean);

    //委托管理
    void entrustManage(ActivitiesBean activitiesBean);

    //报名、签到、缴费
    void doMore(ActivitiesBean activitiesBean);
}
