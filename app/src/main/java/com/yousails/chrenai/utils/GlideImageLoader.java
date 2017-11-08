package com.yousails.chrenai.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yousails.chrenai.common.LogUtil;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Administrator on 2017/6/30.
 */

public class GlideImageLoader extends ImageLoader {

    private static String TAG = "GlideImageLoader";

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //Glide 加载图片简单用法
//        Glide.with(context).load(path).override(375,129).into(imageView);
        Glide.with(context).load(path).into(imageView);
    }

    /**
     * 加载图片（防止activity被回收）
     *
     * @param path
     * @param imageView
     */
    public static void displayImage(Object path, ImageView imageView) {

        if (path == null) {
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
            Glide.with(activity).load(path).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
        } else {

            if (((Activity) context).isFinishing()) {
                return;
            }

            try {
                Glide.with(activity).load(path).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
            } catch (Exception e) {
                LogUtil.e(TAG, "loadImage error : " + e.getMessage());
            }
        }
        Glide.with(context).load(path).into(imageView);
    }
}
