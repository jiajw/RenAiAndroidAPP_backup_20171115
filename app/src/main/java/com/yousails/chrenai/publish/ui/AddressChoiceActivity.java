package com.yousails.chrenai.publish.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.GsonUtils;
import com.yousails.chrenai.common.KeyBoardUtil;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.common.SharedPreferencesUtil;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.publish.adapter.InputSearchAdapter;
import com.yousails.chrenai.publish.adapter.PoiAdapter;
import com.yousails.chrenai.publish.event.PoiDataChoiceEvent;
import com.yousails.common.event.EventBusManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 地图定位
 * Author:WangKunHui
 * Date: 2017/7/21 17:16
 * Desc:
 * E-mail:life_artist@163.com
 */
public class AddressChoiceActivity extends BaseActivity {

    private MapView mMapView;

    public LocationClient mLocationClient = null;

    private BaiduMap baiduMap;

    private ImageView backButton;

    private ImageView searchButton;

    private TextView searchInput;

    private RelativeLayout poiRootParent;

    private RecyclerView poiContainer;

    private ProgressBar poiLoading;

    private RecyclerView inputContainer;

    private Button sureButton;

    private SwipeRefreshLayout inputContainerRoot;

    private PoiAdapter poiAdapter;

    private InputSearchAdapter inputSearchAdapter;

    private List<Poi> defaultPoiList;

    private Address mAddress;

    private BDLocation bdLocation;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    private boolean isInstallKeyBoardHeight = false;

    private ImageView focusImageView;

    private ImageView backToMyPositionView;

    private boolean isDragLocationAble = true;

    /**
     * 输入框下部数据显示状态
     */
    private boolean isInputContainerAble = false;

    /**
     * 被选中的地点
     */
    private PoiInfo selectedPoi;

    /**
     * 默认每页数据条数
     */
    private int defaultPageSize = 10;

    private TextView tvLocation;


   private PoiSearch mPoiSearch;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor[] markIcons = {
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_markb),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_markc),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_markd),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_marke),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_markf),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_markg),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_markh),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_marki),
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_markj)};

    @Override
    protected void setContentView() {
        EventBusManager.getInstance().register(this);
        setContentView(R.layout.activity_address_choice);
    }

    @Override
    protected void init() {


    }


    private void initMap() {

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        baiduMap.setMapStatus(msu);

        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                AddressChoiceActivity.this.bdLocation = bdLocation;
                setMyLocation();

            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {
                LogUtil.d(TAG, "onConnectHotSpotMessage : " + s);
            }
        });
        mLocationClient.start();

        //设置默认坐标为天安门  这里可以缓存一下上次的坐标
