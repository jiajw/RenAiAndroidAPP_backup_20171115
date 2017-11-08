package com.yousails.chrenai.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.utils.CustomToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */

public class MapDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private String uri;

    public MapDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public MapDialog(Context context, String uri) {
        super(context);
        this.mContext = context;
        this.uri = uri;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        setAttributes(getWindow().getWindowManager().getDefaultDisplay()
                .getWidth(), Gravity.BOTTOM);
    }

    private void initContent() {
        setContentView(R.layout.map_dialog);

        TextView baiduTextView = (TextView) findViewById(R.id.baidu_tview);
        TextView amapTextView = (TextView) findViewById(R.id.amap_tview);
        TextView googleTextView = (TextView) findViewById(R.id.google_tview);
        TextView cancelTextView = (TextView) findViewById(R.id.cancel_tview);


        baiduTextView.setOnClickListener(this);
        amapTextView.setOnClickListener(this);
        googleTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.baidu_tview:
                if (isAvilible(mContext, "com.baidu.BaiduMap")) {
                    Uri mUri = Uri.parse(uri);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    mIntent.setPackage("com.baidu.BaiduMap");
                    mContext.startActivity(mIntent);
                    dismiss();
                } else {
                    CustomToast.createToast(mContext, "请先安装百度地图");
                }
                break;
            case R.id.amap_tview:
                if (isAvilible(mContext, "com.autonavi.minimap")) {
//                    Uri mUri = Uri.parse("geo:"+116.305652+","+40.16113+"?q="+"活动地址");
                    Uri mUri = Uri.parse(uri);
                    Intent intent = new Intent("android.intent.action.VIEW", mUri);
                    intent.setPackage("com.autonavi.minimap");
                    mContext.startActivity(intent);
                    dismiss();
                } else {
                    CustomToast.createToast(mContext, "请先安装高德地图");
                }
                break;
            case R.id.google_tview:
                if (isAvilible(mContext, "com.google.android.apps.maps")) {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.google.android.apps.maps"));
                    mContext.startActivity(intent);
                    dismiss();
                } else {
                    CustomToast.createToast(mContext, "请先安装谷歌地图");
                }
                break;
            case R.id.cancel_tview:
                dismiss();
                break;
        }
    }


    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    private boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


}
