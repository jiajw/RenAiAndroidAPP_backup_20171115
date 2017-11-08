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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.person.adapter.EntrustedAdapter;
import com.yousails.chrenai.person.bean.EntrustedMetaBean;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.person.listener.OnEntrustedListener;
import com.yousails.chrenai.person.ui.EntrusteSettingActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CancelEntrustDialog;
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

public class EntrustedFragment extends Fragment {
    protected View mRootView;
    public Context mContext;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private EntrustedAdapter entrustedAdapter;
    private List<RegisterInforBean> registerInforList = new ArrayList<RegisterInforBean>();
    private RegisterInforBean registInforBean;
    private OnCallbackListener mCallback;
    private CancelEntrustDialog cancelEntrustDialog;

    private Handler mHandler;
    private boolean isLoading;
    private boolean isLoadMore;

    private int total;

    private String entrustedCount;
    private String notEntrustedCount;

    private int pageCount = 1;
    private String cId = "";
    private boolean is_chargeable;
    private String type;


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
        getEntrusted();
        return mRootView;
    }

    private void findViews() {
        cId = getArguments().getString("cid");
        is_chargeable=getArguments().getBoolean("is_chargeable",false);
        type = getArguments().getString("type");

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
                        getEntrusted();
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        entrustedAdapter = new EntrustedAdapter(mContext,type, registerInforList);
        entrustedAdapter.setOnItemClickListener(onPaymentListener);

        recyclerView.setAdapter(entrustedAdapter);


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

                if (lastVisibleItem + 1 == entrustedAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                entrustedAdapter.setFooterSwitch(0);
                            }
                        });

                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        entrustedAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        entrustedAdapter.setFooterSwitch(1);
                        getEntrusted();
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
                            entrustedAdapter.notifyDataChanged(registerInforList);
                            nodataLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > entrustedAdapter.getItemCount()) {
                                entrustedAdapter.setFooterSwitch(1);
                            } else {
                                entrustedAdapter.setFooterSwitch(0);
                            }
                            recyclerView.scrollToPosition(0);

                            mCallback.updateTabText(entrustedCount, notEntrustedCount);

                        } else {
                            mCallback.updateTabText(entrustedCount, notEntrustedCount);
                            nodataLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        };
    }


    /**
     * 获取委托列表数据
     */
    public void getEntrusted() {

        String url = ApiConstants.GET_ACTIVITIES_API + "/" + cId + "/applications?type=" + type + "&include=user";

        String token = AppPreference.getInstance(getActivity()).readToken();
        Map<String, String> paramMap = new HashMap<String, String>();
//        paramMap.put("id", cId);
        paramMap.put("type", type);
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

                        Type pageType = new TypeToken<EntrustedMetaBean>() {
                        }.getType();
                        EntrustedMetaBean metaBean = new Gson().fromJson(metaArray.toString(), pageType);
                        if (metaBean != null) {
                            total = Integer.valueOf(metaBean.getPagination().getTotal());

                            entrustedCount = metaBean.getEntrusted_count();
                            notEntrustedCount = metaBean.getNot_entrusted_count();

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

    OnEntrustedListener onPaymentListener = new OnEntrustedListener() {
        @Override
        public void onEntrusted(RegisterInforBean registerInforBean, String type) {
            registInforBean=registerInforBean;
            // type:  entrust/editor/cancel
            if("entrust".equals(type)){
                startActivity(registerInforBean,type,EntrusteSettingActivity.class);
            }else if("editor".equals(type)){
                startActivity(registerInforBean,type,EntrusteSettingActivity.class);
            }else if("cancel".equals(type)){
                showDialog();
            }
        }


    };


    public interface OnCallbackListener {

        void updateTabText(String entrustedCount, String notEntrustedCount);

        String getSearchKey();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == resultCode) {
            getEntrusted();
        }
    }

    /**
     * 跳转
     */
    private void startActivity(RegisterInforBean registBean,String type,Class<?> clz) {
            Intent intent = new Intent(mContext, clz);
            intent.putExtra("type",type);
            intent.putExtra("is_chargeable",is_chargeable);
            intent.putExtra("registerInfor", registBean);
            startActivityForResult(intent, 0);
    }


    /**
     * 取消委托确认对话框
     */
    private void showDialog(){
        if(cancelEntrustDialog==null){
            cancelEntrustDialog=new CancelEntrustDialog(mContext,onClickListener);
        }
        cancelEntrustDialog.show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel_btn:
                    cancelEntrustDialog.dismiss();
                    rescindEntrustment();
                    break;
            }
        }
    };


    /**
     * 取消委托
     */
    private void rescindEntrustment(){
        String token = AppPreference.getInstance(getActivity()).readToken();
        String url=ApiConstants.GET_COMMENTS_API+cId+"/entrusted/applications/"+registInforBean.getId();
        OkHttpUtils.delete().url(url).addHeader("Authorization",token).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
                Toast.makeText(mContext,"取消委托失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.print(response);
                Toast.makeText(mContext,"取消委托成功",Toast.LENGTH_SHORT).show();
                getEntrusted();
            }
        });
    }


}
