package com.yousails.chrenai.publish.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.yousails.chrenai.R;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.publish.adapter.SimpleDialogAdapter;
import com.yousails.chrenai.publish.bean.SimpleDialogItem;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 11:00
 * Desc:
 * E-mail:life_artist@163.com
 */
public class SimpleDialog {

    public static DialogPlus create(Context context, List<SimpleDialogItem> data, OnItemClickListener onItemClickListener) {
        LogUtil.i("DialogPlus create");
        return create(context, data, null, onItemClickListener);
    }

    public static DialogPlus create(Context context, List<SimpleDialogItem> data, View header, OnItemClickListener onItemClickListener) {
        Holder holder = new ListHolder();
        SimpleDialogAdapter adapter = new SimpleDialogAdapter(context, data);

        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(context);

        if (header != null) {
            dialogPlusBuilder.setHeader(header);
        }

        dialogPlusBuilder.setContentHolder(holder)
                .setGravity(Gravity.BOTTOM)
                .setAdapter(adapter)
                .setOnItemClickListener(onItemClickListener)
                .setCancelable(true);

        return dialogPlusBuilder.create();
    }

    public static DialogPlus create(Context context, View view, OnClickListener listener) {
        Holder holder = new ViewHolder(view);

        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(context);
        dialogPlusBuilder.setContentHolder(holder)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(listener)
                .setCancelable(true);
        return dialogPlusBuilder.create();
    }

}
