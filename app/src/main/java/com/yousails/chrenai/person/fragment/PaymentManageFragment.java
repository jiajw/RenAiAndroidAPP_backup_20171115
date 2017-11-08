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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.person.adapter.PaymentManageAdapter;
import com.yousails.chrenai.person.bean.PaymentMetaBean;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.person.listener.OnPaymentListener;
import com.yousails.chrenai.person.ui.PaymentActiviity;
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

public class PaymentManageFragment extends Fragment {
    protected View mRootView;
    public Context mContext;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PaymentManageAdapter paymentManageAdapter;
    private List<RegisterInforBean> registerInforList = new ArrayList<RegisterInforBean>();
    private OnCallbackListener mCallback;

    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;

    private int total;
    private String paymentCount;
    private String nopaymentCount;
    private String refoundCount;
    private String amount;

    private int pageCount = 1;
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

        paymentManageAdapter = new PaymentManageAdapter(mContext, registerInforList);
        paymentManageAdapter.setOnItemClickListener(onPaymentListener);

        recyclerView.setAdapter(paymentManageAdapter);


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

                if (lastVisibleItem + 1 == paymentManageAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                paymentManageAdapter.setFooterSwitch(0);
                            }
                        });

                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        paymentManageAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        paymentManageAdapter.setFooterSwitch(1);
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
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.payment_head, view, false);
        TextView paymentView=(TextView)headerView.findViewById(R.id.payment_tview);
        paymentView.setText("全部缴费:"+ getPayments(amount)+"元");

        paymentManageAdapter.setHeaderView(headerView);
    }


    private String getPayments(String payments){
        if(StringUtil.isNotNull(payments)){
            if(payments.indexOf(".")!=-1){
                return payments.substring(0,payments.indexOf("."));
            }
        }
        return"0";
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
                            if("payPayments".equals(status)){
                                setHeader(recyclerView);
                            }
                            paymentManageAdapter.notifyDataChanged(registerInforList);
                            nodataLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > paymentManageAdapter.getItemCount()) {
                                paymentManageAdapter.setFooterSwitch(1);
                            } else {
                                paymentManageAdapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);

                            // 更改无付款、付款、退款的数量
                            mCallback.updateTabText(paymentCount,nopaymentCount,refoundCount);

                        } else {
                            // 更改无付款、付款、退款的数量
                            mCallback.updateTabText( paymentCount, nopaymentCount,refoundCount);
                            nodataLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        };
    }


    /**
     * 缴费情况
     */
    public void getRegisterInfor() {

        String url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?type=" + status + "&include=user";

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

                        Type pageType = new TypeToken<PaymentMetaBean>() {
                        }.getType();
                        PaymentMetaBean metaBean = new Gson().fromJson(metaArray.toString(), pageType);
                        if (metaBean != null) {
                            total = Integer.valueOf(metaBean.getPagination().getTotal());
                            paymentCount = metaBean.getPay_payments_count();
                            nopaymentCount= metaBean.getNo_payments_count();
                            refoundCount=metaBean.getRefound_payments_count();
                            amount=metaBean.getAmount();
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

    OnPaymentListener onPaymentListener = new OnPaymentListener() {
        @Override
        public void onPayment(RegisterInforBean registerInforBean,String type) {
            Intent intent = new Intent(mContext, PaymentActiviity.class);
            intent.putExtra("registerInforBean",registerInforBean);
            intent.putExtra("type",type);
            startActivityForResult(intent,0);
        }


    };


    public interface OnCallbackListener {

        void updateTabText(String paymentCount, String nopaymentCount, String refoundCount);

        String getSearchKey();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(1==resultCode){
            getRegisterInfor();
        }
    }
}
