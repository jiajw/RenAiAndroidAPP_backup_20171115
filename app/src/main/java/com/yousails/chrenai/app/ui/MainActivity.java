package com.yousails.chrenai.app.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.service.LocationService;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;
import com.yousails.chrenai.home.bean.CityBean;
import com.yousails.chrenai.home.fragment.HomeFragment;
import com.yousails.chrenai.home.ui.ActMapActivity;
import com.yousails.chrenai.home.ui.CityActivity;
import com.yousails.chrenai.home.ui.SearchActivity;
import com.yousails.chrenai.login.ui.LoginActivity;
import com.yousails.chrenai.person.fragment.MyPageFragment;
import com.yousails.chrenai.publish.fragment.PublishFragment;
import com.yousails.chrenai.utils.BadgeViewUtil;
import com.yousails.chrenai.utils.LogUtils;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.PermissionUtils;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.BadgeView;
import com.yousails.chrenai.view.wheel.views.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * 首页
 */
public class MainActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout = null;
    private RelativeLayout titleLayout;
    private LinearLayout searchLayout;
    private TextView filterView;
    private TextView cityView;
    private ImageView localView;

    private FrameLayout frameLayout;
    private LinearLayout homeLayout;
    private LinearLayout publishLayout;
    private LinearLayout personalLayout;

    private ImageView homeImageView;
    private ImageView publishImageView;
    private ImageView personImageView;

    private TextView homeTextView;
    private TextView publishTextView;
    private TextView personTextView;

    private LinearLayout tabMainLayout;
    private LinearLayout tabOtherLayout;

    private HomeFragment homeFragment;
    private BaseFragment publishFragment;
    private MyPageFragment myPageFragment;

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
    private LocationService locationService;

    private int position;

    BadgeView userTabBadge;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        View target = findViewById(R.id.iv_tab_personal);
        userTabBadge = new BadgeView(this);
        userTabBadge.setTargetView(target);
        userTabBadge.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);

        /*try {
            throw new NullPointerException();
        } catch (Exception e) {
            Log.e(TAG, "Exception: ~~~~~~~~~~ ~~~~~~~~~~", e);
        }*/
    }

    @Override
    protected void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            // 注册对象
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isLogin = AppPreference.getInstance(mContext).readLogin();
        Log.e("islogin", isLogin + ">>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (isLogin) {
            BadgeViewUtil.builder(MainActivity.this).setBadge(BadgeViewUtil.BadgeType.MainUserTab, userTabBadge);
        }
    }

    @Override
    protected void findViews() {

        cityView = (TextView) findViewById(R.id.tv_city);
        cityView.setMovementMethod(ScrollingMovementMethod.getInstance());

        titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        searchLayout = (LinearLayout) findViewById(R.id.search_content_layout);

        localView = (ImageView) findViewById(R.id.iv_local);
        filterView = (TextView) findViewById(R.id.tv_selected);


        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        //首页、发布、我的
        homeLayout = (LinearLayout) findViewById(R.id.tab_home_layout);
        publishLayout = (LinearLayout) findViewById(R.id.tab_publish_layout);
        personalLayout = (LinearLayout) findViewById(R.id.tab_personal_layout);

        homeImageView = (ImageView) findViewById(R.id.iv_tab_home);
        publishImageView = (ImageView) findViewById(R.id.iv_tab_publish);
        personImageView = (ImageView) findViewById(R.id.iv_tab_personal);

        homeTextView = (TextView) findViewById(R.id.tv_tab_home);
        publishTextView = (TextView) findViewById(R.id.tv_tab_publish);
        personTextView = (TextView) findViewById(R.id.tv_tab_personal);

        //homeFragment所占的布局
        tabMainLayout = (LinearLayout) findViewById(R.id.tab_main_layout);
        //publish、personal所占的布局
        tabOtherLayout = (LinearLayout) findViewById(R.id.tab_content_layout);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        //------------------------------筛选-----------------------------------

        //距离
        kmLayout = (LinearLayout) findViewById(R.id.btn_left);
        km3Layout = (LinearLayout) findViewById(R.id.btn_center);
        km6Layout = (LinearLayout) findViewById(R.id.btn_right);

        //时间
        dayLayout = (LinearLayout) findViewById(R.id.btn_tleft);
        weekendLayout = (LinearLayout) findViewById(R.id.btn_tcenter);
        weekDayLayout = (LinearLayout) findViewById(R.id.btn_tright);

        //日期选择
        dateLayout = (LinearLayout) findViewById(R.id.date_layout);
        dataView = (TextView) findViewById(R.id.tv_data);

        cancelLayout = (LinearLayout) findViewById(R.id.cancel_layout);
        confirmLayout = (LinearLayout) findViewById(R.id.confirm_layout);


        initCityView();
        initFilterView();

    }


    @Override
    protected void setListeners() {
        cityView.setOnClickListener(this);
        localView.setOnClickListener(this);
        filterView.setOnClickListener(this);
        searchLayout.setOnClickListener(this);

        homeLayout.setOnClickListener(this);
        publishLayout.setOnClickListener(this);
        personalLayout.setOnClickListener(this);

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
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }


    Handler badgeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            userTabBadge.setBadgeCount(1);
        }
    };
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            if (messages.size() > 0) {
                badgeHandler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_city:
                Intent areaIntent = new Intent(MainActivity.this, CityActivity.class);
                startActivity(areaIntent);
                break;
            case R.id.iv_local:
                startActivity(new Intent(MainActivity.this, ActMapActivity.class));
                break;
            case R.id.search_content_layout:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("from", "search");
                startActivity(intent);
//                MainActivity.this.overridePendingTransition(R.anim.activity_up_in, 0);
                break;
            case R.id.tv_selected:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            case R.id.tab_home_layout:
                if (0 == position) return;
                //打开手势滑动
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                position = 0;
                titleLayout.setVisibility(View.VISIBLE);
                tabMainLayout.setVisibility(View.VISIBLE);
                tabOtherLayout.setVisibility(View.GONE);
                updateTabView(position);
                break;
            case R.id.tab_publish_layout:
                if (1 == position) return;
                //禁止手势滑动
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                position = 1;
                titleLayout.setVisibility(View.GONE);
                tabMainLayout.setVisibility(View.GONE);
                tabOtherLayout.setVisibility(View.VISIBLE);
                updateTabView(position);
                replace(publishFragment);
                break;
            case R.id.tab_personal_layout:
                boolean isLogin = AppPreference.getInstance(mContext).readLogin();
                if (isLogin) {
                    if (2 == position) return;
                    //禁止手势滑动
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    position = 2;
                    titleLayout.setVisibility(View.GONE);
                    tabMainLayout.setVisibility(View.GONE);
                    tabOtherLayout.setVisibility(View.VISIBLE);
                    updateTabView(position);
                    replace(myPageFragment);
                    //userTabBadge.setBadgeCount(0);
                } else {
                    Intent loginIntent = new Intent(mContext, LoginActivity.class);
                    loginIntent.putExtra("from", "main");
                    startActivity(loginIntent);
//                    finish();
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
                closeDrawer();
                initFilterView();
                break;
            case R.id.confirm_layout:
                closeDrawer();
                Intent intent1 = new Intent(MainActivity.this, SearchActivity.class);
                intent1.putExtra("from", "main");
                MainActivity.this.startActivity(intent1);
//                MainActivity.this.overridePendingTransition(R.anim.activity_up_in, 0);
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
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            cityView.setText(bundle.getString("city"));
                        }

                        break;

                }
            }
        };

    }

    @Override
    public void initData() {

        if (Build.VERSION.SDK_INT >= 23) {
            String[] perms = new String[]{
                    PermissionUtils.PERMISSION_RECORD_AUDIO,
                    PermissionUtils.PERMISSION_GET_ACCOUNTS,
                    PermissionUtils.PERMISSION_READ_PHONE_STATE,
                    PermissionUtils.PERMISSION_CALL_PHONE,
                    PermissionUtils.PERMISSION_CAMERA,
                    PermissionUtils.PERMISSION_ACCESS_FINE_LOCATION,
                    PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION,
                    PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE,
                    PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE};
            PermissionUtils.requestMultiPermissions(MainActivity.this, perms, mPermissionGrant);
        }

        homeFragment = new HomeFragment();
        publishFragment = new PublishFragment();
        myPageFragment = new MyPageFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.tab_main_layout, homeFragment).commit();

        startLocation();
        getAuth();
    }

    private void replace(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tab_content_layout, fragment).commit();
        }
    }

    /**
     * 更改底部tab
     */
    private void updateTabView(int position) {
        switch (position) {
            case 0:
                homeImageView.setImageResource(R.drawable.ic_home_selected);
                publishImageView.setImageResource(R.drawable.ic_publish_normal);
                personImageView.setImageResource(R.drawable.ic_me_normal);

                homeTextView.setTextColor(getResources().getColor(R.color.main_blue_color));
                publishTextView.setTextColor(getResources().getColor(R.color.text_gray1));
                personTextView.setTextColor(getResources().getColor(R.color.text_gray1));
                break;
            case 1:
                homeImageView.setImageResource(R.drawable.ic_home_normal);
                publishImageView.setImageResource(R.drawable.ic_publish_selected);
                personImageView.setImageResource(R.drawable.ic_me_normal);

                homeTextView.setTextColor(getResources().getColor(R.color.text_gray1));
                publishTextView.setTextColor(getResources().getColor(R.color.main_blue_color));
                personTextView.setTextColor(getResources().getColor(R.color.text_gray1));
                break;
            case 2:
                homeImageView.setImageResource(R.drawable.ic_home_normal);
                publishImageView.setImageResource(R.drawable.ic_publish_normal);
                personImageView.setImageResource(R.drawable.ic_me_selected);

                homeTextView.setTextColor(getResources().getColor(R.color.text_gray1));
                publishTextView.setTextColor(getResources().getColor(R.color.text_gray1));
                personTextView.setTextColor(getResources().getColor(R.color.main_blue_color));
                break;
        }
    }


    /**
     * 初始化筛选界面
     */
    private void initFilterView() {
        String distance = AppPreference.getInstance(mContext).readFilterDistance();
        if (StringUtil.isNotNull(distance)) {
            if ("3km以内".equals(distance)) {
                kmLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                km3Layout.setBackgroundResource(R.drawable.ic_filter_selected);
                km6Layout.setBackgroundResource(R.drawable.ic_filter_normal);
            } else {
                kmLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                km3Layout.setBackgroundResource(R.drawable.ic_filter_normal);
                km6Layout.setBackgroundResource(R.drawable.ic_filter_selected);
            }
        } else {
            kmLayout.setBackgroundResource(R.drawable.ic_filter_selected);
            km3Layout.setBackgroundResource(R.drawable.ic_filter_normal);
            km6Layout.setBackgroundResource(R.drawable.ic_filter_normal);
        }

        String filterDay = AppPreference.getInstance(mContext).readFilterDay();
        if (StringUtil.isNotNull(filterDay)) {
            if ("周末".equals(filterDay)) {
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dataView.setText(R.string.selected_date_text);
            } else if ("工作日".equals(filterDay)) {
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dataView.setText(R.string.selected_date_text);
            } else {
                dayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
                dateLayout.setBackgroundResource(R.drawable.ic_filter_selected);
                dataView.setText(filterDay);
            }
        } else {
            dayLayout.setBackgroundResource(R.drawable.ic_filter_selected);
            weekendLayout.setBackgroundResource(R.drawable.ic_filter_normal);
            weekDayLayout.setBackgroundResource(R.drawable.ic_filter_normal);
            dateLayout.setBackgroundResource(R.drawable.ic_filter_normal);
            dataView.setText(R.string.selected_date_text);
        }
    }

    private void showPickView() {
        TimePickerDialog mTimePickerDialog = new TimePickerDialog(
                MainActivity.this);
        mTimePickerDialog
                .setDialogMode(TimePickerDialog.DIALOG_MODE_BOTTOM);
        mTimePickerDialog.show();
        mTimePickerDialog.setTimePickListener(new TimePickerDialog.OnTimePickListener() {

            @Override
            public void onClick(int year, int month, int day,
                                String hour, String minute) {
                String date = year + "-" + month + "-" + day;
                dataView.setText(date);
                AppPreference.getInstance(mContext).writefilterDay(date);
            }
        });
    }


    /***
     * 刷新当前城市
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeCity(String message) {
        if ("changeCity".equals(message)) {
            initCityView();
        }

    }

    /***
     * 刷新 filter
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFilterView(String message) {
        if ("updateFilterView".equals(message)) {
            initFilterView();
        }
    }


    /***
     * 更换昵称
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateNickname(String message) {
        if (myPageFragment != null && "updatename".equals(message)) {
            myPageFragment.updateNickname();
        }
    }

    /***
     * 更换头像
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAvatar(String message) {
        if (myPageFragment != null && "updateavatar".equals(message)) {
            myPageFragment.updateAvatar();
        }
    }

    /***
     * 切换到我的页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openMyPage(String message) {
        if ("mypage".equals(message)) {
            position = 2;
            titleLayout.setVisibility(View.GONE);
            tabMainLayout.setVisibility(View.GONE);
            tabOtherLayout.setVisibility(View.VISIBLE);
            updateTabView(position);
            myPageFragment = new MyPageFragment();
            replace(myPageFragment);
        }
    }


    /***
     * 切换到发布页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPublish(String message) {
        if ("publish".equals(message)) {
            position = 1;
            titleLayout.setVisibility(View.GONE);
            tabMainLayout.setVisibility(View.GONE);
            tabOtherLayout.setVisibility(View.VISIBLE);
            updateTabView(position);
            replace(publishFragment);
        }
    }


    private void initCityView() {
        String city = AppPreference.getInstance(mContext).readCurrentCity();
        if (StringUtil.isNotNull(city)) {
            cityView.setText(city.substring(city.indexOf(",") + 1));
            if (homeFragment != null) {
                homeFragment.changeCity();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // startLocation();
    }

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        stopLocation();
        super.onStop();
    }


    /***
     * 启动定位
     */
    private void startLocation() {
        // -----------location config ------------
        locationService = ((ModelApplication) getApplication()).locationService;
        //获取locationservice实例
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        locationService.start();// 定位SDK
    }

    /***
     * 停止定位
     */
    private void stopLocation() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }


    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                String longitude = location.getLongitude() + "";
                String latitude = location.getLatitude() + "";

                if (StringUtil.isNotNull(longitude) && !longitude.contains("E")) {
                    AppPreference.getInstance(mContext).writeLongitude(longitude);
                    AppPreference.getInstance(mContext).writeLatitude(latitude);
                    String city = location.getCity();
                    if (StringUtil.isNotNull(city)) {
                        if (city.contains("市")) {
                            city = city.substring(0, city.length() - 1);
                        }

                        initLocalCity(city);

                    }
                }

                //注销掉监听
                stopLocation();
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };


    /**
     * 初始定位城市数据
     */
    private void initLocalCity(String cityName) {

        String localCity = AppPreference.getInstance(mContext).readLocationCity();
        String tempName;
        if (StringUtil.isNotNull(localCity)) {
            if (localCity.contains(",")) {
                tempName = localCity.substring(localCity.indexOf(",") + 1);
            } else {
                tempName = localCity;
            }
            if (!cityName.equals(tempName)) {

                List<CityBean> cityList = ModelApplication.localCityList;

                for (CityBean cityBean : cityList) {
                    if (cityName.equals(cityBean.getName())) {
                        AppPreference.getInstance(mContext).writeLocationCity(cityBean.getId() + "," + cityBean.getName());

                        //首次定位城市，初始化切换的城市数据
                        String firstLocal = AppPreference.getInstance(mContext).readFirLocalCity();
                        if ("0".equals(firstLocal)) {
                            AppPreference.getInstance(mContext).writeFirLocalCity("1");
                            AppPreference.getInstance(mContext).writeCurrentCity(cityBean.getId() + "," + cityBean.getName());
                            EventBus.getDefault().post("changeCity");
                        }

                        break;
                    }
                }

                Message message = new Message();
                message.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("city", cityName);
                message.setData(bundle);
                mHandler.sendMessage(message);

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDrawer();
        EventBus.getDefault().unregister(this);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    private void closeDrawer() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawers();
            }
        }
    }


    /**
     * 获取实名认证信息
     */
    private void getAuth() {
        boolean isLogin = AppPreference.getInstance(mContext).readLogin();
        if (!isLogin) {
            return;
        }

        if (!NetUtil.detectAvailable(mContext)) {
            //   Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String mToken = AppPreference.getInstance(mContext).readToken();

        OkHttpUtils.get().url(ApiConstants.SUBMIT_CERTIFICATION_API).addHeader("Authorization", mToken).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.println("----->" + response);
                if (response == null) {
                    Toast.makeText(mContext, "获取数据失败！", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONObject reJson = jsonObject.optJSONObject("religion");
                        if (reJson != null) {
                            AppPreference.getInstance(mContext).writeReligion(reJson.optString("name"));
                        }

                        //applied: 已申请, passed: 已通过, rejected: 已拒绝
                        String status = jsonObject.optString("status");
                        if ("passed".equals(status)) {
                            AppPreference.getInstance(mContext).writeCertification("1");
                        } else {
                            AppPreference.getInstance(mContext).writeCertification("");
                        }

                        String userId = AppPreference.getInstance(mContext).readUerId();
                        AppPreference.getInstance(mContext).writeStatus(userId, status);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            LogUtils.e("==onPermissionGranted==" + requestCode);
            switch (requestCode) {
                case PermissionUtils.CODE_MULTI_PERMISSION:
//                    startActivity(new Intent(MainActivity.this, ActMapActivity.class));
                    break;

                default:
                    break;
            }
        }
    };
}
