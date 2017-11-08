package com.yousails.chrenai.publish.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.yousails.chrenai.R;
import com.yousails.chrenai.common.FileUtils;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.publish.bean.SimpleDialogItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sym on 17/7/22.
 */

public class PhotoHelper {

    //申请相册权限code
    public final int permission_album = 1;

    //申请摄像头权限（包含文件权限）code
    public final int permission_camera = 2;

    //拍照所得图片的名字
    private String upImageName = "camera_image.jpg";

    /**
     * 打开相册的requestCode
     */
    public static final int requestCodeAlbum = 101;

    /**
     * 打开摄像头的requestCode
     */
    public static final int requestCodeCamera = 102;

    /**
     * 选择图片的dialog
     */
    private DialogPlus albumChoiceDialog;

    private static final PhotoHelper ourInstance = new PhotoHelper();

    public static PhotoHelper getInstance() {
        return ourInstance;
    }

    private PhotoHelper() {
    }

    public void showAlbumChoicePop(Activity activity, View contentView, View achor) {

        PopupWindow pop = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setOutsideTouchable(true);
        //让pop可以点击外面消失掉
        pop.setBackgroundDrawable(new ColorDrawable(0));
        pop.showAtLocation(achor, Gravity.BOTTOM, 0, 0);
    }


    //图片选择框
    public void showAlbumChoiceDialog(final Activity context) {

        if (albumChoiceDialog == null) {

            List<SimpleDialogItem> data = new ArrayList<>();
            data.add(new SimpleDialogItem("从相册选择", R.color.publish_picture_choice_text_color_blue));
            data.add(new SimpleDialogItem("拍照", R.color.publish_picture_choice_text_color_blue));
            data.add(new SimpleDialogItem("取消", R.color.publish_picture_choice_text_color_gray));

            albumChoiceDialog = SimpleDialog.create(context, data, new OnItemClickListener() {
                @Override
                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    albumChoiceDialog.dismiss();
                    switch (position) {
                        case 0:
                            openAlbum(context);
                            break;
                        case 1:
                            openCamera(context);
                            break;
                    }
                }
            });
        }
        albumChoiceDialog.show();
    }

    /**
     * 鉴权打开系统相册
     */
    public void openAlbum(Activity activity) {

        //文件读写权限Check
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            List permissions = new ArrayList();
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            executeOpenAlbum(activity);
        }
    }


    /**
     * 鉴权打开摄像头方法
     */
    public void openCamera(Activity activity) {

        List permissions = new ArrayList();
        //文件读写权限Check
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        //调用摄像头权限Check
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        //两个权限都有  不需要在申请
        if (permissions.size() == 0) {
            executeOpenCamera(activity);
        }
        //至少需要申请一个权限
        else {
            applyPermission(activity, permission_camera, permissions);
        }
    }

    /**
     * 执行打开摄像头的方法
     */
    private void executeOpenCamera(Activity activity) {
        try {
            Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri(activity));
            intentFromCamera.putExtra("return-data", true);
            activity.startActivityForResult(intentFromCamera, requestCodeCamera);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(activity, "需要打开使用照相机权限才能正常使用");
        }
    }

    /**
     * 获取拍照后的图片的uri
     *
     * @return
     */
    public Uri getImageUri(Activity activity) {
        return Uri.fromFile(new File(FileUtils.getImagePath(activity), upImageName));
    }

    /**
     * 申请运行时权限
     *
     * @param code        申请码
     * @param permissions 所需要申请的权限
     */
    private void applyPermission(Activity activity, int code, List permissions) {
        if (permissions != null && permissions.size() > 0) {
            String[] permissionArray = (String[]) permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(activity, permissionArray, code);
        }
    }


    /**
     * 判断是否有可以接受的Activity
     *
     * @param context
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) return false;
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 执行打开系统相册的方法
     */
    private void executeOpenAlbum(Activity activity) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT; // 4.4
        Intent pictureIntent;
        if (isKitKat) {
            pictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (!isIntentAvailable(activity.getApplicationContext(), pictureIntent)) {
                ToastUtils.showShort(activity, "很抱歉，当前您的手机不支持相册选择功能，请安装相册软件");
                return;
            }
        } else {
            pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pictureIntent.setType("image/*");
            if (pictureIntent.resolveActivity(activity.getPackageManager()) == null) {
                ToastUtils.showShort(activity, "很抱歉，当前您的手机不支持相册选择功能，请安装相册软件");
                return;
            }
        }
        try {
            activity.startActivityForResult(pictureIntent, requestCodeAlbum);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
