package com.yousails.chrenai.login.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.PathUtil;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.BitmapUtil;
import com.yousails.chrenai.common.FileUtils;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.ui.EnrollActivity;
import com.yousails.chrenai.home.ui.ProtocolActivity;
import com.yousails.chrenai.login.listener.OnPhotoListener;
import com.yousails.chrenai.login.receiver.InternalStorageContentProvider;
import com.yousails.chrenai.net.HttpRequestUtil;
import com.yousails.chrenai.net.listener.ModelRequestListener;
import com.yousails.chrenai.publish.bean.ImageUploadEntity;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CancelCertifDialog;
import com.yousails.chrenai.view.PhotoDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 上传证件照
 * Created by Administrator on 2017/8/8.
 */

public class CardActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private ImageView frontView;
    private ImageView backView;
    private ImageView handView;

    private TextView frontTextView;
    private TextView backTextView;
    private TextView handTextView;

    private  Button nextBtn;
    private CheckBox checkBox;
    private RelativeLayout protocolLayout;

    private PhotoDialog photoDialog;
    private File mFileTemp;
    public static String FRONT_PHOTO_FILE_NAME = "front_photo.jpg";
    public static String BACK_PHOTO_FILE_NAME = "back_photo.jpg";
    public static String HAND_PHOTO_FILE_NAME = "hand_photo.jpg";
    public static String TEMP_PHOTO_FILE_NAME ="temp_photo.jpg";
    private static String CROP_PHOTO_FILE_NAME ="crop_photo.jpg";

    //拍照所得图片的名字
    private String upImageName = "camera_image.jpg";
    private String type;
    private HashMap<String,String> paramMap=new HashMap<String,String>();
    private HashMap<String,String> photoMap=new HashMap<String,String>();
    private String[] strArray={"card_front","card_back","card_person"};
    private Uri mImageCaptureUri = null;
    private CancelCertifDialog cancelCertifDialog;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_card);
    }

    @Override
    protected void init() {
        paramMap=(HashMap<String,String>)getIntent().getSerializableExtra("map");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("实名认证");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.GONE);

        frontView=(ImageView)findViewById(R.id.front_view);
        backView=(ImageView)findViewById(R.id.back_view);
        handView=(ImageView)findViewById(R.id.hand_view);

        frontTextView=(TextView)findViewById(R.id.front_tview);
        backTextView=(TextView)findViewById(R.id.back_tview);
        handTextView=(TextView)findViewById(R.id.hand_tview);


        checkBox=(CheckBox)findViewById(R.id.id_checkbox);
        protocolLayout=(RelativeLayout)findViewById(R.id.protocol_layout);

        nextBtn=(Button)findViewById(R.id.exit_btn_submit);


        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    upImageName);
        } else {
            mFileTemp = new File(getFilesDir(), upImageName);
        }

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        frontView.setOnClickListener(this);
        backView.setOnClickListener(this);
        handView.setOnClickListener(this);

        frontTextView.setOnClickListener(this);
        backTextView.setOnClickListener(this);
        handTextView.setOnClickListener(this);

        nextBtn.setOnClickListener(this);
        protocolLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                if(photoMap!=null&&photoMap.size()>0){
                    quit();
                }else{
                    finish();
                }
                break;
            case R.id.front_view:
                type="front";
                showDialog();
                break;
            case R.id.back_view:
                type="back";
                showDialog();
                break;
            case R.id.hand_view:
                type="hand";
                showDialog();
                break;
            case R.id.front_tview:
                type="front";
                showDialog();
                break;
            case R.id.back_tview:
                type="back";
                showDialog();
                break;
            case R.id.hand_tview:
                type="hand";
                showDialog();
                break;
            case R.id.exit_btn_submit:
                checkPhoto();
                break;
            case R.id.protocol_layout:
                Intent intent=new Intent(CardActivity.this,ProtocolActivity.class);
                intent.putExtra("from","service");
                startActivity(intent);
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
                        Bundle bundle=msg.getData();
                        String message= bundle.getString("message");
                        CustomToast.createToast(mContext,message);
