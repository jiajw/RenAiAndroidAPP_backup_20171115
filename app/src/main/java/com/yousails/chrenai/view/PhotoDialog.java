package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.login.listener.OnPhotoListener;

/**
 * Created by Administrator on 2017/8/9.
 */

public class PhotoDialog extends BaseDialog implements View.OnClickListener{
    public static final int REQUEST_CODE_TAKE_PICTURE = 9528;
    public static final int REQUEST_CODE_GALLERY = 28;
    public static final int REQUEST_CODE_CROP_IMAGE = 958;
    private Context mContext;
    private String uri;

    private OnPhotoListener onPhotoListener;

    public PhotoDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setOnPhotoClickListener(OnPhotoListener onPhotoListener){
        this.onPhotoListener=onPhotoListener;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        setAttributes(getWindow().getWindowManager().getDefaultDisplay()
                .getWidth(), Gravity.BOTTOM);
    }

    private void initContent() {
        setContentView(R.layout.photo_dialog);

        TextView galleryView = (TextView) findViewById(R.id.gallery_tview);//相册
        TextView cameraView = (TextView) findViewById(R.id.camera_tview);//拍照
        TextView cancelTextView = (TextView) findViewById(R.id.cancel_tview);


        galleryView.setOnClickListener(this);
        cameraView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gallery_tview:
                dismiss();
                if(onPhotoListener!=null){
                    onPhotoListener.openGallery();
                }
                break;
            case R.id.camera_tview:
                dismiss();
                if(onPhotoListener!=null){
                    onPhotoListener.openCamera();
                }
                break;
            case R.id.cancel_tview:
                dismiss();
                break;
        }
    }
}
