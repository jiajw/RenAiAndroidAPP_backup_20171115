package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.person.adapter.FeedbackAdapter;
import com.yousails.chrenai.person.bean.FeedbackBean;
import com.yousails.chrenai.person.listener.OnFeedbackClickListener;
import com.yousails.chrenai.utils.CustomToast;
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
import java.util.Map;

import okhttp3.Call;

/**
*@Author :liuwen
*@Date :2017/8/29
*@Des :意见反馈
*@version :
*/

public class ActivityFeedbackActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;
    private EditText commentEditView;
    private Button releaseBtn;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;
    private ArrayList<FeedbackBean> feedbackList = new ArrayList<>();

    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount = 1;
    private String aId;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_act_feedback);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        if (intent != null) {
            aId = intent.getStringExtra("id");
        }
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("意见反馈");

        commentEditView = (EditText) findViewById(R.id.et_comment);
        releaseBtn = (Button) findViewById(R.id.btn_release);

        nodataLayout = (LinearLayout) findViewById(R.id.nodata_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
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
                        //pageCount = 1;
                        //isLoadMore = false;
                        getFeedbackList();
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        feedbackAdapter = new FeedbackAdapter(mContext, feedbackList);
        feedbackAdapter.setOnItemClickListener(new OnFeedbackClickListener() {
            @Override
            public void onItemClick(FeedbackBean bean) {
                startActivity(new Intent(ActivityFeedbackActivity.this,ReplyActivity.class).putExtra("id",bean.getActivity_id()).putExtra("feedback",bean));
            }

            @Override
            public void delComment(FeedbackBean bean) {

            }
        });
        recyclerView.setAdapter(feedbackAdapter);
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
        getFeedbackList();
    }


    private void getFeedbackList() {

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(aId)) {
            return;
        }

        String url = ApiConstants.BASE_ACTIVITY_API + aId + "/feedbacks";
        String mToken= AppPreference.getInstance(mContext).readToken();

        Map<String, String> params = new HashMap<>();
        params.put("include", "user");

        OkHttpUtils.get().url(url).addHeader("Authorization",mToken).params(params).build().execute(new StringCallback() {
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

                        Type type = new TypeToken<ArrayList<FeedbackBean>>() {
                        }.getType();
                        ArrayList<FeedbackBean> commentList = new Gson().fromJson(jsonArray.toString(), type);
                        if (!isLoadMore) {
                            feedbackList.clear();
                        }
                        if (commentList != null && commentList.size() > 0) {

                            for (FeedbackBean bean : commentList) {
                                feedbackList.add(bean);
                            }
                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        mHandler.sendEmptyMessage(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (feedbackList != null && feedbackList.size() > 0) {
                        feedbackAdapter.notifyDataChanged(feedbackList);
                        nodataLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                        if (total > feedbackAdapter.getItemCount()) {
                            feedbackAdapter.setFooterSwitch(1);
                        } else {
                            feedbackAdapter.setFooterSwitch(0);
                        }
                        if (!isLoadMore) {
                            recyclerView.scrollToPosition(0);
                        }
                    } else {
                        nodataLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                    break;
                case 3:
                    CustomToast.createToast(mContext, "删除成功");
                    pageCount = 1;
                    isLoadMore = false;
                    getFeedbackList();
                    break;

            }
        }
    };
}
