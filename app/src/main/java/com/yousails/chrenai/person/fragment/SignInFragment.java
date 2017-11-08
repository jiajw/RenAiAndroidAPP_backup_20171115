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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.person.adapter.SignInAdapter;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.person.bean.SignInMetaBean;
import com.yousails.chrenai.person.listener.OnSignInListener;
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
 * Created by Administrator on 2017/8/23.
 */

public class SignInFragment extends Fragment {
    protected View mRootView;
    public Context mContext;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SignInAdapter signInAdapter;
    private List<RegisterInforBean> registerInforList = new ArrayList<RegisterInforBean>();
    private OnCallbackListener mCallback;
    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private String reachedCount;
    private String unreadchedCount;
    private String leftCount;
    private int pageCount = 1;
    private String cId = "";
    private String sign_type;
    private String status;//到达状态: reached 已到达，unreached 未到达，left 已离开



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



    private void findViews() {
        cId = getArguments().getString("cid");
        status = getArguments().getString("status");
        sign_type= getArguments().getString("sign_type");
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

        signInAdapter = new SignInAdapter(mContext, registerInforList);
        signInAdapter.setOnItemClickListener(onSignInListener);
        recyclerView.setAdapter(signInAdapter);


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

                if (lastVisibleItem + 1 == signInAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                signInAdapter.setFooterSwitch(0);
                            }
                        });

                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        signInAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        signInAdapter.setFooterSwitch(1);
                        getRegisterInfor();
                    }

                }
            }
        });

    }


    /**
     * 设置headView
     */
    private void setHeader(RecyclerView view) {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.sign_in_head, view, false);
        signInAdapter.setHeaderView(headerView,status,reachedCount,leftCount);
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

                        if (registerInforList != null && registerInforList.size() > 0) {

                            if("twice".equals(sign_type)&&(!"unreached".equals(status))){
                                setHeader(recyclerView);
                            }

                            signInAdapter.notifyDataChanged(registerInforList);
                            nodataLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > signInAdapter.getItemCount()) {
                                signInAdapter.setFooterSwitch(1);
                            } else {
                                signInAdapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);

                            mCallback.updateTabText(reachedCount, unreadchedCount);

                        } else {
                            mCallback.updateTabText(reachedCount, unreadchedCount);
                            if("twice".equals(sign_type)&&(!"unreached".equals(status))){
                                setHeader(recyclerView);

                                signInAdapter.notifyDataChanged(registerInforList);
                                nodataLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }else{
                                nodataLayout.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                        }
                        break;

                }
            }
        };
    }


    /**
     * 签到工时列表
     */
    public void getRegisterInfor() {
        String url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?include=user&type="+status;

        String token = AppPreference.getInstance(getActivity()).readToken();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("id", cId);
        paramMap.put("type", status);
        if(mCallback!=null){
            paramMap.put("keyword", mCallback.getSearchKey());
        }

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

                        Type pageType = new TypeToken<SignInMetaBean>() {
                        }.getType();
                        SignInMetaBean metaBean = new Gson().fromJson(metaArray.toString(), pageType);
                        if (metaBean != null) {
                            total = Integer.valueOf(metaBean.getPagination().getTotal());
                            reachedCount = metaBean.getReached_count();
                            unreadchedCount = metaBean.getUnreached_count();
                            leftCount=metaBean.getLeft_count();
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
                        System.out.print(e);
                        e.printStackTrace();
                    }
                }
            }
        });
    }




    public interface OnCallbackListener {

        void updateTabText(String reachedCount, String unreadchedCount);

        String getSearchKey();
    }


    OnSignInListener onSignInListener=new OnSignInListener() {
        @Override
        public void onReached() {
            status="reached";
            getRegisterInfor();
        }

        @Override
        public void onLeft() {
            status="left";
            getRegisterInfor();
        }
    };
}
