package com.yousails.chrenai.person.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.home.listener.OnItemClickListener;
import com.yousails.chrenai.home.ui.ActivitDetailActivity;
import com.yousails.chrenai.person.adapter.MyActAdapter;
import com.yousails.chrenai.person.ui.MoreOperActivity;
import com.yousails.chrenai.publish.listener.OnPublishClickListener;
import com.yousails.chrenai.publish.ui.PublishActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.ActivityEditDialog;
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
 * Created by liuwen on 2017/8/10.
 */

public class MyEnjoyFragment extends BaseFragment {
    private List<ActivitiesBean> data;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout nodataLayout;

    private RecyclerView recyclerView;
    private MyActAdapter adapter;


    private boolean isFinish = false;
    private int type = 0;
    private List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount=1;

    private String from ="mine";
    private String user = "";

    public MyEnjoyFragment(){}
    @SuppressLint({"NewApi", "ValidFragment"})
    public MyEnjoyFragment(boolean b, int type,String from,String user) {
        isFinish = b;
        this.type = type;
        this.from = from;
        this.user = user;
    }


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_activities, container, false);
        findViews();
        handleMessage();
        return mRootView;
    }

    private void findViews() {

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
                        activitiesBeanList.clear();
                        pageCount=1;
                        isLoadMore=false;
                        getUserActivities();
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        adapter = new MyActAdapter(mContext, activitiesBeanList,type,"");
        adapter.setOnItemClickListener(onClickListener);
        recyclerView.setAdapter(adapter);

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

                if (lastVisibleItem + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if(lastVisibleItem+1>=total){
                        adapter.setFooterSwitch(0);
                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore=true;
                        adapter.setFooterSwitch(1);
                        getUserActivities();
                    }

                }
            }
        });
    }


    @Override
    public void initData() {
        activitiesBeanList.clear();
        getUserActivities();
    }


    private void getUserActivities(){
        if(from.equals("mine")) {
            Map<String, String> params = new HashMap<>();
            params.put("is_finished", isFinish + "");
            params.put("include", "user,userPerms,userApplication.perms");// userPerms   userApplication.perms

            OkHttpUtils.get().url(ApiConstants.USER_ENJOY_ACTIVITIES).addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, String response, Exception e, int id) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);

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
                            JSONObject paggeArray = metaArray.optJSONObject("pagination");

                            Type pageType = new TypeToken<PaginationBean>() {
                            }.getType();
                            PaginationBean paginationBean = new Gson().fromJson(paggeArray.toString(), pageType);
                            if (paginationBean != null) {
                                total = Integer.valueOf(paginationBean.getTotal());
                            }

                            Type type = new TypeToken<ArrayList<ActivitiesBean>>() {
                            }.getType();
                            ArrayList<ActivitiesBean> activitiesList = new Gson().fromJson(jsonArray.toString(), type);
                            if (!isLoadMore) {
                                //没搜索到，先清空之前的内容
                                activitiesBeanList.clear();
                            }

                            if (activitiesList != null && activitiesList.size() > 0) {
                                for (int i = 0; i < activitiesList.size(); i++) {
                                    ActivitiesBean activitiesBean = activitiesList.get(i);
                                    //UserBean userBean=activitiesBean.getUser();
                                    //String name= userBean.getIm_username();
                                    activitiesBeanList.add(activitiesBean);
                                }

                            }
                            mHandler.sendEmptyMessage(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else{
            Map<String, String> params = new HashMap<>();
            params.put("is_finished", isFinish + "");
            params.put("id",user);
            params.put("include", "user,userPerms");

            OkHttpUtils.get().url(ApiConstants.GET_USER+user+"/applied/activities").addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, String response, Exception e, int id) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);

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
                            JSONObject paggeArray = metaArray.optJSONObject("pagination");

                            Type pageType = new TypeToken<PaginationBean>() {
                            }.getType();
                            PaginationBean paginationBean = new Gson().fromJson(paggeArray.toString(), pageType);
                            if (paginationBean != null) {
                                total = Integer.valueOf(paginationBean.getTotal());
                            }

                            Type type = new TypeToken<ArrayList<ActivitiesBean>>() {
                            }.getType();
                            ArrayList<ActivitiesBean> activitiesList = new Gson().fromJson(jsonArray.toString(), type);
                            if (!isLoadMore) {
                                //没搜索到，先清空之前的内容
                                activitiesBeanList.clear();
                            }

                            if (activitiesList != null && activitiesList.size() > 0) {
                                for (int i = 0; i < activitiesList.size(); i++) {
                                    ActivitiesBean activitiesBean = activitiesList.get(i);
                                    //UserBean userBean=activitiesBean.getUser();
                                    //String name= userBean.getIm_username();
                                    activitiesBeanList.add(activitiesBean);
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
                            adapter.notifyDataChanged(activitiesBeanList);
                            recyclerView.setVisibility(View.VISIBLE);
                            nodataLayout.setVisibility(View.GONE);
                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if(total>adapter.getItemCount()){
                                adapter.setFooterSwitch(1);
                            }else{
                                adapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodataLayout.setVisibility(View.VISIBLE);
                        }

                        break;

                }
            }
        };
    }


    private ActivityEditDialog mDialog;

    OnPublishClickListener onClickListener =new OnPublishClickListener(){
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
        public void editor(final ActivitiesBean activitiesBean) {
            //PublishActivity.launchWithActivity(mContext,activitiesBean);
            if(mDialog==null){
                mDialog = new ActivityEditDialog(mContext);
                mDialog.show();
                mDialog.setOnEditDialogListener(new ActivityEditDialog.OnEditDialogListener() {
                    @Override
                    public void onEdit() {
                        PublishActivity.launchWithActivity(mContext,activitiesBean);
                        mDialog.dismiss();
                        mDialog = null;
                    }

                    @Override
                    public void onDelete() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                });
            }else {
                mDialog.show();
            }
        }

        @Override
        public void entrustManage(ActivitiesBean activitiesBean) {

        }

        @Override
        public void doMore(ActivitiesBean activitiesBean) {
            Intent intent=new Intent(getActivity(), MoreOperActivity.class);
            intent.putExtra("cid",activitiesBean.getId());
            intent.putExtra("title",activitiesBean.getTitle());
            intent.putExtra("chargeable",activitiesBean.is_chargeable());
            intent.putExtra("from","enjoy");
            intent.putExtra("activity",activitiesBean);
            mContext.startActivity(intent);
        }
    };
}
