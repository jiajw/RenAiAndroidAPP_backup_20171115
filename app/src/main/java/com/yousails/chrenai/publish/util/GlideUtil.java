package com.yousails.chrenai.publish.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yousails.chrenai.common.LogUtil;

/**
 * Author:WangKunHui
 * Date: 2017/7/21 10:53
 * Desc:
 * E-mail:life_artist@163.com
 */
public class GlideUtil {

    private static String TAG = "GlideUtil";

    public static void loadImage(String imagePath, ImageView imageView) {

        if (TextUtils.isEmpty(imagePath)) {
            return;
        }

        if (imageView == null || imageView.getContext() == null) {
            return;
        }

        Context context = imageView.getContext();

        if (!(context instanceof Activity)) {
            return;
        }

        Activity activity = (Activity) context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                return;
            }
            Glide.with(activity).load(imagePath).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
        } else {
            if (((Activity) context).isFinishing()) {
                return;
            }

            try {
                Glide.with(activity).load(imagePath).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
            } catch (Exception e) {
                LogUtil.e(TAG, "loadImage error : " + e.getMessage());
            }
        }
    }
}
