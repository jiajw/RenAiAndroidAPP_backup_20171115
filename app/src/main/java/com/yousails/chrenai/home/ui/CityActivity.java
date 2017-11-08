package com.yousails.chrenai.home.ui;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;
import com.yousails.chrenai.db.CityDBService;
import com.yousails.chrenai.home.bean.CityAdapter;
import com.yousails.chrenai.home.bean.CityBean;
import com.yousails.chrenai.home.bean.SearchCityAdapter;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.AssortView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by Administrator on 2017/7/9.
 */

public class CityActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout cityListLayout;
    private RelativeLayout searchLayout;
    private ProgressBar proBar;
    private ExpandableListView cityExpandListView;
    private ListView searchListView;
    private AssortView assortView;
    private View headView;
    private EditText editTextView;
    private ImageView clearView;
    private LinearLayout locationCityLayout;
    private TextView currentCityView;
    private TextView locationCityView;

    private List<CityBean> cityList = new ArrayList<>();
    private List<CityBean>searchList=new  ArrayList<CityBean>();
    private CityDBService cityDBService;
    private CityAdapter cityAdapter;
    private SearchCityAdapter searchCityAdapter;
    private View layoutView;
    private TextView promptView;
    private PopupWindow popupWindow;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_city);
    }

    @Override
    protected void init() {
        // 注册对象
        EventBus.getDefault().register(this);
        cityDBService=new CityDBService(mContext);
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        TextView titleView=(TextView)findViewById(R.id.title);
        titleView.setText("选择城市");
        RelativeLayout titleSearchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        titleSearchLayout.setVisibility(View.GONE);

        cityListLayout=(RelativeLayout)findViewById(R.id.city_list_layout);
        searchLayout=(RelativeLayout)findViewById(R.id.city_search_content_layout);
        proBar=(ProgressBar)findViewById(R.id.pbar_loading);

        cityExpandListView = (ExpandableListView) findViewById(R.id.city_elistview);
        assortView = (AssortView) findViewById(R.id.assort);
        searchListView=(ListView) findViewById(R.id.search_list);

        cityAdapter = new CityAdapter(mContext, cityList, mHandler);
        cityExpandListView.setAdapter(cityAdapter);

        searchCityAdapter=new SearchCityAdapter(mContext,searchList);
        searchListView.setAdapter(searchCityAdapter);

        editTextView=(EditText)findViewById(R.id.city_edit_text);
        clearView=(ImageView)findViewById(R.id.city_iv_clear);

        layoutView = LayoutInflater.from(CityActivity.this)
                .inflate(R.layout.alert_menu_layout, null);
        promptView = (TextView) layoutView.findViewById(R.id.content);

        headView = View.inflate(this, R.layout.activity_city_head_layout, null);
        currentCityView=(TextView)headView.findViewById(R.id.current_city_tview);
        locationCityView=(TextView)headView.findViewById(R.id.location_city_tview);
        locationCityLayout=(LinearLayout)headView.findViewById(R.id.location_city_layout);

        //切换的城市
        String city= AppPreference.getInstance(mContext).readCurrentCity();
        if(StringUtil.isNotNull(city)){

            currentCityView.setText( city.substring(city.indexOf(",")+1));
        }

        //定位的城市
        String locationCity=AppPreference.getInstance(mContext).readLocationCity();
        if(StringUtil.isNotNull(locationCity)){
            if(locationCity.contains(",")){
                locationCityView.setText(locationCity.substring(locationCity.indexOf(",")+1));
            }else{
                locationCityView.setText(locationCity);
            }

        }

        headView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) assortView
                                .getLayoutParams();
                        int height= headView.getMeasuredHeight();
                        param.topMargin=height+40;
                        assortView.setLayoutParams(param);
                        headView.getViewTreeObserver().removeGlobalOnLayoutListener(this);


                    }
                });
        cityExpandListView.addHeaderView(headView);



    }

    @Override
    protected void setListeners() {
        //字母按键回调
        assortView.setOnTouchAssortListener(touchAssortListener);
        locationCityLayout.setOnClickListener(this);
        backLayout.setOnClickListener(this);
        editTextView.addTextChangedListener(onTextChangeListener);
        clearView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.city_iv_clear:
                editTextView.setText("");
                clearView.setVisibility(View.GONE);
                mHandler.sendEmptyMessage(1);
                break;
            case R.id.location_city_layout:
                String city= AppPreference.getInstance(mContext).readLocationCity();
                AppPreference.getInstance(mContext).writeCurrentCity(city);

                EventBus.getDefault().post("changeCity");
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
                        if (cityList != null && cityList.size() > 0) {

                            cityListLayout.setVisibility(View.VISIBLE);
                            searchLayout.setVisibility(View.VISIBLE);
                            proBar.setVisibility(View.GONE);

                            assortView.setVisibility(View.VISIBLE);
                            cityExpandListView.setVisibility(View.VISIBLE);
                            searchListView.setVisibility(View.GONE);
                            cityAdapter.setDataChanged(cityList);
                            for (int i = 0; i < cityAdapter.getGroupCount(); i++) {

                                cityExpandListView.expandGroup(i);

                            }

                        }
                        break;
                    case 2:
                        assortView.setVisibility(View.GONE);
                        cityExpandListView.setVisibility(View.GONE);
                        searchListView.setVisibility(View.VISIBLE);
                        searchCityAdapter.setDataChanged(searchList);
                        break;

                }
            }
        };


    }

    @Override
    public void initData() {
        cityList =ModelApplication.localCityList;
        if(cityList!=null&&cityList.size()>0){
            mHandler.sendEmptyMessage(1);
        }
    }


    /**
     * 字母按键回调监听
     */
    AssortView.OnTouchAssortListener touchAssortListener=new AssortView.OnTouchAssortListener(){

        @Override
        public void onTouchAssortListener(String s) {
            int index = cityAdapter.getAssort().getHashList().indexOfKey(s);
            if (index != -1) {
                cityExpandListView.setSelectedGroup(index);
            }
            if (popupWindow != null) {
                promptView.setText(s);
            } else {
                popupWindow = new PopupWindow(layoutView, 180, 180, false);


                // 显示在Activity的根视图中心
                popupWindow.showAtLocation(getWindow().getDecorView(),
                        Gravity.CENTER, 0, 0);
            }
            promptView.setText(s);

        }

        @Override
        public void onTouchAssortUP() {
            if (popupWindow != null){
                popupWindow.dismiss();
                popupWindow = null;
            }

        }
    };


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
            searchList.clear();
            int len = editable.length();
            String tempstr = editable.toString();
            if (len > 0) {
                clearView.setVisibility(View.VISIBLE);
            } else {
                clearView.setVisibility(View.GONE);
            }


            if (cityList != null && cityList.size() > 0) {
                for(int i=0;i<cityList.size();i++){
                    CityBean cityBean= cityList.get(i);

                    if(cityBean.getInitials().contains(tempstr)||cityBean.getPinyin().contains(tempstr)||cityBean.getName().contains(tempstr)){
                        searchList.add(cityBean);
                    }
                }

                mHandler.sendEmptyMessage(2);

            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String message) {
        if("changeCity".equals(message)){
            this.finish();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
