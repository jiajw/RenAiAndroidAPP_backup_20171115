package com.yousails.chrenai.publish.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.publish.widget.SimpleTitleView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.R.attr.data;

/**
 * 发布活动-活动地点
 * Created by sym on 17/7/23.
 */

public class ActPromulgateActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    private String mDetail; //描述内容
    private EditText detailContent_et;
    private SimpleTitleView title_view;
    private RelativeLayout address_rl;
    private TextView address_tv;

    //活动地点选择的requestCode
    private final int requestCodeAddressChoice = 3;

    public static final int REQUEST_CODE_ADDRESS = 1001;

    private RadioGroup rgShow;
    private boolean is_global;

    //活动地点
    private String locationName;

    //详情描述
    private String addressDescription;

    //活动坐标
    private String locationLatAndLog;


    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_act_promulgate);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void findViews() {

        detailContent_et = (EditText) findViewById(R.id.detailContent_et);
        address_rl = (RelativeLayout) findViewById(R.id.address_rl);
        title_view = (SimpleTitleView) findViewById(R.id.title_view);
        title_view.setTitle(R.string.act_promulgate);
        address_tv = (TextView) findViewById(R.id.address_tv);
        rgShow = (RadioGroup) findViewById(R.id.rg_show);

        locationName = getIntent().getStringExtra("locationName");
        if (!TextUtils.isEmpty(locationName)) {
            address_tv.setText(locationName);
        }

        addressDescription = getIntent().getStringExtra("addressDescription");
        if (!TextUtils.isEmpty(addressDescription)) {
            detailContent_et.setText(addressDescription);
        }
    }

    @Override
    protected void setListeners() {
        title_view.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String detail = detailContent_et.getText().toString();
                if (!TextUtils.isEmpty(detail)) {
                    locationName = locationName;
                }
                Intent intent = new Intent();
                intent.putExtra("locationName", locationName);
                intent.putExtra("latAndLon", locationLatAndLog);
                intent.putExtra("addressDescription",detail);
                intent.putExtra("isGlobal", is_global);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        title_view.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        address_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationAndContactsTask();
            }
        });

        rgShow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio0:
                        is_global = false;
                        break;
                    case R.id.radio1:
                        is_global = true;
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case requestCodeAddressChoice:

                    locationName = data.getStringExtra("locationName");
                    locationLatAndLog = data.getStringExtra("latAndLon");
                    if (!TextUtils.isEmpty(locationName)) {
                        address_tv.setText(locationName);
                    }
                    break;
            }
        }
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    private static final int RC_LOCATION_PHONE_STORAGE_PERM = 124;
    @AfterPermissionGranted(RC_LOCATION_PHONE_STORAGE_PERM)
    public void locationAndContactsTask() {
        String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {
            AddressChoiceActivity.launch(mContext, requestCodeAddressChoice);
        } else {
            EasyPermissions.requestPermissions(this, "定位功能需要您的权限才能使用",
                    RC_LOCATION_PHONE_STORAGE_PERM, perms);
            //locationAndContactsTask();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            locationAndContactsTask();
        }else{
            //AddressChoiceActivity.launch(mContext, requestCodeAddressChoice);
        }

    }

    public static void launchForResult(Activity activity) {

        Intent intent = new Intent(activity, ActPromulgateActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }
}
