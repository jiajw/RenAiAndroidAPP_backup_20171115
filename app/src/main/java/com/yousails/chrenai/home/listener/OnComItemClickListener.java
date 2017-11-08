package com.yousails.chrenai.home.listener;

import com.yousails.chrenai.home.bean.CommentBean;

/**
 * Created by Administrator on 2017/7/7.
 */

public interface OnComItemClickListener {
    void onItemClick(CommentBean commentBean);

    void doVote(CommentBean commentBean);//点赞

    void delComment(CommentBean commentBean);//删除评论
}
