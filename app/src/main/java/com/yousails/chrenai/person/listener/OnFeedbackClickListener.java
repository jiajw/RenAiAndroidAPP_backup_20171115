package com.yousails.chrenai.person.listener;

import com.yousails.chrenai.person.bean.FeedbackBean;

/**
 * Created by Administrator on 2017/7/7.
 */

public interface OnFeedbackClickListener {
    void onItemClick(FeedbackBean bean);


    void delComment(FeedbackBean bean);//删除
}
