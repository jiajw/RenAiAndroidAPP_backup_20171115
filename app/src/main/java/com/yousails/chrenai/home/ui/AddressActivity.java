package com.yousails.chrenai.home.ui;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.MapDialog;

/**
 * Created by Administrator on 2017/7/31.
 */

public class AddressActivity extends BaseActivity {
    /** 黑马坐标（北京市海淀区东北旺南路45号）*/
    protected LatLng hmPos = new LatLng(40.017582, 116.480804);

    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    protected BaiduMap baiduMap;
    private MapView mapView;
    private Marker mMarkerA;
    private InfoWindow mInfoWindow;
    private View pop;
    private MapDialog mapDialog;
    private BitmapDescriptor icon;
    private TextView tv_title;
    private String address="";
    private String coordinate;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_address);
    }

    @Override
    protected void init() {
        address=getIntent().getStringExtra("address");
        coordinate=getIntent().getStringExtra("coordinate");//116.480804,40.017582
        System.out.print(coordinate+"");
        if(StringUtil.isNotNull(coordinate)&&coordinate.contains(",")){
            int position=coordinate.indexOf(",");
            double longitude= Double.parseDouble(coordinate.substring(0,position));
            double latitude= Double.parseDouble(coordinate.substring(position+1));
            hmPos = new LatLng(latitude, longitude);
        }
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("查看活动位置");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);

        mapView=(MapView)findViewById(R.id.bmapView);

        baiduMap = mapView.getMap();	// 获取地图控制器



        // 3.	设置地图中心点
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(hmPos);
        baiduMap.setMapStatus(mapStatusUpdate);

        // 4.	设置地图缩放为15
        mapStatusUpdate = MapStatusUpdateFactory.zoomTo(15.0f);
        baiduMap.setMapStatus(mapStatusUpdate);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;

        }
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

        initMarker();


    }


    BaiduMap.OnMarkerClickListener mOnMarkerClickListener = new BaiduMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(final Marker marker) {

            // 显示一个泡泡
            if (mInfoWindow == null) {
                LatLng ll = marker.getPosition();
//                pop = View.inflate(AddressActivity.this, R.layout.pop_layout, null);
//                tv_title = (TextView) pop.findViewById(R.id.tv_title);
//                tv_title.setText(address);
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(pop), ll, -147, listener);
                baiduMap.showInfoWindow(mInfoWindow);

            } else {
                baiduMap.hideInfoWindow();
                mInfoWindow=null;
            }
            return true;
        }
    };

    InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
        public void onInfoWindowClick() {
               baiduMap.hideInfoWindow();

            if(mapDialog==null){
                String uri="geo:"+coordinate+"?q="+address;
                mapDialog=new MapDialog(mContext,uri);
            }
            mapDialog.show();
        }
    };


    /** 初始化标志 */
     private void initMarker() {

         MarkerOptions options = new MarkerOptions();
         icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_location);
         options.position(hmPos)     // 位置
         .title("活动位置")        // title
         .icon(icon)         // 图标
          .zIndex(0)
         .draggable(true);   // 设置图标可以拖动


         mMarkerA = (Marker) (baiduMap.addOverlay(options));
         LatLng ll = mMarkerA.getPosition();

         pop = View.inflate(AddressActivity.this, R.layout.pop_layout, null);
         tv_title = (TextView) pop.findViewById(R.id.tv_title);
         tv_title.setText(address);

         mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(pop), ll, -147, listener);
         baiduMap.showInfoWindow(mInfoWindow);

         baiduMap.setOnMarkerClickListener(mOnMarkerClickListener);

     }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        if(icon!=null){
            icon.recycle();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

}
