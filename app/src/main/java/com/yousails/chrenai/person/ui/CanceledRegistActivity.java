package com.yousails.chrenai.person.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.MetaBean;
import com.yousails.chrenai.person.adapter.CanceledRegistAdapter;
import com.yousails.chrenai.person.bean.RegisterInforBean;
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
 * Created by Administrator on 2017/8/24.
 */

public class CanceledRegistActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private List<RegisterInforBean> registerInforList = new ArrayList<RegisterInforBean>();
    private CanceledRegistAdapter canceledRegistAdapter;

    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount = 1;
    private String cId = "";


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_canceled_register);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void init() {
        cId = getIntent().getStringExtra("cid");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("取消报名");


        nodataLayout = (LinearLayout)findViewById(R.id.nodata_layout);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
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
                        pageCount = 1;
                        isLoadMore = false;
                        getCanceledRegistList();
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        canceledRegistAdapter = new CanceledRegistAdapter(mContext, registerInforList);

        recyclerView.setAdapter(canceledRegistAdapter);


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem + 1 == canceledRegistAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                canceledRegistAdapter.setFooterSwitch(0);
                            }
                        });

                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        canceledRegistAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        canceledRegistAdapter.setFooterSwitch(1);
                        getCanceledRegistList();
                    }

                }
            }
        });
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
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);

                        if (registerInforList != null && registerInforList.size() > 0) {
                            canceledRegistAdapter.notifyDataChanged(registerInforList);
                            nodataLayout.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > canceledRegistAdapter.getItemCount()) {
                                canceledRegistAdapter.setFooterSwitch(1);
                            } else {
                                canceledRegistAdapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);

                        } else {

                            nodataLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {
        getCanceledRegistList();
    }

    /**
     * 获取 取消的报名列表
     */
    private void getCanceledRegistList(){
        if(!NetUtil.detectAvailable(mContext)){
            Toast.makeText(mContext,"请链接网络",Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?status=canceled&include=user";

        String token = AppPreference.getInstance(mContext).readToken();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("id", cId);
        paramMap.put("status", "canceled");

        OkHttpUtils.get().url(url).addHeader("Authorization", token).params(paramMap).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
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

                        Type pageType = new TypeToken<MetaBean>() {
                        }.getType();
                        MetaBean metaBean = new Gson().fromJson(metaArray.toString(), pageType);
                        if (metaBean != null) {
                            total = Integer.valueOf(metaBean.getPagination().getTotal());
                        }

                        Type type = new TypeToken<ArrayList<RegisterInforBean>>() {
                        }.getType();
                        ArrayList<RegisterInforBean> regInforList = new Gson().fromJson(jsonArray.toString(), type);
                        if (!isLoadMore) {
                            //没搜索到，先清空之前的内容
                            registerInforList.clear();
                        }

                        if (regInforList != null && regInforList.size() > 0) {
                            registerInforList.addAll(regInforList);
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
