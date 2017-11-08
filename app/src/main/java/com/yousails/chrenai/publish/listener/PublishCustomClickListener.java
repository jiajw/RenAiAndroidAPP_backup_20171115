package com.yousails.chrenai.publish.listener;

import android.app.Activity;
import android.view.View;

import com.yousails.chrenai.common.KeyBoardUtil;
import com.yousails.chrenai.publish.ui.PublishActivity;

/**
 * Author:WangKunHui
 * Date: 2017/7/14 11:19
 * Desc: 发布界面的点击事件
 * E-mail:life_artist@163.com
 */
public class PublishCustomClickListener implements View.OnClickListener {

    private Activity activity;

    private PublishCustomCallBack callBack;

    public PublishCustomClickListener(Activity activity, PublishCustomCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
    }

    @Override
    public void onClick(View v) {
        if (PublishActivity.isKeyboardShowing) {
            KeyBoardUtil.closeKeyboard(activity);
            return;
        }

        if (callBack != null) {
            callBack.execute(v);
        }
    }
}
