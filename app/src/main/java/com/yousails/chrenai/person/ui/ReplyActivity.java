package com.yousails.chrenai.person.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.login.ui.LoginActivity;
import com.yousails.chrenai.person.adapter.FeedbackAdapter;
import com.yousails.chrenai.person.bean.FeedbackBean;
import com.yousails.chrenai.person.listener.OnFeedbackClickListener;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CircleImageView;
import com.yousails.chrenai.view.WrapContentLinearLayoutManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.yousails.chrenai.home.ui.EnrollActivity.JSON;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ReplyActivity extends BaseActivity {
    private LinearLayout backLayout;
    private LinearLayout llContainer;
    private RelativeLayout delLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;
    private EditText commentEditView;
    private Button releaseBtn;


    private CircleImageView ivAvatar;
    private TextView tvName;
    private TextView tvContent;
    private TextView tvTime;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount = 1;
    private String commentText;
    private String aid;
    private FeedbackBean feedbackBean;
    private FeedbackAdapter replyAdapter;
    private ArrayList<FeedbackBean> replyList = new ArrayList<>();


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_feedback_reply);
    }

    @Override
    protected void init() {

        Intent intent = getIntent();
        if (intent != null) {
            aid = intent.getStringExtra("id");
            feedbackBean = (FeedbackBean) intent.getSerializableExtra("feedback");
            if(feedbackBean!=null){

            }
        }
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        delLayout = (RelativeLayout) findViewById(R.id.del_layout);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        ivAvatar = (CircleImageView) findViewById(R.id.iv_head) ;
        tvName = (TextView) findViewById(R.id.tv_name);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvTime = (TextView) findViewById(R.id.tv_create_time);
        if(feedbackBean!=null){
            String headUrl = feedbackBean.getUser().getAvatar();
            String sex = feedbackBean.getUser().getSex();
            if (StringUtil.isNotNull(headUrl)) {
                Glide.with(mContext).load(headUrl).into(ivAvatar);
            } else {
                if (StringUtil.isNotNull(sex)) {
                    if ("female".equals(sex)) {
                        ivAvatar.setImageResource(R.drawable.ic_avatar_woman);
                    } else {
                        ivAvatar.setImageResource(R.drawable.ic_avatar);
                    }
                }
            }
            tvName.setText(feedbackBean.getUser().getName());
            tvContent.setText(feedbackBean.getContent());
            tvTime.setText(feedbackBean.getCreated_at());
            if(feedbackBean.getReply_count()>0){
                titleView.setText(feedbackBean.getReply_count() + "条回复");
            }else{
                titleView.setText("意见反馈");
            }
            if(StringUtil.isNotNull(AppPreference.getInstance(mContext).readUerId())&&AppPreference.getInstance(mContext).readUerId().equals(""+feedbackBean.getUser_id())){
                delLayout.setVisibility(View.VISIBLE);
            }else{
                delLayout.setVisibility(View.GONE);
            }

        }else{
            llContainer.setVisibility(View.GONE);
            titleView.setText("意见反馈");
        }

        commentEditView = (EditText) findViewById(R.id.et_comment);
        releaseBtn = (Button) findViewById(R.id.btn_release);


        nodataLayout = (LinearLayout) findViewById(R.id.nodata_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#0099ff"));

        if(feedbackBean!=null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pageCount = 1;
                        isLoadMore = false;
                        getReply();
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        replyAdapter = new FeedbackAdapter(mContext, replyList);
        replyAdapter.setOnItemClickListener(new OnFeedbackClickListener() {
            @Override
            public void onItemClick(FeedbackBean bean) {
                //showKeyboard();
            }

            @Override
            public void delComment(FeedbackBean bean) {
                deleteComment(bean,true);
            }
        });
        recyclerView.setAdapter(replyAdapter);

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

                if (lastVisibleItem + 1 == replyAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        replyAdapter.setFooterSwitch(0);
                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        replyAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        replyAdapter.setFooterSwitch(1);
                        getReply();
                    }

                }

            }
        });

        if(feedbackBean!=null){
            read();
        }
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        releaseBtn.setOnClickListener(this);
        commentEditView.addTextChangedListener(commentTextWatcher);
        delLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_release:
                boolean isLogin = AppPreference.getInstance(mContext).readLogin();
                if (isLogin) {
                    commentText = commentEditView.getText().toString().trim();
                    if (StringUtil.isNotNull(commentText)) {
                        if(feedbackBean==null){
                            post(commentText);
                        }else{
                            replyComment(commentText);
                        }

                    }
                } else {
                   // Toast.makeText(mContext, "请先登录!", Toast.LENGTH_SHORT).show();
                    navToLogin();
                }

                break;
            case R.id.del_layout:
                deleteComment(feedbackBean,false);
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
                        if (0 != total) {
                            titleView.setText(total + "条回复");
                        }

                        if (replyList != null && replyList.size() > 0) {
                            replyAdapter.notifyDataChanged(replyList);
                            nodataLayout.setVisibility(View.GONE);
                            llContainer.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > replyAdapter.getItemCount()) {
                                replyAdapter.setFooterSwitch(1);
                            } else {
                                replyAdapter.setFooterSwitch(0);
                            }
                            if (!isLoadMore) {
                                recyclerView.scrollToPosition(0);
                            }
                        } else {
                            nodataLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        break;
                    case 2:
                        CustomToast.createToast(mContext, "发布成功");
                        //隐藏键盘
                        hiddenKeyboard();
                        releaseBtn.setTextColor(ContextCompat.getColor(mContext, R.color.line));
                        releaseBtn.setEnabled(false);
                        commentEditView.setText("");

                        pageCount = 1;
                        isLoadMore = false;
                        getReply();
                        break;
                    case 3:
                        //隐藏键盘
                        hiddenKeyboard();
                        if(feedbackBean!=null){
                            titleView.setText(total-1==0?"意见反馈":total + "条回复");
                            if(StringUtil.isNotNull(AppPreference.getInstance(mContext).readUerId())&&AppPreference.getInstance(mContext).readUerId().equals(""+feedbackBean.getUser_id())){
                                delLayout.setVisibility(View.VISIBLE);
                            }else{
                                delLayout.setVisibility(View.GONE);
                            }
                            nodataLayout.setVisibility(View.GONE);
                            llContainer.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            releaseBtn.setTextColor(ContextCompat.getColor(mContext, R.color.line));
                            releaseBtn.setEnabled(false);
                            commentEditView.setText("");
                            String headUrl = AppPreference.getInstance(ReplyActivity.this).readAvatar();
                            Glide.with(mContext).load(headUrl).into(ivAvatar);
                            tvName.setText(AppPreference.getInstance(ReplyActivity.this).readUserName());
                            tvContent.setText(feedbackBean.getContent());
                            tvTime.setText(feedbackBean.getCreated_at());
                            read();
                        }else{
                            titleView.setText("意见反馈");
                            nodataLayout.setVisibility(View.VISIBLE);
                            llContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        getReply();
                        break;
                }
            }
        };

    }

    @Override
    public void initData() {
        if(feedbackBean==null){
            getFeedback();
        }else {
            getReply();
        }
    }

    TextWatcher commentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String commStr = editable.toString().trim();
            if (StringUtil.isEmpty(commStr)) {
                releaseBtn.setTextColor(ContextCompat.getColor(mContext, R.color.line));
                releaseBtn.setEnabled(false);
            } else {
                releaseBtn.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                releaseBtn.setEnabled(true);
            }
        }
    };

    /**
     * 某个评论的回复列表
     */
    private void getFeedback() {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }


        String url = ApiConstants.BASE_ACTIVITY_API + aid + "/user/feedback";
        String mToken= AppPreference.getInstance(mContext).readToken();
        Map<String, String> params = new HashMap<>();
        params.put("include","user");


        OkHttpUtils.get().url(url).addHeader("Authorization",mToken).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                nodataLayout.setVisibility(View.VISIBLE);
                llContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        Type type = new TypeToken<FeedbackBean>() {
                        }.getType();
                        feedbackBean = new Gson().fromJson(response.toString(), type);
                        aid = String.valueOf(feedbackBean.getId());
                        mHandler.sendEmptyMessage(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 某个评论的回复列表
     */
    private void getReply() {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (feedbackBean == null) {
            nodataLayout.setVisibility(View.VISIBLE);
            llContainer.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        String url = ApiConstants.BASE_ACTIVITY_API + feedbackBean.getActivity_id() + "/feedbacks/"+feedbackBean.getId()+"/replies" ;
        String mToken= AppPreference.getInstance(mContext).readToken();
        Map<String, String> params = new HashMap<>();
        params.put("include","user");


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
                            replyList.clear();
                            /*if (commentBean != null) {
                                replyList.add(commentBean);
                            }*/
                        }
                        if (commentList != null && commentList.size() > 0) {

                            for (FeedbackBean bean : commentList) {
                                replyList.add(bean);
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


    private void deleteComment(final FeedbackBean bean, final boolean isItemDel){
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConstants.BASE_ACTIVITY_API + bean.getActivity_id()+"/feedbacks/"+bean.getId();
        String mToken=AppPreference.getInstance(mContext).readToken();
        OkHttpUtils.delete().url(url).addHeader("Authorization", mToken).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Toast.makeText(mContext, "删除成功！", Toast.LENGTH_SHORT).show();
                if(!isItemDel) {
                    feedbackBean = null;
                }
                mHandler.sendEmptyMessage(3);
            }
        });
    }


    private void post(String comment) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConstants.BASE_ACTIVITY_API + aid + "/feedbacks" ;
        String mToken=AppPreference.getInstance(mContext).readToken();

        Map<String, String> params = new HashMap<>();
        params.put("content", comment);


        OkHttpUtils.post().url(url).addHeader("Authorization", mToken).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Type type = new TypeToken<FeedbackBean>() {
                }.getType();
                feedbackBean = new Gson().fromJson(response.toString(),type);
                mHandler.sendEmptyMessage(3);
            }
        });
    }


    private void read() {
        if (!NetUtil.detectAvailable(mContext)) {
            return;
        }

        String url = ApiConstants.BASE_URL + "/api/user/read/feedbacks" ;
        String mToken=AppPreference.getInstance(mContext).readToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"ids\": ["+feedbackBean.getId()+"]}");

        //RequestBody requestBody = new FormBody.Builder().add("ids", aid).build();

        OkHttpUtils.put().url(url).addHeader("Authorization", mToken).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                Log.e("read.status","error"+response);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("read.status","onResponse"+response);
            }
        });
    }


    /**
     * 回复
     */
    private void replyComment(String comment) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (feedbackBean == null) {
            return;
        }

        String url = ApiConstants.BASE_ACTIVITY_API + feedbackBean.getActivity_id() + "/feedbacks/" + feedbackBean.getId() + "/replies";
        String mToken=AppPreference.getInstance(mContext).readToken();

        Map<String, String> params = new HashMap<>();
        params.put("content", comment);//被@的用户的信息


        OkHttpUtils.post().url(url).addHeader("Authorization", mToken).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(2);
            }
        });
    }


    /**
     * 删除评论
     */


    /**
     * 强制显示软键盘
     */
    private void showKeyboard() {
        commentEditView.setFocusable(true);
        commentEditView.setFocusableInTouchMode(true);
        commentEditView.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEditView, InputMethodManager.SHOW_FORCED);
    }


    /**
     * 强制隐藏软键盘
     */
    private void hiddenKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditView.getWindowToken(), 0);
    }


    /**
     * 跳转到登录界面
     */
    private void navToLogin(){
        Intent intent=new Intent(mContext,LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
