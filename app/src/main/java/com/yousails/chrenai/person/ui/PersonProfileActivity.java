package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.BitmapUtil;
import com.yousails.chrenai.common.FileUtils;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.framework.util.GlideUtil;
import com.yousails.chrenai.login.listener.OnPhotoListener;
import com.yousails.chrenai.login.receiver.InternalStorageContentProvider;
import com.yousails.chrenai.login.ui.AuthActivity;
import com.yousails.chrenai.login.ui.DataSelectActivity;
import com.yousails.chrenai.login.ui.SexSelectedActivity;
import com.yousails.chrenai.net.HttpRequestUtil;
import com.yousails.chrenai.net.listener.ModelRequestListener;
import com.yousails.chrenai.publish.bean.ImageUploadEntity;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.LogUtils;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.PermissionUtils;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CircleImageView;
import com.yousails.chrenai.view.PhotoDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/5.
 * 个人资料
 * update date on 2017/10/18
 */

public class PersonProfileActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;

    private RelativeLayout loadingLayout;
    private LinearLayout contentLayout;

    private RelativeLayout headLayout;
    private RelativeLayout nickNameLayout;
    private RelativeLayout sexLayout;
    private RelativeLayout religionLayout;
    private RelativeLayout certifLayout;

    private CircleImageView headView;
    private TextView nameView;
    private TextView sexView;
    private TextView religionView;
    private TextView certifView;
    private TextView stateView;

    private PhotoDialog photoDialog;
    private Uri mImageCaptureUri = null;
    private File mFileTemp;
    //拍照所得图片的名字
    private String upImageName = "camera_image.jpg";
    public static String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private String userId;
    private String phone;
    private String certification;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_personinfor);
    }

    @Override
    protected void init() {
        // 注册对象
        EventBus.getDefault().register(this);
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("个人资料");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);

        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);

        headLayout = (RelativeLayout) findViewById(R.id.head_layout);
        nickNameLayout = (RelativeLayout) findViewById(R.id.nickname_layout);
        sexLayout = (RelativeLayout) findViewById(R.id.sex_layout);
        religionLayout = (RelativeLayout) findViewById(R.id.religion_layout);
        certifLayout = (RelativeLayout) findViewById(R.id.certification_layout);

        headView = (CircleImageView) findViewById(R.id.iv_head);
        nameView = (TextView) findViewById(R.id.nickname_tview);
        sexView = (TextView) findViewById(R.id.sex_tview);
        religionView = (TextView) findViewById(R.id.religion_tview);
        certifView = (TextView) findViewById(R.id.certification_tview);
        stateView = (TextView) findViewById(R.id.iv_state);


        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    upImageName);
        } else {
            mFileTemp = new File(getFilesDir(), upImageName);
        }


        loadingLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        headLayout.setOnClickListener(this);
        nickNameLayout.setOnClickListener(this);
        sexLayout.setOnClickListener(this);
        religionLayout.setOnClickListener(this);
        certifLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.head_layout:
                showDialog();
                break;
            case R.id.nickname_layout:
                Intent intent = new Intent(mContext, NicknameActivity.class);
                intent.putExtra("name", nameView.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.sex_layout:
                Intent sexIntent = new Intent(mContext, SexSelectedActivity.class);
                sexIntent.putExtra("sex", sexView.getText().toString().trim());
                sexIntent.putExtra("from", "person");
                startActivityForResult(sexIntent, 0);
                break;
            case R.id.religion_layout:
                Intent rIntent = new Intent(mContext, DataSelectActivity.class);
                rIntent.putExtra("title", "宗教信仰");
                rIntent.putExtra("type", "religions");
                rIntent.putExtra("from", "person");
                rIntent.putExtra("selected", religionView.getText().toString().trim());
                startActivityForResult(rIntent, 1);
                break;
            case R.id.certification_layout:

                phone = AppPreference.getInstance(mContext).readPhone(userId);

                if (TextUtils.isEmpty(phone)) {
                    Intent bindIntent = new Intent(mContext, BindPhoneActivity.class);
                    startActivity(bindIntent);
                } else {
                    String certif = certifView.getText().toString().trim();
                    if ("未认证".equals(certif)) {
                        Intent authIntent = new Intent(mContext, AuthActivity.class);
                        startActivity(authIntent);
                    }
                }


                break;
        }
    }


    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:

                        CustomToast.createToast(mContext, "更改头像失败");
                        break;
                    case 1:
                        CustomToast.createToast(mContext, "更改头像成功");

                        //更新头像
                        EventBus.getDefault().post("updateavatar");

                        break;
                    case 2:
                        initAuthenView();
                        loadingLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {

        userId = AppPreference.getInstance(mContext).readUerId();
        String userName = AppPreference.getInstance(mContext).readUserName();
        String gender = AppPreference.getInstance(mContext).readGender();
        String headUrl = AppPreference.getInstance(mContext).readAvatar();
        String religion = AppPreference.getInstance(mContext).readReligion();
        phone = AppPreference.getInstance(mContext).readPhone(userId);

        nameView.setText(userName);

        religionView.setText(religion);

        if (StringUtil.isNotNull(gender)) {
            if ("male".equals(gender)) {
                sexView.setText("男");
            } else {
                sexView.setText("女");
            }
        }


        if (StringUtil.isNotNull(headUrl)) {
            Glide.with(mContext).load(headUrl).into(headView);

        } else {
            String sex = AppPreference.getInstance(mContext).readGender();
            if ("male".equals(sex)) {
                headView.setImageResource(R.drawable.ic_avatar);
            } else {
                headView.setImageResource(R.drawable.ic_avatar_woman);
            }

        }

        //获取用户实名认证信息,后期通过推送方式判断
        getAuth();

    }


    /**
     * 初始化实名认证信息
     */
    private void initAuthenView() {
        certification = AppPreference.getInstance(mContext).readCertification();
        if (StringUtil.isNotNull(certification) && "1".equals(certification)) {
            certifView.setText("已认证");
            certifView.setVisibility(View.GONE);
            stateView.setVisibility(View.VISIBLE);
        } else {
            String status = AppPreference.getInstance(mContext).readStatus(userId);
            if (StringUtil.isNotNull(status)) {
                if ("applied".equals(status)) {
                    certifView.setText("审核中");
                } else if ("rejected".equals(status)) {
                    certifView.setText("未认证");
                } else {
                    certifView.setText("已认证");
                }
            } else {
                certifView.setText("未认证");
            }
            certifView.setVisibility(View.VISIBLE);
            stateView.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        if (photoDialog == null) {
            photoDialog = new PhotoDialog(mContext);
            photoDialog.setOnPhotoClickListener(onPhotoListener);
        }

        photoDialog.show();
    }


    OnPhotoListener onPhotoListener = new OnPhotoListener() {
        @Override
        public void openGallery() {

            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, PhotoDialog.REQUEST_CODE_GALLERY);
        }

        @Override
        public void openCamera() {
            if (Build.VERSION.SDK_INT >= 23) {
                PermissionUtils.requestPermission(PersonProfileActivity.this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
            } else {

                startCamera();
            }

        }
    };

    private void startCamera() {
        Log.e(TAG, "cannot take picture");
        String state = Environment.getExternalStorageState();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }

            Log.e(TAG, "cannot take picture==" + mImageCaptureUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent,
                    PhotoDialog.REQUEST_CODE_TAKE_PICTURE);
        } catch (Exception e) {
            Log.e(TAG, "cannot take picture", e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateNickname(String message) {
        if ("updatename".equals(message)) {
            nameView.setText(AppPreference.getInstance(mContext).readUserName());
        }
    }


    /**
     * 剪裁图片
     */
    private void cropPicture(Uri uri) {
        Intent intent = new Intent();

        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);// 输出图片大小
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PhotoDialog.REQUEST_CODE_CROP_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            if (2 == resultCode) {
                String sex = data.getStringExtra("sex");
                sexView.setText(sex);
            } else if (3 == resultCode) {
                String religion = data.getStringExtra("religion");
                religionView.setText(religion);
            }

        }

        if (resultCode != RESULT_OK) {
            return;
        }
        Log.e(TAG, "requestCode==" + requestCode);
        if (requestCode == PhotoDialog.REQUEST_CODE_TAKE_PICTURE) {
            Log.e(TAG, "mImageCaptureUri==" + mImageCaptureUri);
            if (mImageCaptureUri != null) {
                cropPicture(mImageCaptureUri);
            }
        } else if (requestCode == PhotoDialog.REQUEST_CODE_GALLERY) {
            cropPicture(data.getData());
        } else if (requestCode == PhotoDialog.REQUEST_CODE_CROP_IMAGE) {
            Bitmap bitmap = data.getParcelableExtra("data");
            if (bitmap != null && !bitmap.isRecycled()) {
                headView.setImageBitmap(bitmap);

                BitmapUtil.saveBitmapToDisk(FileUtils.getImagePath(mContext) + TEMP_PHOTO_FILE_NAME, bitmap);

                uploadImageFile(FileUtils.getImagePath(mContext) + TEMP_PHOTO_FILE_NAME);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 上传图片
     *
     * @param path
     */
    private void uploadImageFile(String path) {

        HttpRequestUtil.getInstance(ApiConstants.POST_FILE_UPLOAD).setDefaultHeader().upload(path, ImageUploadEntity.class, new ModelRequestListener<ImageUploadEntity>() {
            @Override
            public void onSuccess(ImageUploadEntity imageUploadEntity) {
                LogUtil.i(TAG, "imageUploadEntity : " + imageUploadEntity);
                if (imageUploadEntity == null) {
                    ToastUtils.showShort(mContext, "图片上传失败");
                } else {

                    updateAvatar(imageUploadEntity.getId() + "");
                }
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showShort(mContext, "图片上传失败");
            }
        });
    }


    /**
     * 更改头像
     */
    private void updateAvatar(final String avatarId) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        String token = AppPreference.getInstance(mContext).readToken();

        RequestBody requestBody = new FormBody.Builder().add("avatar", avatarId).build();
        OkHttpUtils.patch().url(ApiConstants.UPDATE_USERINFOR_API).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        AppPreference.getInstance(mContext).writeAvatar(jsonObject.optString("avatar"));

                        mHandler.sendEmptyMessage(1);

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }

            }
        });
    }


    /**
     * 获取实名认证信息
     */
    private void getAuth() {
        boolean isLogin = AppPreference.getInstance(mContext).readLogin();
        if (!isLogin) {
            return;
        }

        if (!NetUtil.detectAvailable(mContext)) {
            //   Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String mToken = AppPreference.getInstance(mContext).readToken();

        OkHttpUtils.get().url(ApiConstants.SUBMIT_CERTIFICATION_API).addHeader("Authorization", mToken).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                mHandler.sendEmptyMessage(2);
            }

            @Override
            public void onResponse(String response, int id) {

                LogUtil.e("==response===" + response);
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONObject reJson = jsonObject.optJSONObject("religion");
                        if (reJson != null) {
                            AppPreference.getInstance(mContext).writeReligion(reJson.optString("name"));
                        }

                        //applied: 已申请, passed: 已通过, rejected: 已拒绝
                        String status = jsonObject.optString("status");
                        if ("passed".equals(status)) {
                            AppPreference.getInstance(mContext).writeCertification("1");
                        } else {
                            AppPreference.getInstance(mContext).writeCertification("");
                        }

                        String userId = AppPreference.getInstance(mContext).readUerId();
                        AppPreference.getInstance(mContext).writeStatus(userId, status);

                        mHandler.sendEmptyMessage(2);

                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(2);
                    }

                }

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            LogUtils.e("==onPermissionGranted==" + requestCode);
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    startCamera();
                    break;

                default:
                    break;
            }
        }
    };
}
