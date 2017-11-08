package com.yousails.chrenai.net.listener;

import com.yousails.chrenai.bean.HttpBaseBean;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 11:46
 * Desc: OkHttpCommUtils doModelRequest回调
 * E-mail:life_artist@163.com
 */
public interface ModelRequestListener<T extends HttpBaseBean> {

    //成功回调 返回解析后的对象
    void onSuccess(T t);

    //失败回调 简单返回错误信息 需要可扩展
    void onFailure(String message);

}
