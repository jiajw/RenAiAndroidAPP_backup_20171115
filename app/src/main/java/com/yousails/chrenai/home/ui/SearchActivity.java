package com.yousails.chrenai.home.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.db.KeyWordDBService;
import com.yousails.chrenai.home.adapter.ColumnAdapter;
import com.yousails.chrenai.home.adapter.HistorySearchAdapter;
import com.yousails.chrenai.home.adapter.HotWordAdapter;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.KeyWordBean;
import com.yousails.chrenai.home.bean.PaginationBean;
import com.yousails.chrenai.home.listener.OnItemClickListener;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.wheel.views.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/4.
 */

public class SearchActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private DrawerLayout mDrawerLayout = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ColumnAdapter columnAdapter;
    private LinearLayout backLayout;
    private TextView searchView;
    private EditText editTextView;
    private ImageView clearView;
    private GridView hotGridView;
    private TextView clearHisView;
    private LinearLayout keyWordLayout;
    private LinearLayout historyLayout;
    private ListView historyListView;
    private LinearLayout nodataLayout;

    private LinearLayout dateLayout;
    private TextView dataView;
    private LinearLayout confirmLayout;
    private LinearLayout cancelLayout;

    private LinearLayout kmLayout;
    private LinearLayout km3Layout;
    private LinearLayout km6Layout;

    private LinearLayout dayLayout;
    private LinearLayout weekendLayout;
    private LinearLayout weekDayLayout;

    private LinearLayout filterLayout;
    private LinearLayout filterDateLayout;
    private TextView filterDateView;

    private LinearLayout filterDistanceLayout;
    private TextView filterDistanceView;

    private HistorySearchAdapter historyAdapter;
    private HotWordAdapter hotWordAdapter = null;
    private List<KeyWordBean> hotWordLists = new ArrayList<KeyWordBean>();
    private List<String> historyLists = new ArrayList<String>();
    private List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
    private KeyWordDBService keyWordDBService = null;
    private String searchContent="";
    private String date="";
    private String distance="";
    private String cId;
    private boolean b;
    private String fromTag;
    private boolean isLoading;
    private boolean isLoadMore;
    private int total;
    private int pageCount=1;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_search_layout);

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
//        cId = AppPreference.getInstance(mContext).readCategoryId();
        fromTag=getIntent().getStringExtra("from");

        keyWordDBService = new KeyWordDBService(this);

        if(!EventBus.getDefault().isRegistered(this)){
            try{
                // 注册对象
                EventBus.getDefault().register(this);
            }catch (Exception e){
                System.out.print(e.toString());
            }

        }
    }

    @Override
    protected void findViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        backLayout = (LinearLayout) findViewById(R.id.title_back);
        editTextView = (EditText) findViewById(R.id.edit_text);
        clearView = (ImageView) findViewById(R.id.iv_clear);

        searchView = (TextView) findViewById(R.id.txt_more);
        searchView.setText(R.string.selected_text);

        keyWordLayout = (LinearLayout) findViewById(R.id.keyword_layout);
        hotGridView = (GridView) findViewById(R.id.gridview);
        hotWordAdapter = new HotWordAdapter(SearchActivity.this, hotWordLists);
        hotGridView.setAdapter(hotWordAdapter);

        clearHisView=(TextView)findViewById(R.id.clear_history_tview);

        historyLayout = (LinearLayout) findViewById(R.id.search_layout);
        historyListView = (ListView) findViewById(R.id.listview);

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
                        pageCount=1;
                        isLoadMore=false;
                        getActivitiesByKey(searchContent,distance,date);
                    }
                }).start();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        columnAdapter = new ColumnAdapter(mContext, activitiesBeanList);
        recyclerView.setAdapter(columnAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();

//                int itemCount=columnAdapter.getItemCount();
                if (lastVisibleItem + 1 == columnAdapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    if(lastVisibleItem+1>=total){
                        columnAdapter.setFooterSwitch(0);
                        return;
                    }

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        columnAdapter.setFooterSwitch(0);
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        pageCount++;
                        isLoadMore=true;
                        columnAdapter.setFooterSwitch(1);
                        getActivitiesByKey(searchContent,distance,date);

                    }

                }
            }
        });


        //------------------------------筛选-----------------------------------

        //距离
        kmLayout=(LinearLayout)findViewById(R.id.btn_left);
        km3Layout=(LinearLayout)findViewById(R.id.btn_center);
        km6Layout=(LinearLayout)findViewById(R.id.btn_right);

        //时间
        dayLayout=(LinearLayout)findViewById(R.id.btn_tleft);
        weekendLayout=(LinearLayout)findViewById(R.id.btn_tcenter);
        weekDayLayout=(LinearLayout)findViewById(R.id.btn_tright);

        //日期选择
        dateLayout = (LinearLayout) findViewById(R.id.date_layout);
        dataView = (TextView) findViewById(R.id.tv_data);

        cancelLayout=(LinearLayout)findViewById(R.id.cancel_layout);
        confirmLayout=(LinearLayout)findViewById(R.id.confirm_layout);


        //筛选条件按钮
        filterLayout=(LinearLayout)findViewById(R.id.filter_layout);
        filterDateLayout=(LinearLayout)findViewById(R.id.filter_date_layout);
        filterDateView=(TextView)findViewById(R.id.filter_date_tv);

        filterDistanceLayout=(LinearLayout)findViewById(R.id.filter_distance_layout);
        filterDistanceView=(TextView)findViewById(R.id.filter_distance_tv);


        if("main".equals(fromTag)){
            keyWordLayout.setVisibility(View.GONE);
            historyLayout.setVisibility(View.GONE);
            nodataLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);


            searchView.setText(R.string.filter_text);
           String tempDate=AppPreference.getInstance(mContext).readFilterDay();
            if(StringUtil.isNotNull(tempDate)){
                if("工作日".equals(tempDate)){
                    date="weekday";
                }else if("周末".equals(tempDate)){
                    date="weekend";
                }else{
                    date=tempDate;
                }
                filterDateView.setText(tempDate);
                filterDateLayout.setVisibility(View.VISIBLE);
            }else{
                date="";
                filterDateLayout.setVisibility(View.GONE);
            }
            String tempDistance=AppPreference.getInstance(mContext).readFilterDistance();
            if(StringUtil.isNotNull(tempDistance)){
                if("3km以内".equals(tempDistance)){
                    distance="3000";
                }else{
                    distance="10000";
                }
                filterDistanceView.setText(tempDistance);
                filterDistanceLayout.setVisibility(View.VISIBLE);
            }else{
                distance="";
                filterDistanceLayout.setVisibility(View.GONE);
            }
            getActivitiesByKey(searchContent,distance,date);
        }

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        searchView.setOnClickListener(this);
        clearView.setOnClickListener(this);
        editTextView.addTextChangedListener(onTextChangeListener);
        hotGridView.setOnItemClickListener(this);
        historyListView.setOnItemClickListener(this);
        clearHisView.setOnClickListener(this);

        dateLayout.setOnClickListener(this);

        //距离
        kmLayout.setOnClickListener(this);
        km3Layout.setOnClickListener(this);
        km6Layout.setOnClickListener(this);

        //时间
        dayLayout.setOnClickListener(this);
        weekendLayout.setOnClickListener(this);
        weekDayLayout.setOnClickListener(this);

        confirmLayout.setOnClickListener(this);
        cancelLayout.setOnClickListener(this);

        filterDateLayout.setOnClickListener(this);
        filterDistanceLayout.setOnClickListener(this);

        columnAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ActivitiesBean activitiesBean) {
                String longitude=AppPreference.getInstance(mContext).readLongitude();
                String latitude=AppPreference.getInstance(mContext).readLatitude();
                boolean isLogin=AppPreference.getInstance(mContext).readLogin();
                String mToken=AppPreference.getInstance(mContext).readToken();
                Intent intent = new Intent(mContext, ActivitDetailActivity.class);
                intent.putExtra("bean",activitiesBean);
                String url ="";
                if(isLogin){
                    url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?token=" + mToken+"&user_coordinate="+longitude+","+latitude;
                }else{
                    url = ApiConstants.BASE_URL + "/activities/" + activitiesBean.getId() + "?user_coordinate="+longitude+","+latitude;
                }

                intent.putExtra("url", url);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.iv_clear:
                pageCount=1;
                isLoadMore=false;
                editTextView.setText("");
                searchView.setText(R.string.selected_text);
                keyWordLayout.setVisibility(View.VISIBLE);
                historyLayout.setVisibility(View.VISIBLE);
                nodataLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                filterDateLayout.setVisibility(View.GONE);
                filterDistanceLayout.setVisibility(View.GONE);
                break;
            case R.id.txt_more:
                searchContent = editTextView.getText().toString().trim();
                if ("搜索".equals(searchView.getText())) {
                    if (StringUtil.isNotNull(searchContent)) {
                        searchView.setText(R.string.filter_text);
                        //搜索历史排序
                        setOrderHistory(searchContent);
                       //更新搜索历史
                        initHistory();

                        getActivitiesByKey(searchContent,null,null);

                        hiddenKeyboard();
                    }

                } else {
                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    } else {
                        hiddenKeyboard();
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }
                }
                break;
            case R.id.btn_left:
                kmLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                km3Layout.setBackgroundResource(R.drawable.ic_filter_normal);
                km6Layout.setBackgroundResource(R.drawable.ic_filter_normal);
                AppPreference.getInstance(mContext).writefilterDistance("");
                break;
            case R.id.btn_center:
                kmLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                km3Layout.setBackgroundResource(R.drawable.ic_filter_selected);
                km6Layout.setBackgroundResource(R.drawable.ic_filter_normal);
                AppPreference.getInstance(mContext).writefilterDistance("3km以内");
                break;
            case R.id.btn_right:
                kmLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                km3Layout.setBackgroundResource(R.drawable.ic_filter_normal);
                km6Layout.setBackgroundResource(R.drawable.ic_filter_selected);
                AppPreference.getInstance(mContext).writefilterDistance("10km以内");
                break;
            case R.id.btn_tleft:
                dayLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dataView.setText(R.string.selected_date_text);
                AppPreference.getInstance(mContext).writefilterDay("");
                break;
            case R.id.btn_tcenter:
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dataView.setText(R.string.selected_date_text);
                AppPreference.getInstance(mContext).writefilterDay("周末");
                break;
            case R.id.btn_tright:
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dataView.setText(R.string.selected_date_text);
                AppPreference.getInstance(mContext).writefilterDay("工作日");
                break;
            case R.id.date_layout:
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                showPickView();
                break;
            case R.id.cancel_layout:
                AppPreference.getInstance(mContext).writefilterDistance("");
                AppPreference.getInstance(mContext).writefilterDay("");
                filterDateLayout.setVisibility(View.GONE);
                filterDistanceLayout.setVisibility(View.GONE);
                closeDrawer();
                initFilterView();
                date="";
                distance="";
                EventBus.getDefault().post("updateFilterView");
                getActivitiesByKey(searchContent,distance,date);

                break;
            case R.id.confirm_layout:
                String tempDate=AppPreference.getInstance(mContext).readFilterDay();
                if(StringUtil.isNotNull(tempDate)){
                    if("工作日".equals(tempDate)){
                        date="weekday";
                    }else if("周末".equals(tempDate)){
                        date="weekend";
                    }else{
                        date=tempDate;
                    }
                    filterDateView.setText(tempDate);
                    filterDateLayout.setVisibility(View.VISIBLE);
                }else{
                    date="";
                    filterDateLayout.setVisibility(View.GONE);
                }
                String tempDistance=AppPreference.getInstance(mContext).readFilterDistance();
                if(StringUtil.isNotNull(tempDistance)){
                    if("3km以内".equals(tempDistance)){
                        distance="3000";
                    }else{
                        distance="10000";
                    }
                    filterDistanceView.setText(tempDistance);
                    filterDistanceLayout.setVisibility(View.VISIBLE);
                }else{
                    distance="";
                    filterDistanceLayout.setVisibility(View.GONE);
                }
                closeDrawer();
                EventBus.getDefault().post("updateFilterView");
                getActivitiesByKey(searchContent,distance,date);

                break;
            case R.id.filter_date_layout:
                AppPreference.getInstance(mContext).writefilterDay("");
                filterDateLayout.setVisibility(View.GONE);
                date="";
                //重新搜索
                getActivitiesByKey(searchContent,distance,date);
                break;
            case R.id.filter_distance_layout:
                AppPreference.getInstance(mContext).writefilterDistance("");
                filterDistanceLayout.setVisibility(View.GONE);
                distance="";
                //重新搜索
                getActivitiesByKey(searchContent,distance,date);
                break;
            case R.id.clear_history_tview:
                AppPreference.getInstance(mContext).writeKeyWords("");
                historyLayout.setVisibility(View.GONE);
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
                    case 0:
                        nodataLayout.setVisibility(View.VISIBLE);
                        keyWordLayout.setVisibility(View.GONE);
                        historyLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        if (hotWordAdapter != null) {
                            hotWordAdapter.setDataChanged(hotWordLists);
                        }
                        break;
                    case 2:
                            keyWordLayout.setVisibility(View.GONE);
                            historyLayout.setVisibility(View.GONE);
                            nodataLayout.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            columnAdapter.notifyDataChanged(activitiesBeanList);
                            //滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
                            if(total>columnAdapter.getItemCount()){
                                columnAdapter.setFooterSwitch(1);
                            }else{
                                columnAdapter.setFooterSwitch(0);
                            }

                        break;

                }
            }
        };
    }

    @Override
    public void initData() {
        initFilterView();
        initKeyWords();
        initHistory();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (view instanceof RelativeLayout) {
            searchContent = (String) adapterView.getItemAtPosition(i);
        } else {
            KeyWordBean keyWordBean = (KeyWordBean) adapterView.getItemAtPosition(i);
            searchContent = keyWordBean.getContent();
        }

        editTextView.setText(searchContent);
        editTextView.setSelection(searchContent.length());//将光标移至文字末尾
        searchView.setText(R.string.filter_text);
       //搜索历史排序
        setOrderHistory(searchContent);
        //更新搜索历史
        initHistory();

        getActivitiesByKey(searchContent,null,null);
    }

    /**
     * 对搜索历史记录进行排序
     */
    private void setOrderHistory(String searchContent){
        String keyWords = AppPreference.getInstance(mContext).readKeyWords();
        if (StringUtil.isNotNull(keyWords)) {
            StringBuffer buffer = new StringBuffer();
            ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(keyWords.split(",")));
            if (arrayList.size() == 5) {
                arrayList.remove(4);
            }

            for (int i = 0; i < arrayList.size(); i++) {
                if (searchContent.equals(arrayList.get(i))) {
                    arrayList.remove(i);
                    break;
                }
            }

            for (int i = 0; i < arrayList.size(); i++) {
                buffer.append(arrayList.get(i) + ",");
            }

            keyWords = buffer.toString();
            AppPreference.getInstance(mContext).writeKeyWords(searchContent + "," + keyWords);

        } else {
            AppPreference.getInstance(mContext).writeKeyWords(searchContent + ",");
        }
    }

    /**
     * 历史搜索记录
     */
    private void initHistory() {
        String keyWords = AppPreference.getInstance(mContext).readKeyWords();
        if (StringUtil.isNotNull(keyWords)) {
            if(!"main".equals(fromTag)){
                historyLayout.setVisibility(View.VISIBLE);
            }
            historyLists.clear();
            String[] keyword = keyWords.split(",");

            for (int i = 0; i < keyword.length; i++) {
                historyLists.add(keyword[i]);
            }
            if(historyAdapter ==null){
                historyAdapter = new HistorySearchAdapter(mContext, historyLists);
            }
            historyListView.setAdapter(historyAdapter);
        }else{
            historyLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化关键词
     */
    private void initKeyWords(){
        if (keyWordDBService != null) {
            hotWordLists = keyWordDBService.getKeyWords();
            if (hotWordLists != null && hotWordLists.size() > 0) {
                hotWordAdapter.setDataChanged(hotWordLists);
            }else{
                getKeyWords();
            }
        }
    }
    /**
     * 获取关键词
     */
    private void getKeyWords() {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpUtils.get().url(ApiConstants.GET_KEYWORDS_API).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
                if (keyWordDBService != null) {
                    hotWordLists = keyWordDBService.getKeyWords();
                    if (hotWordLists != null && hotWordLists.size() > 0) {
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
//                    Log.i(TAG,response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.optJSONArray("data");

                        Type type = new TypeToken<ArrayList<KeyWordBean>>() {
                        }.getType();
                        hotWordLists = new Gson().fromJson(jsonArray.toString(), type);

                        if (hotWordLists != null && hotWordLists.size() > 0 && keyWordDBService != null) {
                            //插入到数据库
                            for (int i = 0; i < hotWordLists.size(); i++) {

                                keyWordDBService.insertKeyWords(hotWordLists.get(i));
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
     * 通过关键词获取活动列表
     */
    private void getActivitiesByKey(String keyWord,String distance,String startDate) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String longitude=AppPreference.getInstance(mContext).readLongitude();
        String latitude=AppPreference.getInstance(mContext).readLatitude();

        String city=AppPreference.getInstance(mContext).readCurrentCity();
        String cityId="";
        if(StringUtil.isNotNull(city)){
            cityId=city.substring(0,city.indexOf(","));
        }

        Map<String, String> params = new HashMap<>();
        params.put("coordinate", longitude+","+latitude);
//        params.put("category", cId);
        params.put("city", cityId);
        params.put("user_coordinate", longitude+","+latitude);
        params.put("page", pageCount+"");
        params.put("include", "user,category");

        if(StringUtil.isNotNull(keyWord)){
            params.put("keyword", keyWord);
        }
        if(StringUtil.isNotNull(distance)){
            params.put("distance", distance);
        }
        if(StringUtil.isNotNull(startDate)){
            params.put("start_date", startDate);
        }

        OkHttpUtils.get().url(ApiConstants.GET_ACTIVITIES_API).params(params).build().execute(new StringCallback() {
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
                        JSONObject paggeArray= metaArray.optJSONObject("pagination");

                        Type pageType = new TypeToken<PaginationBean>() {}.getType();
                        PaginationBean  paginationBean = new Gson().fromJson(paggeArray.toString(), pageType);
                        if(paginationBean!=null){
                            total=Integer.valueOf(paginationBean.getTotal());
                        }

                        Type type = new TypeToken<ArrayList<ActivitiesBean>>() {
                        }.getType();
                        ArrayList<ActivitiesBean> activitiesList = new Gson().fromJson(jsonArray.toString(), type);

                        if (activitiesList != null && activitiesList.size() > 0) {
                            if(!isLoadMore){
                                activitiesBeanList.clear();
                            }

                            for (int i = 0; i < activitiesList.size(); i++) {
                                activitiesBeanList.add(activitiesList.get(i));
                            }

                            mHandler.sendEmptyMessage(2);
                        } else {
                            mHandler.sendEmptyMessage(0);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * 用户搜索监听
     */
    private TextWatcher onTextChangeListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence charSequence, int start,
                                  int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start,
                                      int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int len = editable.length();
            if (len > 0) {
                clearView.setVisibility(View.VISIBLE);
            } else {
                clearView.setVisibility(View.GONE);
            }
        }
    };


    private void showPickView() {
        TimePickerDialog mTimePickerDialog = new TimePickerDialog(
                SearchActivity.this);
        mTimePickerDialog
                .setDialogMode(TimePickerDialog.DIALOG_MODE_BOTTOM);
        mTimePickerDialog.show();
        mTimePickerDialog.setTimePickListener(new TimePickerDialog.OnTimePickListener() {

            @Override
            public void onClick(int year, int month, int day,
                                String hour, String minute) {
                String date=year + "-" + month + "-" + day;
                dataView.setText(date);
                AppPreference.getInstance(mContext).writefilterDay(date);
            }
        });
    }

    /**
     * 初始化筛选界面
     */
    private void initFilterView(){
       String distance= AppPreference.getInstance(mContext).readFilterDistance();
        if(StringUtil.isNotNull(distance)){
            if("3km以内".equals(distance)){
                kmLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                km3Layout.setBackgroundResource(R.drawable.ic_filter_selected);
                km6Layout.setBackgroundResource(R.drawable.ic_filter_normal);
            }else{
                kmLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                km3Layout.setBackgroundResource(R.drawable.ic_filter_normal);
                km6Layout.setBackgroundResource(R.drawable.ic_filter_selected);
            }
        }else{
            kmLayout.setBackgroundResource(R.drawable.ic_filter_selected);
            km3Layout.setBackgroundResource(R.drawable.ic_filter_normal);
            km6Layout.setBackgroundResource(R.drawable.ic_filter_normal);
        }

        String filterDay=AppPreference.getInstance(mContext).readFilterDay();
        if(StringUtil.isNotNull(filterDay)){
            if("周末".equals(filterDay)){
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dataView.setText(R.string.selected_date_text);
            }else if("工作日".equals(filterDay)){
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dataView.setText(R.string.selected_date_text);
            }else{
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                dataView.setText(filterDay);
            }
        }else{
            dayLayout.setBackgroundResource(R.drawable.ic_filter_selected);
            weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
            weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
            dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
            dataView.setText(R.string.selected_date_text);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            EventBus.getDefault().unregister(this);
            closeDrawer();
            finish();
//            override();
        }
        return super.onKeyDown(keyCode, event);
    }



    private void closeDrawer(){
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawers();
            }
        }
    }

    private void override() {
        SearchActivity.this.overridePendingTransition(0, R.anim.activity_down_out);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFilterView(String message){
        if("updateFilterView".equals(message)){

        }
    }

    /**
     * 强制隐藏软键盘
     */
    private void hiddenKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
    }


    /**
     * 强制隐藏软键盘
     */
//    private void hiddenKeyboard() {
//        phoneEditText.setText("");
//        phoneEditText.clearFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(phoneEditText.getWindowToken(), 0);
//    }
}
