package com.yousails.chrenai.framework.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;


/**
 * User: jiajinwu
 * Date: 2017-11-03
 * Time: 14:01
 * 修改备注：
 * version:
 */


public class CustomDialogHelper {

    private static CustomDialog customDialog;

    /**
     * 弹出版本更新提示框
     */
    public static CustomDialog showUpdateTipDialog(Context context, String title, String message, String buttonString, DialogInterface.OnClickListener onClickListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setLeftButton(buttonString, onClickListener);
        customDialog = builder.create();
        customDialog.show();
        return customDialog;
    }

    /**
     * 弹出版本更新提示框
     */
    public static CustomDialog showUpdateTipDialog(Context context, String title, String message, String buttonLeftString,String buttonRightString, DialogInterface.OnClickListener onLeftClickListener,DialogInterface.OnClickListener onRightClickListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setLeftButton(buttonLeftString, onLeftClickListener);
        builder.setRightButton(buttonRightString, onRightClickListener);
        customDialog = builder.create();
        customDialog.show();
        return customDialog;
    }


    /**
     * 弹出版本更新进度框
     */
    public static CustomDialog showUpdateProgressDialog(Context context, View contentView) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setContentView(contentView);
        builder.setCanceledOnTouchOutside(false);
        builder.setCancelable(false);
        customDialog = builder.create();
        customDialog.show();
        return customDialog;
    }
}
