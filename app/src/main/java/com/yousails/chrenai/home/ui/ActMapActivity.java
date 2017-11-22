package com.yousails.chrenai.home.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.baidumap.clusterutil.clustering.Cluster;
import com.yousails.chrenai.baidumap.clusterutil.clustering.ClusterItem;
import com.yousails.chrenai.baidumap.clusterutil.clustering.ClusterManager;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.utils.LogUtils;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.PermissionUtils;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class ActMapActivity extends BaseActivity implements BaiduMap.OnMapLoadedCallback {

    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    MapStatus ms;
    private BDLocation mBdLocation;
    public LocationClient mLocationClient = null;
    private ClusterManager<MyItem> mClusterManager;


    private double currLatitude;
    private double currLongitude;

    private float zoomLevel = 14;
    private int screenHeight = 0;
    private int distance = 0;

    private LatLng clickLatLng;

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final ActivitiesBean act;

        public MyItem(LatLng latLng,ActivitiesBean act) {
            mPosition = latLng;
            this.act = act;
        }

        public ActivitiesBean getAct() {
            return act;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_act_location);
        }
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_act_map);
    }

    @Override
    protected void init() {

        if (Build.VERSION.SDK_INT >= 23) {
            String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
            PermissionUtils.requestMultiPermissions(ActMapActivity.this, perms, mPermissionGrant);
        }

    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setVisibility(View.VISIBLE);
        titleView.setText("地图活动");
        mMapView = (MapView) findViewById(R.id.map_view);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        mClusterManager.clearItems();
                        List<MyItem> items = new ArrayList<>();
                        if (activitiesBeanList != null && activitiesBeanList.size() > 0) {
                            for (int i = 0; i < activitiesBeanList.size(); i++) {
                                ActivitiesBean activitiesBean = activitiesBeanList.get(i);
                                LatLng llG6 = new LatLng(Double.parseDouble(activitiesBean.getCoordinate().split(",")[1]),Double.parseDouble(activitiesBean.getCoordinate().split(",")[0]));
                                items.add(new MyItem(llG6,activitiesBean));
                            }
                        }
                        mClusterManager.addItems(items);
                        mClusterManager.cluster();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {
        getScreenHeight();
        initMap();
    }

    private InfoWindow mInfoWindow;

    private void initMap() {
        ms = new MapStatus.Builder().zoom(zoomLevel).build();

        MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap.setMapStatus(msu);

        mBaiduMap.setMaxAndMinZoomLevel(17.0f,5.0f);

        //UiSettings settings=mBaiduMap.getUiSettings();
        //settings.setZoomGesturesEnabled(false);

        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);

        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);


        final boolean isLogin=AppPreference.getInstance(mContext).readLogin();
        final String longitude=AppPreference.getInstance(mContext).readLongitude();
        final String latitude=AppPreference.getInstance(mContext).readLatitude();
        final String mToken=AppPreference.getInstance(mContext).readToken();

        mClusterManager.setOnClusterMapStatusChangeFinishListener(new ClusterManager.OnClusterMapStatusChangeFinishListener() {
            @Override
            public void onClusterMapStatusChangeFinish(MapStatus mapStatus) {
                Log.e("onClusterChangeFinish","能不能进来");
                LatLng mCenterLatLng = mapStatus.target;
                zoomLevel = mapStatus.zoom;
                setCenterLocation(mCenterLatLng.latitude,mCenterLatLng.longitude);
            }
        });

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(final Cluster<MyItem> cluster) {

                List<MyItem> list = new ArrayList<>();
                list.addAll(cluster.getItems());
                final List<ActivitiesBean> activities = new ArrayList<>();
                for (MyItem myItem : list) {
                    activities.add(myItem.getAct());
                }
                View pop = View.inflate(ActMapActivity.this, R.layout.pop_act_map, null);
                TextView tv_title = (TextView) pop.findViewById(R.id.tv_title);
                tv_title.setText(list.get(0).getAct().getTitle());
                TextView tv_date = (TextView) pop.findViewById(R.id.tv_date);
                tv_date.setText(list.get(0).getAct().getStarted_at().substring(0,10));
                LinearLayout ll_more = (LinearLayout) pop.findViewById(R.id.ll_more);
                ll_more.setVisibility(View.VISIBLE);
                TextView tv_more = (TextView) pop.findViewById(R.id.tv_more);
                String str="此位置还有<font color='#FF0000'>"+(cluster.getSize()-1)+"</font>个活动";
                tv_more.setText(Html.fromHtml(str));

                LatLng ll = cluster.getPosition();
                //mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(pop), ll, -147, listener);
                if(mInfoWindow != null&&clickLatLng!=null&&clickLatLng.latitude==ll.latitude&&clickLatLng.longitude==ll.longitude){
                    mBaiduMap.hideInfoWindow();
                    mInfoWindow=null;
                }else {
                    clickLatLng = ll;
                    mInfoWindow = new InfoWindow(pop, ll, -137);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                pop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInfoWindow != null) {
                            mBaiduMap.hideInfoWindow();
                            mInfoWindow=null;
                        }
                        Intent intent = new Intent(mContext,MapActActivity.class);
                        intent.putExtra("currLatitude",cluster.getPosition().latitude);
                        intent.putExtra("currLongitude",cluster.getPosition().longitude);
                        intent.putExtra("distance",distance);
                        intent.putExtra("activities",(Serializable) activities);
                        startActivity(intent);
                    }
                });
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(final MyItem item) {

                View pop = View.inflate(ActMapActivity.this, R.layout.pop_act_map, null);
                TextView tv_title = (TextView) pop.findViewById(R.id.tv_title);
                tv_title.setText(item.getAct().getTitle());
                TextView tv_date = (TextView) pop.findViewById(R.id.tv_date);
                tv_date.setText((item.getAct().getStarted_at().substring(0,10)));

                LatLng ll = item.getPosition();
                //mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(pop), ll, -147, listener);
                if(mInfoWindow != null&&clickLatLng!=null&&clickLatLng.latitude==ll.latitude&&clickLatLng.longitude==ll.longitude){
                        mBaiduMap.hideInfoWindow();
                        mInfoWindow=null;
                }else {
                    clickLatLng = ll;
                    mInfoWindow = new InfoWindow(pop, ll, -137);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                pop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInfoWindow != null) {
                            mBaiduMap.hideInfoWindow();
                            mInfoWindow=null;
                        }
                        Intent intent = new Intent(mContext, ActivitDetailActivity.class);
                        intent.putExtra("bean",item.getAct());
                        String url ="";
                        if(isLogin){
                            url = ApiConstants.BASE_URL + "/activities/" + item.getAct().getId() + "?token=" + mToken+"&user_coordinate="+longitude+","+latitude;
                        }else{
                            url = ApiConstants.BASE_URL + "/activities/" + item.getAct().getId() + "?user_coordinate="+longitude+","+latitude;
                        }

                        intent.putExtra("url", url);
                        mContext.startActivity(intent);
                    }
                });

                return false;
            }
        });

        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mBdLocation = bdLocation;
                setMyLocation();
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {
                LogUtil.d(TAG, "onConnectHotSpotMessage : " + s);
            }
        });
        mLocationClient.start();

    }

    private void getScreenHeight(){
        Point size=new Point();
        Display display=getWindowManager().getDefaultDisplay();
        display.getRealSize(size);
        DisplayMetrics metrics=new DisplayMetrics();
        display.getRealMetrics(metrics);
        int height=size.y;
        float xdpi=metrics.xdpi;
        float realHeight=height/xdpi*2.54f;
        screenHeight = (int) realHeight;
    }

    //计算屏幕范围的百度地图距离
    private void getDistance(){
        zoomLevel = mBaiduMap.getMapStatus().zoom;

        switch ((int) zoomLevel) {
            case 22:
                distance = (int)screenHeight *2;
                break;
            case 21:
                distance = (int)screenHeight *5;
                break;
            case 20:
                distance = (int)screenHeight *10;
                break;
            case 19:
                distance = (int)screenHeight *20;
                break;
            case 18:
                distance = (int)screenHeight *50;
                break;
            case 17:
                distance = (int)screenHeight *100;
                break;
            case 16:
                distance = (int)screenHeight *200;
                break;
            case 15:
                distance = (int)screenHeight *500;
                break;
            case 14:
                distance = (int)screenHeight *1000;
                break;
            case 13:
                distance = (int)screenHeight *2000;
                break;
            case 12:
                distance = (int)screenHeight *5000;
                break;
            case 11:
                distance = (int)screenHeight *10000;
                break;
            case 10:
                distance = (int)screenHeight *20000;
                break;
            case 9:
                distance = (int)screenHeight *25000;
                break;
            case 8:
                distance = (int)screenHeight *50000;
                break;
            case 7:
                distance = (int)screenHeight *100000;
                break;
            case 6:
                distance = (int)screenHeight *200000;
                break;
            case 5:
                distance = (int)screenHeight *500000;
                break;
            case 4:
                distance = (int)screenHeight *1000000;
                break;
            case 3:
                distance = (int)screenHeight *2000000;
                break;

        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private void setMyLocation() {
        if (mBdLocation == null) {
            return;
        }
        //获取纬度信息
        currLatitude = mBdLocation.getLatitude();
        //获取精度信息
        currLongitude = mBdLocation.getLongitude();

        mLocationClient.stop();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder().accuracy(
                mBdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(mBdLocation.getLatitude())
                .longitude(mBdLocation.getLongitude()).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        //获取纬度信息
        double latitude = mBdLocation.getLatitude();
        //获取精度信息
        double longitude = mBdLocation.getLongitude();
        setCenterLocation(latitude, longitude);
    }

    /**
     *@Name :设置地图中心位置
     *@Function :
     *@Param :[latitude, longitude]
     *@Return :void
     *@Exception :
     *@Author :liuwen
     *@Date :2017/9/11
     */
    private void setCenterLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        //定义地图状态
        ms = new MapStatus.Builder().target(latLng).zoom(zoomLevel).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(ms);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        getActivities();
    }


    private List<ActivitiesBean> activitiesBeanList = new ArrayList<>();

    /**
     * 获取活动列表
     */
    public void getActivities() {
        getDistance();
        if(!NetUtil.detectAvailable(mContext)){
            Toast.makeText(mContext,"请链接网络",Toast.LENGTH_SHORT).show();
        }


        Map<String, String> params = new HashMap<>();
        params.put("coordinate", currLongitude+","+currLatitude);
        params.put("distance",String.valueOf(distance));
        params.put("per_page", "max");
        params.put("include", "user");


        OkHttpUtils.get().url(ApiConstants.GET_ACTIVITIES_API).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        Type type = new TypeToken<ArrayList<ActivitiesBean>>() {
                        }.getType();
                        ArrayList<ActivitiesBean> activitiesList = new Gson().fromJson(jsonArray.toString(), type);
                        activitiesBeanList.clear();
                        activitiesBeanList.addAll(activitiesList);

                        mHandler.sendEmptyMessage(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_MULTI_PERMISSION:

                    break;

            }
        }
    };
}
