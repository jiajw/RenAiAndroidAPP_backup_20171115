package com.yousails.chrenai.net.listener;

/**
 * Author:WangKunHui
 * Date: 2017/7/19 15:11
 * Desc:
 * E-mail:life_artist@163.com
 */
public interface StringRequestListener {

    //成功回调 返回解析后的对象
    void onSuccess(String result);

    //失败回调 简单返回错误信息 需要可扩展
    void onFailure(String message);

}
