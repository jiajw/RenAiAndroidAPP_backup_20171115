package com.yousails.chrenai.person.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.MetaBean;
import com.yousails.chrenai.person.adapter.AddRegisterAdapter;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.person.listener.OnEnrollListener;
import com.yousails.chrenai.person.ui.AddRegistDetailActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.AllowEnrollDialog;
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
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/24.
 */

public class AddRegisterFragment extends Fragment {
    protected View mRootView;
    public Context mContext;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AddRegisterAdapter addRegisterAdapter;
    private List<RegisterInforBean> registerInforList = new ArrayList<RegisterInforBean>();
    private AllowEnrollDialog allowEnrollDialog;
    private RegisterInforBean currentInforBean;
    private OnCallbackListener mCallback;
    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private String passedCount;
    private String appliedCount;
    private String rejectedCount;
    private int pageCount = 1;
    private String url;
    private String cId = "";
    private String status;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCallbackListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_column, container, false);
        }


        findViews();
        handleMessage();
        getRegisterInfor();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findViews() {
        cId = getArguments().getString("cid");
        status = getArguments().getString("status");
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
                        pageCount = 1;
                        isLoadMore = false;
                        getRegisterInfor();
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        addRegisterAdapter = new AddRegisterAdapter(mContext, status, registerInforList);
        addRegisterAdapter.setOnItemClickListener(onEnrollListener);

        recyclerView.setAdapter(addRegisterAdapter);


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

                if (lastVisibleItem + 1 == addRegisterAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                addRegisterAdapter.setFooterSwitch(0);
                            }
                        });

                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        addRegisterAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        addRegisterAdapter.setFooterSwitch(1);
                        getRegisterInfor();
                    }

                }
            }
        });

    }


    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                    case 1:
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);

                        if (registerInforList != null && registerInforList.size() > 0) {
                            addRegisterAdapter.notifyDataChanged(registerInforList);
                            nodataLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > addRegisterAdapter.getItemCount()) {
                                addRegisterAdapter.setFooterSwitch(1);
                            } else {
                                addRegisterAdapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);

                            mCallback.updateTabText(appliedCount,passedCount,rejectedCount);
                        } else {
                            mCallback.updateTabText(appliedCount,passedCount,rejectedCount);
                            nodataLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        };
    }


    /**
     * 追加报名信息
     */
    public void getRegisterInfor(){
        if(status == null){
            return;
        }
        if ("passed".equals(status)||"applied".equals(status)) {
            url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?type=additional&status="+status+"&include=user";
        } else {
            url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?type=additional&status=" + status + "&include=user,operator";
        }

        String token = AppPreference.getInstance(getActivity()).readToken();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("id", cId);
        paramMap.put("status", status);
        paramMap.put("type", "additional");
        if(mCallback!=null){
            paramMap.put("keyword", mCallback.getSearchKey());
        }

        OkHttpUtils.get().url(url).addHeader("Authorization", token).params(paramMap).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                mHandler.sendEmptyMessage(0);
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
                            passedCount = metaBean.getPassed_count();
                            appliedCount = metaBean.getApplied_count();
                            rejectedCount=metaBean.getRejected_count();

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


    public interface OnCallbackListener {

         void updateTabText(String applied, String passed,String rejected);

         String getSearchKey();
    }

    OnEnrollListener onEnrollListener = new OnEnrollListener() {
        @Override
        public void onPassed(RegisterInforBean registerInforBean) {
            currentInforBean = registerInforBean;
            showEnrollDialog();
        }

        @Override
        public void onRejected(RegisterInforBean registerInforBean) {
            currentInforBean = registerInforBean;
            allowEnroll("rejected");
        }

        @Override
        public void OnLongClick(RegisterInforBean registerInforBean) {

        }

        @Override
        public void OnItemClick(RegisterInforBean registerInforBean) {
            Intent intent = new Intent(mContext, AddRegistDetailActivity.class);
            intent.putExtra("registerInfor",registerInforBean);
            startActivityForResult(intent,0);
        }

    };


    /**
     * 同意报名
     */
    private void showEnrollDialog() {
        if (allowEnrollDialog == null) {
            allowEnrollDialog = new AllowEnrollDialog(mContext, onClickListener);
        }
        allowEnrollDialog.show();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.quit_btn:
                    allowEnrollDialog.dismiss();
                    allowEnroll("passed");
                    break;

            }
        }
    };


    /**
     * 同意/删除报名
     */
    private void allowEnroll(String status) {
        if (currentInforBean == null) {
            return;
        }
        String pid = currentInforBean.getActivity_id();
        String id = currentInforBean.getId();

        String url = ApiConstants.GET_COMMENTS_API + pid + "/applications/" + id;
        String token = AppPreference.getInstance(mContext).readToken();

        RequestBody requestBody = new FormBody.Builder()
                .add("status", status)
                .add("pid", pid)
                .add("id", id)
                .build();


        OkHttpUtils.patch().url(url).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.print(response);
                getRegisterInfor();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(1==resultCode){
            getRegisterInfor();
        }

    }
}
