package com.yousails.chrenai.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseMenuFragment;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.db.ActivitiesDBService;
import com.yousails.chrenai.home.adapter.ColumnAdapter;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.BannerBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.home.listener.OnItemClickListener;
import com.yousails.chrenai.home.ui.ActivitDetailActivity;
import com.yousails.chrenai.login.bean.UserBean;
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

/**
 * Created by Administrator on 2017/6/27.
 */


public class ColumnFragment extends BaseMenuFragment implements OnItemClickListener {
    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ColumnAdapter columnAdapter;
    private int index;
    private int cId;
    private String cityId;
    private List<String> imageUrl = new ArrayList<String>();
    private List<String> titles = new ArrayList<String>();
    private List<String> tempStr = new ArrayList<String>();
    private List<BannerBean> bannerLists = new ArrayList<BannerBean>();
    private List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
    private ActivitiesDBService activitiesDBService = null;
    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount=1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitiesDBService = new ActivitiesDBService(mContext);
        index = getArguments().getInt("index");
        cId = getArguments().getInt("cId");

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_column, container, false);
        findViews();
        handleMessage();
        return mRootView;
    }

    private void findViews() {
        index = getArguments().getInt("index");
        nodataLayout = (LinearLayout) mRootView.findViewById(R.id.nodata_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeLayout);
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
                        mHandler.sendEmptyMessage(2);
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        columnAdapter = new ColumnAdapter(mContext, activitiesBeanList);
        columnAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(columnAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(mContext,
//                DividerItemDecoration.VERTICAL_LIST));

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                columnAdapter.setFooterSwitch(0);
                            }
                        });

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
        });

    }


    @Override
    public void initData() {

        if (0 == index) {
            setHeader(recyclerView);
        }

        getActivities();
    }

    private void setHeader(RecyclerView view) {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.banner_layout, view, false);
        columnAdapter.setHeaderView(headerView);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        if (activitiesBeanList != null && activitiesBeanList.size() > 0) {
                            columnAdapter.notifyDataChanged(activitiesBeanList);
                            nodataLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if(total>columnAdapter.getItemCount()){
                                columnAdapter.setFooterSwitch(1);
                            }else{
                                columnAdapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);
                        } else {
                            if (0 == index) {

                                if(recyclerView.getChildCount()>0){
                                    nodataLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }else{
                                    nodataLayout.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            }else{
                                nodataLayout.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }

                        break;
                    case 2:
                        pageCount=1;
                        isLoadMore=false;
                        getActivities();
                        break;

                }
            }
        };
    }

    /**
     * 获取活动列表
     */
    public void getActivities() {
        if(!NetUtil.detectAvailable(mContext)){
            Toast.makeText(mContext,"请链接网络",Toast.LENGTH_SHORT).show();
        }

        String city = AppPreference.getInstance(mContext).readCurrentCity();
        if (StringUtil.isNotNull(city)) {
            cityId= city.substring(0,city.indexOf(","));
        }else{
            cityId="";
        }

        String longitude=AppPreference.getInstance(mContext).readLongitude();
        String latitude=AppPreference.getInstance(mContext).readLatitude();

        Map<String, String> params = new HashMap<>();
//        params.put("coordinate", longitude+","+latitude);
        params.put("page", pageCount+"");
        params.put("category", cId + "");
        params.put("city", cityId);
        params.put("user_coordinate", longitude+","+latitude);
        params.put("include", "user");
//        params.put("include", "user,category,userApplication");


        OkHttpUtils.get().url(ApiConstants.GET_ACTIVITIES_API).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                if (activitiesDBService != null) {

                    String cityId=getCityId();

                    if(StringUtil.isNotNull(cityId)){
                        activitiesBeanList = activitiesDBService.getActivitiesByParam(cId+"",cityId);
                    }

                }

                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(String response, int id) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        JSONObject metaArray = jsonObject.optJSONObject("meta");
                        JSONObject paggeArray= metaArray.optJSONObject("pagination");

                        Type pageType = new TypeToken<PaginationBean>() {}.getType();
                        PaginationBean  paginationBean = new Gson().fromJson(paggeArray.toString(), pageType);
                        if(paginationBean!=null){
                            total=Integer.valueOf(paginationBean.getTotal());
                        }

                        Type type = new TypeToken<ArrayList<ActivitiesBean>>() {
                        }.getType();
                        ArrayList<ActivitiesBean> activitiesList = new Gson().fromJson(jsonArray.toString(), type);
                        if(!isLoadMore){
                            //没搜索到，先清空之前的内容
                            activitiesBeanList.clear();
                        }

                        if (activitiesList != null && activitiesList.size() > 0 && activitiesDBService != null) {
                            for (int i = 0; i < activitiesList.size(); i++) {
                                ActivitiesBean activitiesBean = activitiesList.get(i);
                                activitiesBeanList.add(activitiesBean);
                                activitiesDBService.insertActivities(activitiesBean);
                            }

                        }
                        mHandler.sendEmptyMessage(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 切换城市，重新获取活动列表
     */
    public void refreshActivites(){
        pageCount=1;
        isLoadMore=false;
        getActivities();
    }


    private String getCityId(){
        String city=AppPreference.getInstance(mContext).readCurrentCity();
        if(StringUtil.isNotNull(city)){

            return  city.substring(0,city.indexOf(","));
        }

        return null;
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
