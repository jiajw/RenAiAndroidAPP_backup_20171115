package com.yousails.chrenai.home.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.adapter.ColumnAdapter;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.home.listener.OnItemClickListener;
import com.yousails.chrenai.person.adapter.MyActAdapter;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.WrapContentLinearLayoutManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MapActActivity extends BaseActivity {


    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;

    private TextView address;

    //private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private ColumnAdapter columnAdapter;



    private List<ActivitiesBean> activitiesBeanList = new ArrayList<>();
    private List<ActivitiesBean> activities = new ArrayList<>();

    private double currLatitude;
    private double currLongitude;
    private int distance;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_map_act);
    }

    @Override
    protected void init() {
        currLatitude = getIntent().getDoubleExtra("currLatitude",0);
        currLongitude = getIntent().getDoubleExtra("currLongitude",0);
        distance = getIntent().getIntExtra("distance",0);
        activities = (ArrayList<ActivitiesBean>) getIntent().getSerializableExtra("activities");
        reverseGeoCodeResult();
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("地图活动");

        address = (TextView) findViewById(R.id.address);

        /*swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0099ff"));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        activitiesBeanList.clear();
                        pageCount=1;
                        isLoadMore=false;
                        getActivities();
                    }
                }).start();
            }
        });*/

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        columnAdapter = new ColumnAdapter(mContext, activitiesBeanList);
        columnAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ActivitiesBean activitiesBean) {
                String longitude=AppPreference.getInstance(mContext).readLongitude();
                String latitude=AppPreference.getInstance(mContext).readLatitude();
                boolean isLogin=AppPreference.getInstance(mContext).readLogin();
                String mToken=AppPreference.getInstance(mContext).readToken();
                Intent intent = new Intent(mContext, ActivitDetailActivity.class);
                intent.putExtra("bean",activitiesBean);
                String url ="";
                if(isLogin){
                    url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?token=" + mToken+"&user_coordinate="+longitude+","+latitude;
                }else{
                    url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?user_coordinate="+longitude+","+latitude;
                }

                intent.putExtra("url", url);
                mContext.startActivity(intent);
            }
        });
        recyclerView.setAdapter(columnAdapter);
        /*recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem + 1 == columnAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if(lastVisibleItem+1>=total){
                        columnAdapter.setFooterSwitch(0);
                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        columnAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore=true;
                        columnAdapter.setFooterSwitch(1);
                        getActivities();
                    }

                }
            }
        });*/
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

    }

    @Override
    public void initData() {
        //getActivities();
        for (ActivitiesBean activity : activities) {
            activitiesBeanList.add(activity);
        }
        columnAdapter.notifyDataSetChanged();
    }

    /**
     * 通过当前地图中心点的坐标反编码拿到位置
     *
     * @param
     */
    private void reverseGeoCodeResult() {
        /**获取经纬度*/

        final GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                geoCoder.destroy();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                address.setText(reverseGeoCodeResult.getAddress());

                geoCoder.destroy();
            }
        });

        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(new LatLng(currLatitude, currLongitude));
        geoCoder.reverseGeoCode(reverseGeoCodeOption);
    }


    /**
     * 获取活动列表
     */
    public void getActivities() {
        if(!NetUtil.detectAvailable(mContext)){
            Toast.makeText(mContext,"请链接网络",Toast.LENGTH_SHORT).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("coordinate", currLongitude+","+currLatitude);
        params.put("distance",String.valueOf((int)(distance/5)));
        params.put("page", "max");
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
                        //if(!isLoadMore){
                            //没搜索到，先清空之前的内容
                            activitiesBeanList.clear();
                        //}

                        if (activitiesList != null && activitiesList.size() > 0) {
                            for (int i = 0; i < activitiesList.size(); i++) {
                                ActivitiesBean activitiesBean = activitiesList.get(i);
                                activitiesBeanList.add(activitiesBean);
                            }

                        }
                        columnAdapter.notifyDataSetChanged();
                        //swipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