//                        finish();
                        break;
                    case 1:
                        Intent intent=new Intent(mContext,CertCompleteActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {

    }

    private void showDialog(){
        if(photoDialog==null){
            photoDialog=new PhotoDialog(mContext);
            photoDialog.setOnPhotoClickListener(onPhotoListener);
        }

        photoDialog.show();
    }

    OnPhotoListener onPhotoListener=new OnPhotoListener(){
        @Override
        public void openGallery() {
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            startActivityForResult(intent, PhotoDialog.REQUEST_CODE_GALLERY);
        }

        @Override
        public void openCamera() {
            String state = Environment.getExternalStorageState();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
//                Uri mImageCaptureUri = null;
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    mImageCaptureUri = Uri.fromFile(mFileTemp);
                } else {
                    mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
                }
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
                intent.putExtra("return-data", true);
                startActivityForResult(intent,
                        PhotoDialog.REQUEST_CODE_TAKE_PICTURE);
            } catch (Exception e) {
                Log.d(TAG, "cannot take picture", e);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PhotoDialog.REQUEST_CODE_TAKE_PICTURE:

                if(mImageCaptureUri!=null){
                    cropPicture(mImageCaptureUri);
                }

                break;
            case PhotoDialog.REQUEST_CODE_GALLERY:
                cropPicture(data.getData());
                break;
            case PhotoDialog.REQUEST_CODE_CROP_IMAGE:
                Bitmap bitmap = data.getParcelableExtra("data");
                if (bitmap != null && !bitmap.isRecycled()) {

//                    addPictureButton.setVisibility(View.GONE);
//                    changePictureButton.setVisibility(View.VISIBLE);
                    if(StringUtil.isNotNull(type)){
                        if("front".equals(type)){
                            CROP_PHOTO_FILE_NAME=FRONT_PHOTO_FILE_NAME;
                            frontTextView.setVisibility(View.VISIBLE);
                            frontView.setImageBitmap(bitmap);
                        }else if("back".equals(type)){
                            CROP_PHOTO_FILE_NAME=BACK_PHOTO_FILE_NAME;
                            backTextView.setVisibility(View.VISIBLE);
                            backView.setImageBitmap(bitmap);
                        }else if("hand".equals(type)){
                            CROP_PHOTO_FILE_NAME=HAND_PHOTO_FILE_NAME;
                            handTextView.setVisibility(View.VISIBLE);
                            handView.setImageBitmap(bitmap);
                        }
                    }

                    BitmapUtil.saveBitmapToDisk(FileUtils.getImagePath(mContext) + CROP_PHOTO_FILE_NAME, bitmap);

                    uploadImageFile(FileUtils.getImagePath(mContext) + CROP_PHOTO_FILE_NAME);
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 获取拍照后的图片的uri
     *
     * @return
     */
    protected Uri getImageUri() {
        return Uri.fromFile(new File(FileUtils.getImagePath(mContext), upImageName));
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

                    if(StringUtil.isNotNull(type)){
                        if("front".equals(type)){
                            photoMap.put("card_front",imageUploadEntity.getId()+"");
                        }else if("back".equals(type)){
                            photoMap.put("card_back",imageUploadEntity.getId()+"");
                        }else if("hand".equals(type)){
                            photoMap.put("card_person",imageUploadEntity.getId()+"");
                        }
                    }

                }
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showShort(mContext, "图片上传失败");
            }
        });
    }


    /**
     * 检查图片是否上传
     */
    private void checkPhoto(){
        boolean b=false;
        if(photoMap!=null){
            if(photoMap.size()==0){
                CustomToast.createToast(mContext,"请上传证件照");
                return;
            }

            StringBuffer stringBuffer=new StringBuffer();
            for (Map.Entry<String, String> entry : photoMap.entrySet()) {
                stringBuffer.append(entry.getKey()+" ");
            }

            String paramStr=stringBuffer.toString();
            for(int i=0;i<strArray.length;i++){

                if(!paramStr.contains(strArray[i])){
                    if(0==i){
                        b=true;
                        CustomToast.createToast(mContext,"请上传身份正面照");
                        break;
                    }else if(1==i){
                        b=true;
                        CustomToast.createToast(mContext,"请上传身份背面照");
                        break;
                    }else{
                        b=true;
                        CustomToast.createToast(mContext,"请上传手持证件照");
                        break;
                    }
                }
            }

            if(!b){

                if(checkBox.isChecked()){
                    if(paramMap!=null){
                        paramMap.putAll(photoMap);
                        doAuth();
                    }
                }else{
                    Toast.makeText(mContext,"请阅读并同意《志愿者服务协议》",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }



    /**
     * 提交实名认证
     */
    private void doAuth(){

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String mToken= AppPreference.getInstance(mContext).readToken();

        final FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            builder.add(entry.getKey(),entry.getValue());
        }

        OkHttpUtils.put().url(ApiConstants.SUBMIT_CERTIFICATION_API).addHeader("Authorization", mToken).requestBody(builder.build()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if(StringUtil.isNotNull(response)){
                    try{
                        JSONObject jsonObject=new JSONObject(response);
                        String message=jsonObject.optString("message");

                        Message message1=new Message();
                        message1.what=0;
                        Bundle bundle=new Bundle();
                        bundle.putString("message",message);
                        message1.setData(bundle);
                        mHandler.sendMessage(message1);

                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        //返回成功 保存宗教
                        JSONObject reJson=jsonObject.optJSONObject("religion");
                        if(reJson!=null){
                            AppPreference.getInstance(mContext).writeReligion(reJson.optString("name"));
                        }

                        String status=jsonObject.optString("status");
                        if("passed".equals(status)){
                            AppPreference.getInstance(mContext).writeCertification("1");
                        }

                        String userId=AppPreference.getInstance(mContext).readUerId();
                        AppPreference.getInstance(mContext).writeStatus(userId,status);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                mHandler.sendEmptyMessage(1);

            }
        });
    }

    /**
     * 取消报名Dialog
     */
    private void quit(){
        if (cancelCertifDialog == null) {
            cancelCertifDialog = new CancelCertifDialog(mContext, onClickListener);
        }
        cancelCertifDialog.show();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel_btn:
                    cancelCertifDialog.dismiss();
                    CardActivity.this.finish();
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if(photoMap!=null&&photoMap.size()>0){
                quit();
                return true;
            }else{
                finish();
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);

    }

}
