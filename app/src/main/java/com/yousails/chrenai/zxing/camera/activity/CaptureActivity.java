/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yousails.chrenai.zxing.camera.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.yousails.chrenai.R;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.zxing.camera.CameraManager;
import com.yousails.chrenai.zxing.decode.DecodeThread;
import com.yousails.chrenai.zxing.utils.BeepManager;
import com.yousails.chrenai.zxing.utils.CaptureActivityHandler;
import com.yousails.chrenai.zxing.utils.InactivityTimer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.yousails.chrenai.config.ModelApplication.mContext;


/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback,EasyPermissions.PermissionCallbacks {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private LinearLayout backLayout;
	private RelativeLayout searchLayout;
	private LinearLayout sharedLayout;
	private TextView titleView;

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;

	private SurfaceView scanPreview = null;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	private boolean isHasSurface = false;

	private boolean barcodeScanned = false;
	private boolean previewing = true;
	private TranslateAnimation animation;

	private int requestCode = 0;

	private String activity_id="";
	private String activity_name = "";


	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_capture);


		cameraTask();

		backLayout=(LinearLayout)findViewById(R.id.title_back);
		searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
		searchLayout.setVisibility(View.GONE);
		titleView=(TextView)findViewById(R.id.title);
		titleView.setText(R.string.txt_scan);

		sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
		sharedLayout.setVisibility(View.INVISIBLE);

		backLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);

		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				0.9f);
		animation.setDuration(4500);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		scanLine.startAnimation(animation);
		/*
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("result", results);
				resultIntent.putExtras(bundle);
				CaptureActivity.this.setResult(RESULT_OK, resultIntent);
				finish();
			}
		*/



		//requestCode = getIntent().getIntExtra("requestCode",0);
		activity_id = getIntent().getStringExtra("activity_id");
		activity_name = getIntent().getStringExtra("activity_name");
	}
	private static final int RC_CAMERA_PERM = 124;
	@AfterPermissionGranted(RC_CAMERA_PERM)
	public void cameraTask() {
		String[] perms = { Manifest.permission.CAMERA };
		if (EasyPermissions.hasPermissions(this, perms)) {
			// Have permissions, do the thing!
			//Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
		} else {
			// Ask for both permissions
			EasyPermissions.requestPermissions(this, "扫码功能需要您的权限才能使用",
					RC_CAMERA_PERM, perms);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> list) {
		// Some permissions have been granted
		// ...
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> list) {
		// Some permissions have been denied
		// ...
		//finish();
		if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
			//new AppSettingsDialog.Builder(this).build().show();
			cameraTask();
		}

	}
	private void scanStop(){
		cameraManager.stopPreview();
		scanLine.clearAnimation();
	}
	private void scanRestart(){
		cameraManager.startPreview();
		restartPreviewAfterDelay(500);
		scanLine.startAnimation(animation);
	}
	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		handler = null;

		if (isHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(scanPreview.getHolder());
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			scanPreview.getHolder().addCallback(this);
		}

		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!isHasSurface) {
			scanPreview.getHolder().removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!isHasSurface) {
			isHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isHasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * 
	 * @param bundle
	 *            The extras
	 */
	public void handleDecode(Result rawResult, Bundle bundle) {
		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();

		previewing = false;
		barcodeScanned = true;

		//Toast.makeText(CaptureActivity.this,rawResult.getText(),Toast.LENGTH_LONG).show();
		Log.e("code.content",rawResult.getText().toString());
		String user_id = "";
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(rawResult.getText().toString());
			user_id = jsonObj.optString("user_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Intent resultIntent = new Intent();
		resultIntent.putExtra("user_id",user_id);
		resultIntent.putExtra("activity_id",activity_id);
		resultIntent.putExtra("activity_name",activity_name);
		CaptureActivity.this.setResult(RESULT_OK, resultIntent);
		finish();
		//userSign(activity_id,user_id);


	}


	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
			}

			initCrop();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		// camera error
		/*AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("相机打开出错，请稍后重试");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}

		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		builder.show();*/
		cameraTask();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	private void userSign(String activityId,String userId){
		Map<String, String> params = new HashMap<>();
		params.put("id",activityId);
		params.put("user_id", userId);

		OkHttpUtils.post().url(ApiConstants.USER_SIGN+activityId+"/records").addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, String response, Exception e, int id) {

				if (StringUtil.isNotNull(response) && response.contains("message")) {
					try {
						JSONObject jsonObj = new JSONObject(response);
						String message = jsonObj.optString("message");
						Toast.makeText(CaptureActivity.this,message,Toast.LENGTH_LONG).show();
						finish();

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			}

			@Override
			public void onResponse(String response, int id) {
				try {
					Log.e("aaa",response);
					JSONObject jsonObject = new JSONObject(response);
					String message = jsonObject.optString("message");
					Toast.makeText(CaptureActivity.this,"报名成功！",Toast.LENGTH_LONG).show();
					finish();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Rect getCropRect() {
		return mCropRect;
	}

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = cameraManager.getCameraResolution().y;
		int cameraHeight = cameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}