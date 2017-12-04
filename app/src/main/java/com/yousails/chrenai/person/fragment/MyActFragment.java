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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.db.ActivitiesDBService;
import com.yousails.chrenai.home.adapter.ColumnAdapter;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.home.listener.OnItemClickListener;
import com.yousails.chrenai.home.ui.ActivitDetailActivity;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.person.ActDelEvent;
import com.yousails.chrenai.person.adapter.MyActAdapter;
import com.yousails.chrenai.person.ui.ActDelReasonActivity;
import com.yousails.chrenai.person.ui.EntrustedActivity;
import com.yousails.chrenai.person.ui.MoreOperActivity;
import com.yousails.chrenai.publish.listener.OnPublishClickListener;
import com.yousails.chrenai.publish.ui.PublishActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.ActivityEditDialog;
import com.yousails.chrenai.view.WrapContentLinearLayoutManager;
import com.yousails.chrenai.view.YSDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.RequestBody;

import static com.yousails.chrenai.home.ui.EnrollActivity.JSON;

/**
 * 报名信息——全部
 * Created by liuwen on 2017/8/10.
 */


public class MyActFragment extends BaseFragment {
    private List<ActivitiesBean> data;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout nodataLayout;

    private RecyclerView recyclerView;
    private MyActAdapter adapter;


    private boolean isFinish = false;
    private String from = "";
    private String user = "";
    private int type = 0;
    private List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount = 1;


    public MyActFragment() {
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public MyActFragment(boolean b, int type, String from, String user) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(ActDelEvent event) {
        getUserActivities();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void findViews() {
        EventBus.getDefault().register(this);

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

                LogUtil.e("==onRefresh==");

                activitiesBeanList.clear();
                pageCount = 1;
                isLoadMore = false;
                getUserActivities();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        activitiesBeanList.clear();
//                        pageCount = 1;
//                        isLoadMore = false;
//                        getUserActivities();
//                    }
//                }).start();
            }
        });

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        adapter = new MyActAdapter(mContext, activitiesBeanList, type, from);
        adapter.setOnItemClickListener(onClickListener);
        recyclerView.setAdapter(adapter);

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

                if (lastVisibleItem + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
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
                        isLoadMore = true;
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


    private void getUserActivities() {
        if (from.equals("mine")) {
            Map<String, String> params = new HashMap<>();
            params.put("is_finished", isFinish + "");
            params.put("include", "user,category");//,userPerms

            OkHttpUtils.get().url(ApiConstants.USER_ACTIVITIES).addHeader("Authorization", AppPreference.getInstance(mContext).readToken()).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, String response, Exception e, int id) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);

                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e("user.activites", response);
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    if (StringUtil.isNotNull(response)) {
                        try {

                            LogUtil.e("===response==" + response);

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
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("is_finished", isFinish + "");
            params.put("id", user);
            params.put("include", "user");//,userPerms


            OkHttpUtils.get().url(ApiConstants.GET_USER + user + "/activities").params(params).build().execute(new StringCallback() {
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
                            if (total > adapter.getItemCount()) {
                                adapter.setFooterSwitch(1);
                            } else {
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
    OnPublishClickListener onClickListener = new OnPublishClickListener() {
        @Override
        public void onItemClick(ActivitiesBean activitiesBean) {
            String longitude = AppPreference.getInstance(mContext).readLongitude();
            String latitude = AppPreference.getInstance(mContext).readLatitude();
            boolean isLogin = AppPreference.getInstance(mContext).readLogin();
            Intent intent = new Intent(mContext, ActivitDetailActivity.class);
            intent.putExtra("bean", activitiesBean);
            String url = "";
            if (isLogin) {
                url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?token=" + AppPreference.getInstance(mContext).readToken() + "&user_coordinate=" + longitude + "," + latitude;
            } else {
                url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?user_coordinate=" + longitude + "," + latitude;
            }

            intent.putExtra("url", url);
            mContext.startActivity(intent);
        }

        @Override
        public void editor(final ActivitiesBean activitiesBean) {
            //PublishActivity.launchWithActivity(mContext,activitiesBean);
            if (mDialog == null) {
                mDialog = new ActivityEditDialog(mContext);
            }
            mDialog.show();
            mDialog.setOnEditDialogListener(new ActivityEditDialog.OnEditDialogListener() {
                @Override
                public void onEdit() {
//                        PublishActivity.launchWithActivity(mContext,activitiesBean);

                    Intent intent = new Intent(mContext, PublishActivity.class);
                    intent.putExtra("activity", activitiesBean);
                    mContext.startActivity(intent);

                    mDialog.dismiss();
                    mDialog = null;
                }

                @Override
                public void onDelete() {
                    final YSDialog deleteDialog = new YSDialog(mContext);
                    deleteDialog.show();
                    deleteDialog.setTitle("确认要删除吗？");
                    deleteDialog.setConfirm("确认删除");
                    deleteDialog.setCancel("先不删除");
                    deleteDialog.setOnYsDialogListenter(new YSDialog.YsDialogListener() {
                        @Override
                        public void onConfirm() {
                            deleteDialog.dismiss();
                            //deleteActivity(activitiesBean);
                            mDialog.dismiss();
                            mDialog = null;
                            startActivity(new Intent(mContext, ActDelReasonActivity.class).putExtra("activity", activitiesBean));
                        }

                        @Override
                        public void onCancel() {
                            deleteDialog.dismiss();
                            mDialog.dismiss();
                            mDialog = null;
                        }
                    });

                }

                @Override
                public void onFinish() {
                    final YSDialog finishDialog = new YSDialog(mContext);
                    finishDialog.show();
                    finishDialog.setTitle("确认要结束吗？");
                    finishDialog.setConfirm("确认结束");
                    finishDialog.setCancel("先不结束");
                    finishDialog.setOnYsDialogListenter(new YSDialog.YsDialogListener() {
                        @Override
                        public void onConfirm() {
                            finishDialog.dismiss();
                            finishActivity(activitiesBean);
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        @Override
                        public void onCancel() {
                            finishDialog.dismiss();
                            mDialog.dismiss();
                            mDialog = null;
                        }
                    });
                }

                @Override
                public void onCancel() {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });

        }

        @Override
        public void entrustManage(ActivitiesBean activitiesBean) {
            Intent intent = new Intent(getActivity(), EntrustedActivity.class);
            intent.putExtra("cid", activitiesBean.getId());
            intent.putExtra("is_chargeable", activitiesBean.is_chargeable());
            mContext.startActivity(intent);
        }

        @Override
        public void doMore(ActivitiesBean activitiesBean) {
            Intent intent = new Intent(getActivity(), MoreOperActivity.class);
            intent.putExtra("cid", activitiesBean.getId());
            intent.putExtra("title", activitiesBean.getTitle());
            intent.putExtra("chargeable", activitiesBean.is_chargeable());
            intent.putExtra("from", "publish");
            intent.putExtra("activity", activitiesBean);
            mContext.startActivity(intent);
        }
    };

    private void deleteActivity(ActivitiesBean bean) {
        String token = AppPreference.getInstance(mContext).readToken();
        Map<String, String> params = new HashMap<>();
        params.put("reason", "1");
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(params));


        OkHttpUtils.delete().url(ApiConstants.BASE_ACTIVITY_API + bean.getId()).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        getUserActivities();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void finishActivity(ActivitiesBean bean) {
        String token = AppPreference.getInstance(mContext).readToken();
        Map<String, String> params = new HashMap<>();
        params.put("is_finished", "1");
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(params));

        OkHttpUtils.patch().url(ApiConstants.BASE_ACTIVITY_API + bean.getId()).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        getUserActivities();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