//        setCenterLocation(39.915071, 116.403907);
    }

    private void setMyLocation() {

        if (bdLocation == null) {
            return;
        }

        //获取纬度信息
        double latitude = bdLocation.getLatitude();

        //获取精度信息
        double longitude = bdLocation.getLongitude();

        mLocationClient.stop();

        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder().accuracy(
                bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        // 设置定位数据
        baiduMap.setMyLocationData(locData);

        defaultPoiList = bdLocation.getPoiList();
        mAddress = bdLocation.getAddress();
        setCenterLocation(latitude, longitude);

        PoiInfo poiInfo = new PoiInfo();
        poiInfo.address = bdLocation.getAddrStr();
        poiInfo.name = bdLocation.getSemaAptag();
        poiInfo.location = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        markLocation(poiInfo);
        //mHandler.sendEmptyMessage(1);
        reverseGeoCodeResult(baiduMap.getMapStatus());
    }

    private void setCenterLocation(double latitude, double longitude) {

        LatLng latLng = new LatLng(latitude, longitude);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(latLng).zoom(15).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);


    }


    @Override
    protected void findViews() {


        tvLocation = (TextView) findViewById(R.id.tv_location);
        backButton = (ImageView) findViewById(R.id.iv_back);
        searchButton = (ImageView) findViewById(R.id.iv_search);
        searchInput = (TextView) findViewById(R.id.tv_search_input);

        mMapView = (MapView) findViewById(R.id.map_view);
        baiduMap = mMapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        backToMyPositionView = (ImageView) findViewById(R.id.iv_back_to_my_position);
        focusImageView = (ImageView) findViewById(R.id.iv_focus_mark);

        inputContainerRoot = (SwipeRefreshLayout) findViewById(R.id.input_container_root);

        inputContainer = (RecyclerView) findViewById(R.id.input_container);
        inputContainer.setLayoutManager(new LinearLayoutManager(mContext));

        inputSearchAdapter = new InputSearchAdapter();
        inputContainer.setAdapter(inputSearchAdapter);

        poiRootParent = (RelativeLayout) findViewById(R.id.poi_container_root_parent);
        poiLoading = (ProgressBar) findViewById(R.id.poi_loading);
        poiContainer = (RecyclerView) findViewById(R.id.poi_container);
        poiContainer.setLayoutManager(new LinearLayoutManager(mContext));

        sureButton = (Button) findViewById(R.id.btn_sure);

        poiAdapter = new PoiAdapter();
        poiContainer.setAdapter(poiAdapter);

        initKeyBoard();

        initMap();


    }

    private void initKeyBoard() {

        int keyBoardHeight = SharedPreferencesUtil.getInt(mContext,
                ApiConstants.SharedKey.KEY_BOARD_HEIGHT, 0);
        if (keyBoardHeight == 0) {
            isInstallKeyBoardHeight = false;

            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    //获取当前界面可视部分
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    //获取屏幕的高度
                    int screenHeight = getWindow().getDecorView().getRootView().getHeight();
                    //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                    int heightDifference = screenHeight - r.bottom;
                    //判断为关键盘
                    if (heightDifference > 400) {
                        if (!isInstallKeyBoardHeight) {
                            SharedPreferencesUtil.setInt(mContext,
                                    ApiConstants.SharedKey.KEY_BOARD_HEIGHT, heightDifference);
                            resetBottomViewHeight(heightDifference);

                            if (globalLayoutListener != null) {
                                searchInput.getViewTreeObserver().removeOnGlobalLayoutListener(
                                        globalLayoutListener);
                            }
                        }
                    }
                }
            };
            searchInput.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        } else {
            resetBottomViewHeight(keyBoardHeight);
            isInstallKeyBoardHeight = true;
        }

    }

    /**
     * 设置底部高度和软键盘高度相同
     */
    private void resetBottomViewHeight(int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height);
        poiRootParent.setLayoutParams(params);
    }


    @Override
    protected void setListeners() {

        backToMyPositionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMyLocation();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    clickToSearch();
                    return true;
                }
                return false;
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputStr = s.toString();
                if (!TextUtils.isEmpty(inputStr)) {

                    setDragToLocationStatus(false);

                    isInputContainerAble = true;
                    //获取模糊搜索内容
                    poiSearchByKey(inputStr, 1, true, new OnSearchListener() {
                        @Override
                        public void onSuccess(PoiResult poiResult, int currentPageIndex) {
                            LogUtil.i(TAG, "");
                            inputContainerRoot.setVisibility(View.VISIBLE);
                            LogUtil.e("===poiResult=="+poiResult);
                            setInputSearchData(poiResult);
                        }

                        @Override
                        public void onFailure(String error) {
                            LogUtil.i(TAG, "" + error);
                            isInputContainerAble = false;
                            inputContainerRoot.setVisibility(View.GONE);
                        }
                    });
                } else {

                    setDragToLocationStatus(true);
                    isInputContainerAble = false;
                    inputContainerRoot.setVisibility(View.GONE);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToSearch();
            }
        });

        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPoi != null) {
                    java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
                    Intent intent = new Intent();
                    intent.putExtra("locationName", selectedPoi.name);
                    intent.putExtra("latAndLon",
                            df.format(selectedPoi.location.longitude) + "," + df.format(
                                    selectedPoi.location.latitude));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (isDragLocationAble) {
                    reverseGeoCodeResult(mapStatus);
                }
            }
        });
    }


    /**
     * 拖拽定位是否有效
     *
     * @param isAble
     */
    private void setDragToLocationStatus(boolean isAble) {
        isDragLocationAble = isAble;
        if (isAble) {
            focusImageView.setVisibility(View.VISIBLE);
        } else {
            focusImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 通过当前地图中心点的坐标反编码拿到位置
     *
     * @param status
     */
    private void reverseGeoCodeResult(MapStatus status) {
        LatLng mCenterLatLng = status.target;
        /**获取经纬度*/
        double lat = mCenterLatLng.latitude;
        double lng = mCenterLatLng.longitude;

        LogUtil.i(TAG, "lat  :  " + lat + "     lng : " + lng);

        final GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                geoCoder.destroy();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();

                showPoiViewData(poiList);
                Log.e("ongetgeocoderresult", reverseGeoCodeResult.getSematicDescription() + "><" + reverseGeoCodeResult.getAddressDetail().street + "><" + reverseGeoCodeResult.getAddressDetail().district);
                PoiInfo poiInfo = new PoiInfo();
                poiInfo.address = reverseGeoCodeResult.getAddress();
                poiInfo.name = reverseGeoCodeResult.getSematicDescription();
                poiInfo.location = new LatLng(reverseGeoCodeResult.getLocation().latitude, reverseGeoCodeResult.getLocation().longitude);
                markLocation(poiInfo);
                geoCoder.destroy();
            }
        });

        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(new LatLng(lat, lng));
        geoCoder.reverseGeoCode(reverseGeoCodeOption);
    }

    private void clickToSearch() {
        String input = searchInput.getText().toString();
        if (!TextUtils.isEmpty(input)) {
            //获取模糊搜索内容
            poiSearchByKey(input, 1, false, new OnSearchListener() {
                @Override
                public void onSuccess(PoiResult poiResult, int currentPageIndex) {

                    if (poiResult != null && poiResult.getAllPoi() != null && poiResult.getAllPoi().size() > 0) {
                        try {
                            PoiInfo poiInfo = poiResult.getAllPoi().get(0);
                            setCenterLocation(poiInfo.location.latitude, poiInfo.location.longitude);
                            KeyBoardUtil.closeKeyboard(mContext);
                            inputContainerRoot.setVisibility(View.GONE);
                            drawMarks(poiResult);
                            showPoiViewData(poiResult.getAllPoi());
                        } catch (Exception e) {
                            LogUtil.i(TAG, "e : " + e.getMessage());
                        }
                    } else {
                        ToastUtils.showShort(mContext, "搜索失败");
                    }
                }

                @Override
                public void onFailure(String error) {
                    LogUtil.i(TAG, "" + error);
                    inputContainerRoot.setVisibility(View.GONE);
                }
            });
        } else {
            ToastUtils.showShort(mContext, "搜索内容不能为空");
        }
    }

    private void setInputSearchData(PoiResult poiResult) {
        List<PoiInfo> allPoi = poiResult.getAllPoi();
        inputSearchAdapter.setDataList(allPoi);
    }

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        poiAdapter.setData(defaultPoiList, mAddress);
                        break;
                    case 2:
                        PoiInfo info = (PoiInfo) msg.getData().getParcelable("location");
                        tvLocation.setText(info.name);
                        setCenterLocation(info.location.latitude, info.location.longitude);
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    @Override
    protected void initViews() {
        super.initViews();
    }


    @Override
    public void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        MapView.setMapCustomEnable(false);
        mMapView.onDestroy();
        //释放POI检索实例
        mPoiSearch.destroy();

        EventBusManager.getInstance().unregister(this);
    }

    @Subscribe
    public void onEvent(PoiDataChoiceEvent event) {
        if (event == null) {
            return;
        }

        if (event.isDefaultPoi()) {
            poiSearchByKey(event.getPoi().getName(), 1, 1);
        } else {
            this.selectedPoi = event.getPoiInfo();
            markLocation(event.getPoiInfo());
        }
    }

    @Subscribe
    public void onEvent(PoiInfo poiInfo) {
        if (poiInfo == null) {
            return;
        }

        KeyBoardUtil.closeKeyboard(mContext);
        showPoiViewLoading();
        searchInput.clearFocus();

        LatLng location = poiInfo.location;
        setCenterLocation(location.latitude, location.longitude);

        inputContainerRoot.setVisibility(View.GONE);
        //获取模糊搜索内容
        poiSearchByKey(poiInfo.name, 1, false, new OnSearchListener() {
            @Override
            public void onSuccess(PoiResult poiResult, int currentPageIndex) {

                drawMarks(poiResult);
                showPoiViewData(poiResult.getAllPoi());

            }

            @Override
            public void onFailure(String error) {
                LogUtil.i(TAG, "" + error);
                inputContainerRoot.setVisibility(View.GONE);
            }
        });
    }

