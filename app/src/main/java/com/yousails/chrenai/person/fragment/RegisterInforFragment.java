package com.yousails.chrenai.person.fragment;

import android.content.Context;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.MetaBean;
import com.yousails.chrenai.person.adapter.RegisterInforAdapter;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.person.listener.OnEnrollListener;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.AllowEnrollDialog;
import com.yousails.chrenai.view.DeletedEnrollDialog;
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
 * 活动管理报名信息-全部报名信息
 * Created by Administrator on 2017/8/23.
 */

public class RegisterInforFragment extends Fragment {
    protected View mRootView;
    public Context mContext;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RegisterInforAdapter registerInforAdapter;
    private List<RegisterInforBean> registerInforList = new ArrayList<RegisterInforBean>();
    private AllowEnrollDialog allowEnrollDialog;
    private DeletedEnrollDialog deletedEnrollDialog;
    private RegisterInforBean currentInforBean;
    private OnCallbackListener mCallback;
    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private String passedCount;
    private String deletedCount;
    private int pageCount = 1;
    private String url;
    private String cId = "";
    private String status;


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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCallbackListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

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

        registerInforAdapter = new RegisterInforAdapter(mContext, status, registerInforList);
        registerInforAdapter.setOnItemClickListener(onEnrollListener);

        recyclerView.setAdapter(registerInforAdapter);


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

                if (lastVisibleItem + 1 == registerInforAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                registerInforAdapter.setFooterSwitch(0);
                            }
                        });

                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        registerInforAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        registerInforAdapter.setFooterSwitch(1);
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
                            registerInforAdapter.notifyDataChanged(registerInforList);
                            nodataLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > registerInforAdapter.getItemCount()) {
                                registerInforAdapter.setFooterSwitch(1);
                            } else {
                                registerInforAdapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);
                            mCallback.updateTabText(passedCount,deletedCount);
                        } else {
                            mCallback.updateTabText(passedCount,deletedCount);
                            nodataLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        };
    }



    /**
     * 报名信息
     */
    public void getRegisterInfor() {

        if (status == null || "passed".equals(status)) {
            url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?include=user";
        } else {
            url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?status=" + status + "&include=user,operator";
        }

        String token = AppPreference.getInstance(getActivity()).readToken();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("id", cId);
        paramMap.put("status", status);
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

                    LogUtil.e("==response=="+response);
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
                            deletedCount = metaBean.getDeleted_count();

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

    OnEnrollListener onEnrollListener = new OnEnrollListener() {
        @Override
        public void onPassed(RegisterInforBean registerInforBean) {
            currentInforBean = registerInforBean;
            showEnrollDialog();
        }

        @Override
        public void onRejected(RegisterInforBean registerInforBean) {

        }

        @Override
        public void OnLongClick(RegisterInforBean registerInforBean) {
            currentInforBean = registerInforBean;
            String uid= currentInforBean.getUser_id();
            String currentId=AppPreference.getInstance(mContext).readUerId();
            if(uid.equals(currentId)){
                Toast.makeText(mContext,"您不可以删除您自己",Toast.LENGTH_SHORT).show();
            }else{
                deletedDialog();
            }

        }

        @Override
        public void OnItemClick(RegisterInforBean registerInforBean) {

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
     * 删除报名者
     */
    private void deletedDialog() {
        if (deletedEnrollDialog == null) {
            deletedEnrollDialog = new DeletedEnrollDialog(mContext, onDeletedListener);
        }
        deletedEnrollDialog.show();
    }

    View.OnClickListener onDeletedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.quit_btn:
                    deletedEnrollDialog.dismiss();
                    allowEnroll("deleted");
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
            }

            @Override
            public void onResponse(String response, int id) {
                getRegisterInfor();
            }
        });
    }


    public interface OnCallbackListener {

        void updateTabText(String passedCount,String deletedCount);

        String getSearchKey();

    }


}
