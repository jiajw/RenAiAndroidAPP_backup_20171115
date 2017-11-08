package com.yousails.chrenai.publish.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.yousails.chrenai.R;
import com.yousails.chrenai.bean.HttpBaseBean;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.net.HttpRequestUtil;
import com.yousails.chrenai.net.Path;
import com.yousails.chrenai.net.listener.LoadingRequestListener;
import com.yousails.chrenai.net.listener.ModelRequestListener;
import com.yousails.chrenai.publish.bean.ImageUploadEntity;
import com.yousails.chrenai.publish.bean.SimpleDialogItem;
import com.yousails.chrenai.publish.util.PhotoHelper;

import org.wordpress.android.editor.EditorFragment;
import org.wordpress.android.editor.EditorFragmentAbstract;
import org.wordpress.android.editor.EditorMediaUploadListener;
import org.wordpress.android.editor.OnMenuClickListener;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.helpers.MediaFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sym on 17/7/15.
 */

public class EditorActivity extends FragmentActivity
        implements EditorFragmentAbstract.EditorFragmentListener,
        OnMenuClickListener,
        EditorFragmentAbstract.EditorDragAndDropListener,
        View.OnClickListener {

    public static final int USE_LEGACY_EDITOR = 2;

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE = 1112;
    public static final int ADD_MEDIA_SLOW_NETWORK_REQUEST_CODE = 1113;

    public static final int EDITOR_CODE = 501;

    public static final String MEDIA_REMOTE_ID_SAMPLE = "123";

    public static final String DRAFT_PARAM = "DRAFT_PARAM";

    public static final String EDITOR_HTML = "editor_html";

    private EditorFragment mEditorFragment;
    private Map<String, String> mFailedUploads;
    private ArrayList<SimpleDialogItem> mSize;

    private String mContent;

    /**
     * 开启富文本Activity
     */
    public static void launchForResult(Activity context, String html) {

        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EDITOR_HTML, html);
        context.startActivityForResult(intent, EDITOR_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mFailedUploads = new HashMap<>();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof EditorFragmentAbstract) {
            mEditorFragment = (EditorFragment) fragment;
        }
    }

    private View mPopMenuView;

    @Override
    public void onEditorFragmentInitialized() {
        //pop
        mPopMenuView = LayoutInflater.from(this).inflate(R.layout.pop_editor_album, null);
        mPopMenuView.findViewById(R.id.album_tv).setOnClickListener(this);
        mPopMenuView.findViewById(R.id.camera_tv).setOnClickListener(this);
        mPopMenuView.findViewById(R.id.dismiss_tv).setOnClickListener(this);
        mPopMenuView.findViewById(R.id.root_view).setOnClickListener(this);

        // arbitrary setup
        mEditorFragment.setFeaturedImageSupported(true);
        mEditorFragment.setBlogSettingMaxImageWidth("600");
        mEditorFragment.setDebugModeEnabled(true);

        // get title and content and draft switch
        boolean isLocalDraft = getIntent().getBooleanExtra(DRAFT_PARAM, true);
        mEditorFragment.setOnMenuClickListener(this);
        mEditorFragment.setLocalDraft(isLocalDraft);

        Intent intent = getIntent();
        String html = intent.getStringExtra(EDITOR_HTML);
        if (!TextUtils.isEmpty(html)) {
            mEditorFragment.insertHtml(html);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        Uri mediaUri = null;
        if (requestCode == PhotoHelper.requestCodeAlbum) {
            mediaUri = data.getData();
        } else if (requestCode == PhotoHelper.requestCodeCamera) {
            mediaUri = PhotoHelper.getInstance().getImageUri(this);
        } else {
            mediaUri = data.getData();
        }

        MediaFile mediaFile = new MediaFile();
        String mediaId = String.valueOf(System.currentTimeMillis());
        mediaFile.setMediaId(mediaId);
        mediaFile.setVideo(mediaUri.toString().contains("video"));

        switch (requestCode) {

            case PhotoHelper.requestCodeAlbum:
            case PhotoHelper.requestCodeCamera:

                String path = com.ipaulpro.afilechooser.utils.FileUtils.getPath(EditorActivity.this,
                        mediaUri);
                mEditorFragment.appendMediaFile(mediaFile, path, null);

                if (mEditorFragment instanceof EditorMediaUploadListener) {
                    simulateFileUpload(mediaId, path);
                }
                break;
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
               /* String path = com.ipaulpro.afilechooser.utils.FileUtils.getPath(EditorActivity
               .this, mediaUri);
                mEditorFragment.appendMediaFile(mediaFile, path, null);

                if (mEditorFragment instanceof EditorMediaUploadListener) {
                    simulateFileUpload(mediaId, path);
                }*/
                break;
            case ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE:
                mEditorFragment.appendMediaFile(mediaFile, mediaUri.toString(), null);

                if (mEditorFragment instanceof EditorMediaUploadListener) {
                    simulateFileUploadFail(mediaId, mediaUri.toString());
                }
                break;
            case ADD_MEDIA_SLOW_NETWORK_REQUEST_CODE:
                mEditorFragment.appendMediaFile(mediaFile, mediaUri.toString(), null);

                if (mEditorFragment instanceof EditorMediaUploadListener) {
                    simulateSlowFileUpload(mediaId, mediaUri.toString());
                }
                break;
        }
    }

    private void simulateFileUpload(final String mediaId, final String mediaUrl) {

        HttpRequestUtil.getInstance(ApiConstants.POST_FILE_UPLOAD).setDefaultHeader().upload(
                new Path(mediaUrl, 1024),
                ImageUploadEntity.class, new LoadingRequestListener() {

                    @Override
                    public void start(long totalBytes) {

                    }

                    @Override
                    public void loading(long current, long total, float percent, float speed) {

                        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadProgress(mediaId,
                                percent);
                    }

                    @Override
                    public void finish() {

                    }

                    @Override
                    public void onSuccess(HttpBaseBean httpBaseBean) {

                        // ((EditorMediaUploadListener) mEditorFragment).onMediaUploadProgress
                        // (mediaId, 1);


                        if (httpBaseBean != null) {
                            ImageUploadEntity imageUploadEntity = (ImageUploadEntity) httpBaseBean;
                            String file = imageUploadEntity.getFile();

                            MediaFile mediaFile = new MediaFile();
                            mediaFile.setMediaId(MEDIA_REMOTE_ID_SAMPLE);
                            mediaFile.setFileURL(file+"?imageView2/0/w/500");
                            ((EditorMediaUploadListener) mEditorFragment).onMediaUploadSucceeded(
                                    mediaId, mediaFile);
                            if (mFailedUploads.containsKey(mediaId)) {
                                mFailedUploads.remove(mediaId);
                            }

                        } else {
                            ToastUtils.showShort(EditorActivity.this, "图片上传失败");
                            ((EditorMediaUploadListener) mEditorFragment).onMediaUploadFailed(
                                    mediaId,
                                    getString(R.string.tap_to_try_again));

                            mFailedUploads.put(mediaId, mediaUrl);

                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showShort(EditorActivity.this, "图片上传失败");
                        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadFailed(mediaId,
                                getString(R.string.tap_to_try_again));

                        mFailedUploads.put(mediaId, mediaUrl);
                    }
                });
    }


    private void simulateFileUploadFail(final String mediaId, final String mediaUrl) {

        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadFailed(mediaId,
                getString(R.string.tap_to_try_again));

        mFailedUploads.put(mediaId, mediaUrl);
    }

    private void simulateSlowFileUpload(final String mediaId, final String mediaUrl) {

        MediaFile mediaFile = new MediaFile();
        mediaFile.setMediaId(MEDIA_REMOTE_ID_SAMPLE);
        mediaFile.setFileURL(mediaUrl+"?imageView2/0/w/500");

        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadSucceeded(mediaId, mediaFile);
        if (mFailedUploads.containsKey(mediaId)) {
            mFailedUploads.remove(mediaId);
        }
    }

    @Override
    public void onSettingsClicked() {

    }

    @Override
    public void onAddMediaClicked() {

    }

    @Override
    public void onMediaRetryClicked(String mediaId) {
        if (mFailedUploads.containsKey(mediaId)) {
            simulateFileUpload(mediaId, mFailedUploads.get(mediaId));
        }
    }

    @Override
    public void onMediaUploadCancelClicked(String mediaId, boolean delete) {

    }

    @Override
    public void onFeaturedImageChanged(long mediaId) {

    }

    @Override
    public void onVideoPressInfoRequested(String videoId) {

    }

    @Override
    public String onAuthHeaderRequested(String url) {
        return null;
    }

    @Override
    public void saveMediaFile(MediaFile mediaFile) {

    }

    @Override
    public void onTrackableEvent(EditorFragmentAbstract.TrackableEvent event) {
        AppLog.d(AppLog.T.EDITOR, "Trackable event: " + event);
    }

    @Override
    public void onMediaDropped(ArrayList<Uri> mediaUri) {

    }

    @Override
    public void onRequestDragAndDropPermissions(DragEvent dragEvent) {

    }

    private PopupWindow mPop;

    /**
     * 处理编辑器按钮点击事件
     */
    @Override
    public void onMenuClick(View targt, Object... args) {
        int id = targt.getId();
        switch (id) {
            case R.id.image_img:
                hideKeyboardForCurrentFous();

                if(mPop==null){
                    mPop = new PopupWindow(mPopMenuView, LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, true);
                }
                mPop.setOutsideTouchable(true);
                //让pop可以点击外面消失掉
                mPop.setBackgroundDrawable(new ColorDrawable(0));

//                 ViewGroup parent = (ViewGroup)mPopMenuView.getParent();
//                if(parent!=null){
//                    parent.removeAllViews();
//                }


                mPop.showAtLocation(targt, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.size_img:
                break;
            case R.id.color_img:
                break;
        }
    }

    private final String TAG = getClass().getSimpleName();

    @Override
    public void finish() {
        super.finish();
        //moveTaskToBack(true);
    }

    /**
     * 上传图片
     */
    private void uploadImageFile(String path) {

        HttpRequestUtil.getInstance(ApiConstants.POST_FILE_UPLOAD).setDefaultHeader().upload(path,
                ImageUploadEntity.class, new ModelRequestListener<ImageUploadEntity>() {
                    @Override
                    public void onSuccess(ImageUploadEntity imageUploadEntity) {
                        LogUtil.i(TAG, "imageUploadEntity : " + imageUploadEntity);
                        if (imageUploadEntity == null) {


                        } else {
                            ToastUtils.showShort(EditorActivity.this, "图片上传失败");
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showShort(EditorActivity.this, "图片上传失败");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (null != mEditorFragment) {
            mEditorFragment.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.album_tv:
                PhotoHelper.getInstance().openAlbum(this);
                mPop.dismiss();
                break;
            case R.id.camera_tv:
                PhotoHelper.getInstance().openCamera(this);
                mPop.dismiss();
                break;
            case R.id.dismiss_tv:
            case R.id.root_view:
                mPop.dismiss();
                break;
        }
    }

    /**
     * 在指定view处显示键盘
     */
    public void showKeyboardAtView(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboardForCurrentFous() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}