//    private void search(){
//        KeyBoardUtil.closeKeyboard(mContext);
//        showPoiViewLoading();
//        searchInput.clearFocus();
//    }

    /**
     * 绘制图标
     */
    private void drawMarks(PoiResult poiResult) {

        baiduMap.clear();

        List<PoiInfo> allPoi = poiResult.getAllPoi();
        for (int i = 0; i < allPoi.size(); i++) {
            PoiInfo poiInfo = allPoi.get(i);

            MarkerOptions markerOptions = new MarkerOptions().position(poiInfo.location).icon(
                    markIcons[i]).zIndex(20).draggable(true);
            markerOptions.animateType(MarkerOptions.MarkerAnimateType.drop);

            Marker overlay = (Marker) baiduMap.addOverlay(markerOptions);
        }
    }

    /**
     * POI布局展示loading
     */
    private void showPoiViewLoading() {
        poiRootParent.setVisibility(View.VISIBLE);
        sureButton.setVisibility(View.INVISIBLE);
        poiContainer.setVisibility(View.INVISIBLE);
        poiLoading.setVisibility(View.VISIBLE);
    }

    /**
     * POI布局展示数据
     */
    private void showPoiViewData(List data) {
        poiRootParent.setVisibility(View.VISIBLE);
        sureButton.setVisibility(View.VISIBLE);
        poiContainer.setVisibility(View.VISIBLE);
        poiLoading.setVisibility(View.GONE);

        poiAdapter.setDataList(data);
    }

    /**
     * 根据关键字检索适用于某个城市搜索某个名称相关的POI
     * @param type 是否为input的数据 true input  false click
     */
    private void poiSearchByKey(String key, final int pageIndex, final boolean type,
                                final OnSearchListener onSearchListener) {

        if (bdLocation == null) {
            return;
        }

        Address address = bdLocation.getAddress();
        if (address == null) {
            return;
        }


        //1.创建POI检索实例
        mPoiSearch = PoiSearch.newInstance();
        //2.创建POI检索监听者
        OnGetPoiSearchResultListener poiListener=new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //获取POI检索结果

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                //获取place详情页检索结果

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        };

        //3.设置POI检索监听者
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        //4.发起检索请求
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        //城市
        citySearchOption.city(address.city);
        //检索关键字
        citySearchOption.keyword(key);
        citySearchOption.pageNum(pageIndex);
        citySearchOption.pageCapacity(defaultPageSize);
        mPoiSearch.searchInCity(citySearchOption);


        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult != null && poiResult.getAllPoi() != null
                        && poiResult.getAllPoi().size() != 0) {
                    if (type) {
                        if (isInputContainerAble) {
                            onSearchListener.onSuccess(poiResult, pageIndex);
                        }
                    } else {
                        onSearchListener.onSuccess(poiResult, pageIndex);
                    }
                } else {
                    onSearchListener.onFailure("获取定位信息失败");
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                LogUtil.i(TAG, "onGetPoiDetailResult  :  " + GsonUtils.toJson(poiDetailResult));
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                LogUtil.i(TAG, "onGetPoiIndoorResult  :  " + GsonUtils.toJson(poiIndoorResult));
            }
        });
        mPoiSearch.searchInCity(citySearchOption);
    }

    private void poiSearchByLocation(MapStatus status) {
        if (status == null) {
            return;
        }
        PoiSearch poiSearch = PoiSearch.newInstance();
        PoiNearbySearchOption option = new PoiNearbySearchOption();
//        option.
//        poiSearch.searchNearby()
    }

    private void poiSearchByKey(String key, int pageSize, int pageIndex) {

        if (bdLocation == null) {
            return;
        }

        PoiSearch poiSearch = PoiSearch.newInstance();

        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.radius(1000);
        option.pageCapacity(pageSize);  // 默认每页10条
        option.location(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
        option.pageNum(pageIndex);
        option.keyword(key);

        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult != null && poiResult.getAllPoi() != null
                        && poiResult.getAllPoi().size() != 0) {
                    List<PoiInfo> allPoi = poiResult.getAllPoi();
                    PoiInfo info = allPoi.get(0);
                    markLocation(info);
                } else {
                    ToastUtils.showShort(mContext, "获取定位信息失败");
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                LogUtil.i(TAG, "onGetPoiDetailResult  :  " + GsonUtils.toJson(poiDetailResult));
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                LogUtil.i(TAG, "onGetPoiIndoorResult  :  " + GsonUtils.toJson(poiIndoorResult));
            }
        });
        poiSearch.searchNearby(option);
    }

    private void markLocation(PoiInfo info) {
        if (info == null || info.location == null) {
            return;
        }

        if (baiduMap == null) {
            return;
        }

        this.selectedPoi = info;

        baiduMap.clear();

        /*//定义Maker坐标点
        LatLng point = new LatLng(info.location.latitude, info.location.longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);*/

        Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putParcelable("location", (Parcelable) info);
        msg.setData(b);
        msg.what = 2;
        mHandler.sendMessage(msg);

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

    private interface OnSearchListener {
        void onSuccess(PoiResult poiResult, int currentPageIndex);

        void onFailure(String error);
    }

    public static void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, AddressChoiceActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

}
