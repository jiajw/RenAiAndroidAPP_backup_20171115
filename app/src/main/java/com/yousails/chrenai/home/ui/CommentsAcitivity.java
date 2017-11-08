package com.yousails.chrenai.home.ui;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.adapter.CommentAdapter;
import com.yousails.chrenai.home.bean.CommentBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.home.listener.OnComItemClickListener;
import com.yousails.chrenai.home.listener.ReplyEvent;
import com.yousails.chrenai.home.listener.VotedEvent;
import com.yousails.chrenai.login.ui.LoginActivity;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.WrapContentLinearLayoutManager;
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
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/7/18.
 */

public class CommentsAcitivity extends BaseActivity implements OnComItemClickListener {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;
    private EditText commentEditView;
    private Button releaseBtn;

    private LinearLayout nodataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<CommentBean> commentBeanList = new ArrayList<CommentBean>();

    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount = 1;
    private CommentBean commentBean;//点击跳转的评论条目
    private CommentBean currentComment;//点赞的评论条目
    private String commentText;
    private String aId;
    private String flag;
    private String userId;
    private static String mToken;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_comments);
    }

    @Override
    protected void init() {

        // 注册对象
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        if (intent != null) {
            aId = intent.getStringExtra("id");
            flag=intent.getStringExtra("flag");
            userId=intent.getStringExtra("userId");
        }
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("讨论");

        commentEditView = (EditText) findViewById(R.id.et_comment);
        releaseBtn = (Button) findViewById(R.id.btn_release);

        if("show".equals(flag)){
            showKeyboard();
        }

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
                        pageCount = 1;
                        isLoadMore = false;
                        getCommentLists();
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        commentAdapter = new CommentAdapter(mContext, userId,commentBeanList);
        commentAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(commentAdapter);


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

                if (lastVisibleItem + 1 == commentAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if (lastVisibleItem + 1 >= total) {
                        commentAdapter.setFooterSwitch(0);
                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        commentAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore = true;
                        commentAdapter.setFooterSwitch(1);
                        getCommentLists();
                    }

                }

            }
        });
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        releaseBtn.setOnClickListener(this);
        commentEditView.addTextChangedListener(commentTextWatcher);
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
                        postComment(commentText);
                    }
                } else {
                   // Toast.makeText(mContext, "请先登录!", Toast.LENGTH_SHORT).show();
                    navToLogin();
                }

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
                        if (commentBeanList != null && commentBeanList.size() > 0) {
                            commentAdapter.notifyDataChanged(commentBeanList);
                            nodataLayout.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if (total > commentAdapter.getItemCount()) {
                                commentAdapter.setFooterSwitch(1);
                            } else {
                                commentAdapter.setFooterSwitch(0);
                            }
                            if (!isLoadMore) {
                                recyclerView.scrollToPosition(0);
                            }
                        } else {
                            nodataLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
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
                        getCommentLists();
                        EventBus.getDefault().post("refreshwebview");
                        break;
                    case 3:
                        CustomToast.createToast(mContext, "删除成功");
                        pageCount = 1;
                        isLoadMore = false;
                        getCommentLists();
                        EventBus.getDefault().post("refreshwebview");
                        break;

                }
            }
        };

    }

    @Override
    public void initData() {
        getCommentLists();
    }


    /**
     * 获取评论列表
     */

    private void getCommentLists() {

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(aId)) {
            return;
        }

        String url = ApiConstants.GET_COMMENTS_API + aId + "/comments";

        Map<String, String> params = new HashMap<>();
        params.put("page", pageCount + "");
        params.put("id", aId);

        OkHttpUtils.get().url(url).params(params).build().execute(new StringCallback() {
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

                        Type type = new TypeToken<ArrayList<CommentBean>>() {
                        }.getType();
                        ArrayList<CommentBean> commentList = new Gson().fromJson(jsonArray.toString(), type);
                        if (!isLoadMore) {
                            commentBeanList.clear();
                        }
                        if (commentList != null && commentList.size() > 0) {

                            for (CommentBean commentBean : commentList) {
                                commentBeanList.add(commentBean);
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

    /**
     * 发布评论
     */
    private void postComment(String content) {

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(aId)) {
            return;
        }

        String url = ApiConstants.GET_COMMENTS_API + aId + "/comments";
        mToken=AppPreference.getInstance(mContext).readToken();

        Map<String, String> params = new HashMap<>();
        params.put("id", aId);
        params.put("content", content);

        OkHttpUtils.post().url(url).addHeader("Authorization", mToken).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        //先认证，才可发评论
                        String message = jsonObject.optString("message");
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(2);
            }
        });
    }

    @Override
    public void onItemClick(CommentBean commBean) {
        commentBean=commBean;
        Intent intent = new Intent(mContext, ReplyActivity.class);
        intent.putExtra("userId",userId);
        intent.putExtra("comment", commBean);
        startActivity(intent);
    }

    @Override
    public void doVote(CommentBean commentBean) {
        currentComment = commentBean;

        boolean isLogin = AppPreference.getInstance(mContext).readLogin();
        if (isLogin) {
            if (!currentComment.is_voted()) {
                doVoteById(currentComment.getId());
            } else {
                deleVoteById(currentComment.getId());
            }
        } else {
            //Toast.makeText(mContext, "请先登录!", Toast.LENGTH_SHORT).show();
            navToLogin();
        }


    }

    @Override
    public void delComment(CommentBean commentBean) {
        //不需要判断是否登录，否则就不会显示删除按钮(adapter已经作了判断)
        deleteComment(commentBean.getId());
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
        commentEditView.setText("");
        commentEditView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditView.getWindowToken(), 0);
    }

    /**
     * 点赞某个评论
     */
    private void doVoteById(String cId) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConstants.SUPPORT_COMMENT_BY_ID + cId;
        mToken=AppPreference.getInstance(mContext).readToken();

        RequestBody requestBody = new FormBody.Builder().add("id", "cId").build();
        OkHttpUtils.put().url(url).addHeader("Authorization", mToken).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                CustomToast.createToast(mContext, "点赞成功");
                if (currentComment != null && commentAdapter != null) {
                    int voteCount = Integer.valueOf(currentComment.getVote_count()) + 1;
                    currentComment.setVote_count(voteCount + "");
                    currentComment.setIs_voted(true);
                    commentAdapter.notifyVoteChanged(currentComment);
                }

            }
        });
    }

    /**
     * 取消点赞某个评论
     */
    private void deleVoteById(String cId) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = ApiConstants.DEL_SUPPORT_COMMENT_BY_ID + cId;
        mToken=AppPreference.getInstance(mContext).readToken();
        OkHttpUtils.delete().url(url).addHeader("Authorization", mToken).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                CustomToast.createToast(mContext, "取消点赞成功");
                if (currentComment != null && commentAdapter != null) {
                    int voteCount = Integer.valueOf(currentComment.getVote_count()) - 1;
                    currentComment.setVote_count(voteCount + "");
                    currentComment.setIs_voted(false);
                    commentAdapter.notifyVoteChanged(currentComment);
                }
            }
        });
    }

    /**
     * 删除评论
     */
    private void deleteComment(String cId){
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = ApiConstants.GET_COMMENTS_API + aId+"/comments/"+cId;
        mToken=AppPreference.getInstance(mContext).readToken();
        RequestBody requestBody = new FormBody.Builder().add("pid",aId).add("id", "cId").build();
        OkHttpUtils.delete().url(url).addHeader("Authorization", mToken).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
               mHandler.sendEmptyMessage(3);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateVoted(VotedEvent votedEvent) {
       String cId= votedEvent.getcId();
        boolean b;
       if("doVote".equals(votedEvent.getFlag())){
           b=true;
       }else{
           b=false;
       }

        if (commentAdapter != null) {
            for(CommentBean cBean:commentBeanList){
                if(cId.equals(cBean.getId())){
                    commentBean=cBean;
                    break;
                }
            }

            if(b){
                int voteCount = Integer.valueOf(commentBean.getVote_count()) + 1;
                commentBean.setVote_count(voteCount + "");
                commentBean.setIs_voted(true);
            }else{
                int voteCount = Integer.valueOf(commentBean.getVote_count()) - 1;
                commentBean.setVote_count(voteCount + "");
                commentBean.setIs_voted(false);
            }

            commentAdapter.notifyVoteChanged(commentBean);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateReplyCount(ReplyEvent replyEvent){
        String type= replyEvent.getType();
        if(commentBean!=null&&commentAdapter != null){
            if("add".equals(type)){
                commentBean.setReply_count((Integer.valueOf(commentBean.getReply_count())+1)+"");
            }else{
                commentBean.setReply_count((Integer.valueOf(commentBean.getReply_count())-1)+"");
            }

            commentAdapter.notifyVoteChanged(commentBean);
        }

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
