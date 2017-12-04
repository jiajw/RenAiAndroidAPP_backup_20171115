package com.yousails.chrenai.publish.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.camera.CropImageIntentBuilder;
import com.baidu.mapapi.search.core.PoiInfo;
import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.DateStyle;
import com.yousails.chrenai.common.DateUtil;
import com.yousails.chrenai.common.FileUtils;
import com.yousails.chrenai.common.KeyBoardUtil;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.common.ScreenUtil;
import com.yousails.chrenai.common.SharedPreferencesUtil;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.AppConfigBean;
import com.yousails.chrenai.home.ui.ActivitDetailActivity;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.net.HttpRequestUtil;
import com.yousails.chrenai.net.listener.ModelRequestListener;
import com.yousails.chrenai.net.listener.StringRequestListener;
import com.yousails.chrenai.person.ui.PersonProfileActivity;
import com.yousails.chrenai.publish.Activity.EditorActivity;
import com.yousails.chrenai.publish.bean.ActiveApplyItem;
import com.yousails.chrenai.publish.bean.ActiveItem;
import com.yousails.chrenai.publish.bean.Category;
import com.yousails.chrenai.publish.bean.ImageUploadEntity;
import com.yousails.chrenai.publish.bean.PublishBean;
import com.yousails.chrenai.publish.bean.SimpleDialogItem;
import com.yousails.chrenai.publish.event.PublishItemEvent;
import com.yousails.chrenai.publish.event.PublishTitleClickEvent;
import com.yousails.chrenai.publish.listener.PublishCustomCallBack;
import com.yousails.chrenai.publish.listener.PublishCustomClickListener;
import com.yousails.chrenai.publish.util.GlideUtil;
import com.yousails.chrenai.publish.util.SimpleDialog;
import com.yousails.chrenai.publish.widget.CustomToast;
import com.yousails.chrenai.publish.widget.FinishEditDialog;
import com.yousails.chrenai.publish.widget.OnceScanDurationDialog;
import com.yousails.chrenai.publish.widget.PublishDialog;
import com.yousails.chrenai.publish.widget.PublishItemView;
import com.yousails.chrenai.publish.widget.SimpleCustomDialog;
import com.yousails.chrenai.publish.widget.SimpleHelpDialog;
import com.yousails.chrenai.utils.LogUtils;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.PermissionUtils;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.common.event.EventBusManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.wordpress.android.editor.Event.EditorEvent;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;
import cn.dreamtobe.kpswitch.widget.KPSwitchRootLinearLayout;
import okhttp3.Call;
import okhttp3.RequestBody;

import static com.yousails.chrenai.home.ui.EnrollActivity.JSON;

/**
 * 发布活动
 * Author:WangKunHui
 * Date: 2017/7/12 13:25
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PublishActivity extends BaseActivity {

    private final String SP_STARTDATE = "startDate";
    private final String SP_ENDDATE = "endDate";

    /**
     * 软键盘是否正在展示
     */
    public static boolean isKeyboardShowing = false;

    //活动从属的requestCode
    private final int requestCodeCategory = 1;

    //活动设置的requestCode
    private final int requestCodeApplySetting = 2;

    //活动地点选择的requestCode
    private final int requestCodeAddressChoice = 3;

    /**
     * 打开相册的requestCode
     */
    private final int requestCodeAlbum = 101;

    /**
     * 打开摄像头的requestCode
     */
    private final int requestCodeCamera = 102;

    /**
     * 图片剪裁
     */
    private final int requestCodeCrop = 103;

    //申请相册权限code
    private final int permission_album = 1;

    //申请摄像头权限（包含文件权限）code
    private final int permission_camera = 2;

    /**
     * 是否可以直接返回上一个界面的标识符
     */
    private boolean isCanBack = true;

    private KPSwitchRootLinearLayout rootView;

    /**
     * 活动封面
     */
    private ImageView activeImage;

    private LinearLayout addPictureButton;

    private LinearLayout changePictureButton;

    /**
     * 活动名称
     */
    private PublishItemView activeName;

    /**
     * 活动从属
     */
    private PublishItemView activeCategory;

    /**
     * 开始时间
     */
    private PublishItemView activeBeginTime;

    /**
     * 结束时间
     */
    private PublishItemView activeEndTime;

    /**
     * 活动地点
     */
    private PublishItemView activeLocation;

    /**
     * 活动描述
     */
    private PublishItemView activeDesc;

    /**
     * 人数限制
     */
    private PublishItemView activeMemberLimit;

    /**
     * 是否收费
     */
    private PublishItemView activeIsCharge;

    /**
     * 申请设置
     */
    private PublishItemView activeApplySetting;

    /**
     * 签到审核
     */
    private PublishItemView activeSign;

    private KPSwitchPanelLinearLayout panelLinearLayout;

    /**
     * 被选中的活动从属
     */
    private Category selectedCategory;

    /**
     * 选择图片的dialog
     */
    private DialogPlus albumChoiceDialog;

    public final String IS_EDITOR = "isEditor";


    //拍照所得图片的名字
    private String upImageName = "camera_image.jpg";

    //剪裁所得图片的名字
    private String cropImageName = "crop_image.jpg";


    private List<ActiveApplyItem> activeApplyItems;

    private Date beginDate;

    private Date endDate;

    private ActiveItem activeItem;

    private ImageUploadEntity imageUploadEntity;

    private List<String> onceScanHour = new ArrayList<>();

    private List<String> onceScanMin = new ArrayList<>();

    private int onceScanSelectedHour = 0;

    private int onceScanSelectedMin = 0;

    //是否缓存的有活动发布数据
    private boolean isCachedActiveItem = false;

    //报名设置是否设置完成
    private boolean isApplySettingAdded = true;

    private PoiInfo selectedPoiInfo;

    //活动地点
    private String locationName;

    //活动坐标
    private String locationLatAndLog;

    private String description;

    private String addressDescription;
    private boolean isGlobal;

    //一次扫码工时
    private int onceTime;

    ActivitiesBean editActivity;

    private PublishDialog publishDialog;


    @Override
    protected void onDestroy() {
        EventBusManager.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void setContentView() {
        EventBusManager.getInstance().register(this);
        setContentView(R.layout.activity_publish);
    }

    @Override
    protected void init() {

        editActivity = (ActivitiesBean) getIntent().getSerializableExtra("activity");
        if (null == editActivity) {
            ((TextView) findViewById(R.id.publish_title)).setText("活动发布");
            ((TextView) findViewById(R.id.tv_publish)).setText("发布");
            ((TextView) findViewById(R.id.tv_preview)).setVisibility(View.VISIBLE);


            activeItem = SharedPreferencesUtil.getObject(this, ApiConstants.SharedKey.CACHED_ACTIVE_KEY, ActiveItem.class);
            if (activeItem != null) {
                isCachedActiveItem = true;
                activeApplyItems = activeItem.getApplication_config();
                beginDate = SharedPreferencesUtil.getObject(this, SP_STARTDATE, Date.class);
                endDate = SharedPreferencesUtil.getObject(this, SP_ENDDATE, Date.class);
            } else {
                activeApplyItems = new ArrayList<>();
                activeItem = new ActiveItem();
            }
        } else {
            ((TextView) findViewById(R.id.publish_title)).setText("活动编辑");
            ((TextView) findViewById(R.id.tv_publish)).setText("修改活动");
            ((TextView) findViewById(R.id.tv_preview)).setVisibility(View.GONE);
            activeItem = new ActiveItem();
            ImageUploadEntity iue = new ImageUploadEntity();
            iue.setFile(editActivity.getCover_image());
            iue.setId(Integer.parseInt(editActivity.getCover_image_id()));
            activeItem.setImageUploadEntity(iue);
            activeItem.setCover_image_id(Integer.parseInt(editActivity.getCover_image_id()));
            activeItem.setTitle(editActivity.getTitle());
            //activeItem.setCategory_id(editActivity.getCategory_id());
            Category category = new Category();
            category.setId(Integer.parseInt(editActivity.getCategory().getId()));
            category.setName(editActivity.getCategory().getName());
            activeItem.setCategory(category);
            activeItem.setCategory_id(Integer.parseInt(editActivity.getCategory_id()));
            activeItem.setStarted_at(editActivity.getStarted_at());
            activeItem.setEnded_at(editActivity.getEnded_at());
            activeItem.setCoordinate(editActivity.getCoordinate());
            activeItem.setAddress(editActivity.getAddress());
            activeItem.setAddress_description(editActivity.getAddress_description());
            activeItem.setIs_global(editActivity.is_global());
            Log.e("activity.description", editActivity.getDescription());
            activeItem.setDescription(editActivity.getDescription());
            SharedPreferences sp = PublishActivity.this.getSharedPreferences(
                    "editor_content_edit", Context.MODE_PRIVATE);
            sp.edit().putString("html", editActivity.getDescription()).commit();
            activeItem.setLimit(editActivity.getLimit());
            activeItem.setIs_chargeable(editActivity.is_chargeable());
            List<ActiveApplyItem> activeApplyItems = new ArrayList<>();
            //activeApplyItems.add(0, new ActiveApplyItem("电话", ActiveApplyItem.TYPE_SINGLE_INPUT, true));
            //activeApplyItems.add(0, new ActiveApplyItem("姓名", ActiveApplyItem.TYPE_SINGLE_INPUT, true));
            if (editActivity.getApplication_config() != null && editActivity.getApplication_config().size() > 0) {
                for (AppConfigBean acb : editActivity.getApplication_config()) {
                    ActiveApplyItem aai = new ActiveApplyItem();
                    aai.setTitle(acb.getTitle());
                    aai.setType(acb.getType());
                    aai.setOptions(null == acb.getOptions() ? new ArrayList<String>() : Arrays.asList(acb.getOptions()));
                    aai.setRequired(acb.isRequired());
                    //aai.setAddView(acb.getValue());
                    activeApplyItems.add(aai);
                }
            }
            activeItem.setApplication_config(activeApplyItems);
            Log.e("activity.signtype", editActivity.getSign_type());
            activeItem.setSign_type(editActivity.getSign_type());
            activeItem.setRecord_time(editActivity.getRecord_time());
            activeApplyItems = activeItem.getApplication_config();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
            try {
                beginDate = sdf.parse(editActivity.getStarted_at());
                endDate = sdf.parse(editActivity.getEnded_at());
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    protected void findViews() {
        rootView = (KPSwitchRootLinearLayout) findViewById(R.id.root_view);
        activeImage = (ImageView) findViewById(R.id.iv_active_image);

        addPictureButton = (LinearLayout) findViewById(R.id.ll_add_picture);
        changePictureButton = (LinearLayout) findViewById(R.id.ll_change_picture);

        activeName = (PublishItemView) findViewById(R.id.active_name);
        activeCategory = (PublishItemView) findViewById(R.id.active_org);
        activeBeginTime = (PublishItemView) findViewById(R.id.active_begin_time);
        activeEndTime = (PublishItemView) findViewById(R.id.active_end_time);
        activeLocation = (PublishItemView) findViewById(R.id.active_location);
        activeDesc = (PublishItemView) findViewById(R.id.active_desc);
        activeMemberLimit = (PublishItemView) findViewById(R.id.active_member_limit);
        activeIsCharge = (PublishItemView) findViewById(R.id.active_is_charge);
        activeApplySetting = (PublishItemView) findViewById(R.id.active_apply_setting);
        activeSign = (PublishItemView) findViewById(R.id.active_sign);
        panelLinearLayout = (KPSwitchPanelLinearLayout) findViewById(R.id.panel_root);
    }

    @Override
    protected void initViews() {
        resetImageViewHeight();

        initCachedActive();
    }

    /**
     * 接受Eventbus消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditorEvent(EditorEvent action) {
        boolean flag = action.flag;
        if (flag) {
            if (null != activeDesc) {
                setActiveDesc(flag);
                SharedPreferencesUtil.setBoolean(getApplicationContext(), IS_EDITOR, true);
            }
        }
    }

    private void setActiveDesc(boolean flag) {
        if (flag) {
            if (activeDesc != null) {
                activeDesc.setItemDesc("继续编辑");
            }
        } else {
            if (activeDesc != null) {
                activeDesc.setItemDesc("-请选择-");
            }
        }
    }

    /**
     * 加载缓存内容
     */
    private void initCachedActive() {

        if (activeItem != null) {
            imageUploadEntity = activeItem.getImageUploadEntity();
            if (imageUploadEntity != null) {
                String filePath = activeItem.getImageUploadEntity().getFile();
                if (!TextUtils.isEmpty(filePath)) {
                    addPictureButton.setVisibility(View.GONE);
                    changePictureButton.setVisibility(View.VISIBLE);

                    GlideUtil.loadImage(filePath, activeImage);
                } else {
                    addPictureButton.setVisibility(View.VISIBLE);
                    changePictureButton.setVisibility(View.GONE);
                }
            }

            String title = activeItem.getTitle();
            if (!TextUtils.isEmpty(title)) {
                activeName.setItemDesc(title);
            }

            selectedCategory = activeItem.getCategory();
            if (selectedCategory != null) {
                activeCategory.setItemDesc(selectedCategory.getName());
            }

            String started_at = activeItem.getStarted_at();
            if (!TextUtils.isEmpty(started_at)) {
                activeBeginTime.setItemDesc(timeFormat(started_at));
            }

            String ended_at = activeItem.getEnded_at();
            if (!TextUtils.isEmpty(ended_at)) {
                activeEndTime.setItemDesc(timeFormat(ended_at));
            }

            locationLatAndLog = activeItem.getCoordinate();

            locationName = activeItem.getAddress();
            addressDescription = activeItem.getAddress_description();

            if (!TextUtils.isEmpty(locationName)) {
                activeLocation.setItemDesc(locationName + (TextUtils.isEmpty(addressDescription) ? "" : addressDescription));
            }

            int limit = activeItem.getLimit();
            if (limit != 0) {
                activeMemberLimit.setItemDesc(limit + "");
            }

            description = activeItem.getDescription();

            Boolean is_chargeable = activeItem.isIs_chargeable();
            if (is_chargeable != null) {
                activeIsCharge.setCharge(is_chargeable);
            }

            if (null != editActivity && !TextUtils.isEmpty(editActivity.getDescription())) {
                setActiveDesc(true);
            } else {
                boolean aBoolean = SharedPreferencesUtil.getBoolean(getApplicationContext(), IS_EDITOR,
                        false);
                if (aBoolean) {
                    setActiveDesc(aBoolean);
                }
            }

            String sign_type = activeItem.getSign_type();
            if (!TextUtils.isEmpty(sign_type)) {
                if (sign_type.equals("once")) {
                    String record_time = activeItem.getRecord_time();
                    if (!TextUtils.isEmpty(record_time)) {
                        try {
                            onceTime = Integer.valueOf(record_time);
                            activeSign.setOnceSelected(
                                    (onceTime / 60) + "小时" + (onceTime % 60) + "分钟");
                        } catch (NumberFormatException e) {
                            Log.i(TAG, "不是数字");
                        }
                    }

                } else {
                    activeSign.setSignSelector(sign_type);
                }
            }

            activeApplyItems = activeItem.getApplication_config();
            if (activeApplyItems != null && activeApplyItems.size() > 0) {

                if (editActivity != null) {
                    String showText = "已设置" + (activeApplyItems.size() + 2) + "个选项";
                    activeApplySetting.setItemDesc(showText);
                } else {
                    String showText = "已设置" + activeApplyItems.size() + "个选项";
                    activeApplySetting.setItemDesc(showText);
                }
                isApplySettingAdded = true;

            } else {
                if (editActivity != null) {
                    String showText = "已设置2个选项";
                    activeApplySetting.setItemDesc(showText);
                }
            }
        }
    }

    private String timeFormat(String savedTime) {

        if (TextUtils.isEmpty(savedTime)) {
            return savedTime;
        }

        String format = DateStyle.YYYY_MM_DD_HH_MM_SS.getValue();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date parse = simpleDateFormat.parse(savedTime);

            String result = getShowTime(parse);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedTime;
    }

    private void resetImageViewHeight() {

        int margin = ScreenUtil.dip2px(mContext, 8);

        int imageViewWidth = ScreenUtil.getScreenWidth(mContext) - margin * 2;
        int height = (int) ((double) imageViewWidth * 4 / 9);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageViewWidth,
                height);
        activeImage.setLayoutParams(params);
    }

    @Override
    protected void setListeners() {

        KeyboardUtil.attach(this, panelLinearLayout,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        LogUtil.i(TAG, "isShowing : " + isShowing);
                        isKeyboardShowing = isShowing;
                    }
                });

        addPictureButton.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {
                        showAlbumChoiceDialog();
                    }
                }));

        changePictureButton.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {
                        showAlbumChoiceDialog();
                    }
                }));

        activeCategory.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {
                        CategoryChoiceActivity.launchForResult(mContext, selectedCategory,
                                requestCodeCategory);
                    }
                }));

        activeBeginTime.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {
                        showDataChoiceDialog("活动开始时间", new Date(), true);
                    }
                }));

        activeEndTime.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {
                        if (beginDate == null) {
                            ToastUtils.showShort(mContext, "请先选择活动开始时间");
                        } else {
                            showDataChoiceDialog("活动结束时间", beginDate, false);
                        }
                    }
                }));

        activeLocation.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {

                        //地点设置
                        //ActPromulgateActivity.launchForResult(mContext);
                        Intent actpromulgate = new Intent(PublishActivity.this, ActPromulgateActivity.class);
                        actpromulgate.putExtra("locationName", locationName);
                        actpromulgate.putExtra("addressDescription", addressDescription);
                        startActivityForResult(actpromulgate, ActPromulgateActivity.REQUEST_CODE_ADDRESS);
                    }
                }));

        activeDesc.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {
                        //TODO
                        String html = "";
                        if (null != editActivity && !TextUtils.isEmpty(editActivity.getDescription())) {
                            SharedPreferences sp = getApplication().getSharedPreferences(
                                    "editor_content_edit", Context.MODE_PRIVATE);
                            html = sp.getString("html", "");
                        } else {
                            SharedPreferences sp = getApplication().getSharedPreferences(
                                    "editor_content", Context.MODE_PRIVATE);
                            html = sp.getString("html", "");
                        }

                        EditorActivity.launchForResult(PublishActivity.this, html);
                    }
                }));

        activeApplySetting.setOnClickListener(
                new PublishCustomClickListener(mContext, new PublishCustomCallBack() {
                    @Override
                    public void execute(View view) {

                        ApplySettingActivity.launchForResult(mContext, activeApplyItems, requestCodeApplySetting);
                    }
                }));
    }

    /**
     * 显示时间选择
     */
    private void showDataChoiceDialog(String title, Date date, final boolean isBegin) {

        Calendar startDate = Calendar.getInstance();
        Calendar finishDate = Calendar.getInstance();

        startDate.setTime(date);
        finishDate.add(Calendar.YEAR, +2);

        //时间选择器
        final TimePickerView pvTime = new TimePickerView.Builder(mContext)
                .setTitleText(title)
                .setRangDate(startDate, finishDate)
                .setLabel("年", "月", "日", "时", "分")//默认设置为年月日时分秒
                .build();

        pvTime.setTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (isBegin) {
                    beginDate = date;

                    String showTime = getShowTime(beginDate);
                    activeBeginTime.setItemDesc(showTime);
                    pvTime.dismiss();
                    activeItem.setStarted_at(
                            DateUtil.DateToString(beginDate, DateStyle.YYYY_MM_DD_HH_MM));

                    //重置活动结束时间
                    endDate = null;
                    activeEndTime.descClear();
                    activeItem.setEnded_at(null);

                } else {

                    long during = date.getTime() - beginDate.getTime();
                    if (during < 360000) {
                        CustomToast toast = CustomToast.makeText(mContext,
                                "结束时间必须晚于开始时间1小时，\n" + "请检查后重新填写！", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        endDate = date;
                        String showTime = getShowTime(endDate);
                        activeEndTime.setItemDesc(showTime);
                        pvTime.dismiss();
                        activeItem.setEnded_at(
                                DateUtil.DateToString(endDate, DateStyle.YYYY_MM_DD_HH_MM));
                    }
                }
            }
        });

        pvTime.setDate(Calendar.getInstance(),
                false);
        //注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();

    }

    private String getShowTime(Date date) {
        String ymd = DateUtil.DateToString(date, DateStyle.YYYY_MM_DD_CN);
        String week = DateUtil.getWeek(date).getChineseName();
        String time = DateUtil.DateToString(date, DateStyle.HH_MM);

        return ymd + " " + week + " " + time;
    }

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        showLoadingDialog();
                        break;
                    case 1:
                        if (publishDialog != null) {
                            publishDialog.dismiss();
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case requestCodeCategory:
                    selectedCategory = (Category) data.getSerializableExtra(
                            CategoryChoiceActivity.CATEGORY_EXTRA);
                    if (selectedCategory != null) {
                        activeCategory.setItemDesc(selectedCategory.getName());
                    }
                    break;
                case ActPromulgateActivity.REQUEST_CODE_ADDRESS:
                    locationName = data.getStringExtra("locationName");
                    locationLatAndLog = data.getStringExtra("latAndLon");
                    addressDescription = data.getStringExtra("addressDescription");

                    isGlobal = data.getBooleanExtra("isGlobal", false);
                    activeItem.setIs_global(isGlobal);

                    LogUtil.e("===isGlobal=="+isGlobal);

                    if (!TextUtils.isEmpty(locationName)) {
                        activeLocation.setItemDesc(locationName + (TextUtils.isEmpty(addressDescription) ? "" : addressDescription));
                    }
                    break;

                case requestCodeAlbum:
                    cropPicture(data.getData());
                    break;
                case requestCodeCamera:
                    cropPicture(getImageUri());
                    break;
                case requestCodeCrop:

                    File croppedImageFile = new File(getFilesDir(), "test.jpg");
                    if (croppedImageFile.exists()) {

                        String path = croppedImageFile.getAbsolutePath();
                        activeImage.setImageBitmap(BitmapFactory.decodeFile(path));

                        addPictureButton.setVisibility(View.GONE);
                        changePictureButton.setVisibility(View.VISIBLE);

                        uploadImageFile(path);
                    }

                    break;
                case requestCodeApplySetting:
                    activeApplyItems = (List<ActiveApplyItem>) data.getSerializableExtra(ApplySettingActivity.APPLY_ITEMS);
                    isApplySettingAdded = true;
                    String showText = "已设置" + (activeApplyItems.size() + 2) + "个选项";
                    activeApplySetting.setItemDesc(showText);
                    break;
            }
        }
    }

    /**
     * 上传图片
     */
    private void uploadImageFile(String path) {

        HttpRequestUtil.getInstance(ApiConstants.POST_FILE_UPLOAD).setDefaultHeader().upload(path,
                ImageUploadEntity.class, new ModelRequestListener<ImageUploadEntity>() {
                    @Override
                    public void onSuccess(ImageUploadEntity imageUploadEntity) {
                        LogUtil.i(TAG, "imageUploadEntity : " + imageUploadEntity);
                        if (imageUploadEntity == null) {
                            ToastUtils.showShort(mContext, "图片上传失败");
                        } else {
                            PublishActivity.this.imageUploadEntity = imageUploadEntity;
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showShort(mContext, "图片上传失败");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
        switch (requestCode) {
            case permission_camera:
                if (isPermissionSuccess(grantResults)) {
                    executeOpenCamera();
                } else {
                    ToastUtils.showShort(mContext, "权限被拒绝");
                }
                break;
            case permission_album:
                if (isPermissionSuccess(grantResults)) {
                    executeOpenAlbum();
                } else {
                    ToastUtils.showShort(mContext, "权限被拒绝");
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        finishEdit();
    }

    //============EventBus事件 Start==========//

    @Subscribe
    public void onEvent(PublishTitleClickEvent event) {
        if (event == null) {
            return;
        }
        switch (event.getType()) {
            case PublishTitleClickEvent.TYPE_BACK:
                requestBack();
                break;
            case PublishTitleClickEvent.TYPE_PREVIEW:
                if (isKeyboardShowing) {
                    KeyBoardUtil.closeKeyboard(mContext);
                } else {
//                    ToastUtils.showShort(mContext, "进入预览页");
                    checkData(false);
                }
                break;
            case PublishTitleClickEvent.TYPE_PUBLISH:
                if (isKeyboardShowing) {
                    KeyBoardUtil.closeKeyboard(mContext);
                } else {
                    checkData(true);
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(PublishItemEvent event) {
        if (event == null) {
            return;
        }
        switch (event.getClickType()) {
            case PublishItemEvent.TYPE_HELP:
                showHelpDialog();
                break;
            case PublishItemEvent.TYPE_ONCE_SCAN:
                showOnceScanDialog();
                break;
        }
    }


    //============EventBus事件 End==========//

    //=================业务方法 Start==================//

    private OnceScanDurationDialog onceScanDialog;

    /**
     * 展示一次选择的dialog
     */
    private void showOnceScanDialog() {

        if (onceScanDialog == null) {
            onceScanDialog = new OnceScanDurationDialog(mContext);
            onceScanDialog.setTitle("一次扫码工时设置");
            onceScanDialog.setIsCanOutSideCancelable(true);

            for (int i = 0; i <= 99; i++) {
                onceScanHour.add(i + "小时");
            }

            for (int i = 0; i <= 59; i++) {
                onceScanMin.add(i + "分钟");
            }

            onceScanDialog.setData(onceScanHour, onceScanMin);
            onceScanDialog.setOnChoiceListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onceScanDialog.dismiss();

                    int[] choice = onceScanDialog.getChoice();
                    onceScanSelectedHour = choice[0];
                    onceScanSelectedMin = choice[1];

                    String showOnceScanDesc = onceScanHour.get(onceScanSelectedHour)
                            + onceScanMin.get(onceScanSelectedMin);
                    activeSign.setOnceSelected(showOnceScanDesc);
                    onceTime = onceScanSelectedHour * 60 + onceScanSelectedMin + 1;
                }
            });
        }
        onceScanDialog.setSelected(onceScanSelectedHour, onceScanSelectedMin);
        onceScanDialog.show();
    }

    /**
     * 帮助说明dialog
     */
    private SimpleHelpDialog helpDialog;

    /**
     * 帮助alert框
     */
    private void showHelpDialog() {
        if (helpDialog == null) {
            helpDialog = new SimpleHelpDialog(mContext);
        }
        helpDialog.show();
    }

    /**
     * 结束编辑
     */
    private void finishEdit() {

        if (activeItem == null) {
            activeItem = new ActiveItem();
        }

        if (imageUploadEntity != null) {
            isCanBack = false;
            activeItem.setImageUploadEntity(imageUploadEntity);
        }

        String title = activeName.getInputText();
        if (!TextUtils.isEmpty(title)) {
            isCanBack = false;
            activeItem.setTitle(title);
        }

        if (selectedCategory != null) {
            isCanBack = false;
            activeItem.setCategory(selectedCategory);
        }

        if (beginDate != null) {
            isCanBack = false;
            if (null == editActivity) {
                activeItem.setStarted_at(
                        DateUtil.DateToString(beginDate, DateStyle.YYYY_MM_DD_HH_MM));
                SharedPreferencesUtil.setObject(this, SP_STARTDATE, beginDate); //保存开始时间对象
            }

        }

        if (endDate != null) {
            isCanBack = false;
            if (null == editActivity) {
                activeItem.setEnded_at(DateUtil.DateToString(endDate, DateStyle.YYYY_MM_DD_HH_MM));
                SharedPreferencesUtil.setObject(this, SP_ENDDATE, beginDate); //保存开始时间对象
            }
        }

        if (!TextUtils.isEmpty(locationLatAndLog)) {
            isCanBack = false;
            activeItem.setCoordinate(locationLatAndLog);
        }

        if (!TextUtils.isEmpty(locationName)) {
            isCanBack = false;
            activeItem.setAddress(locationName);
        }

        if (!TextUtils.isEmpty(addressDescription)) {
            activeItem.setAddress_description(addressDescription);
        }

        activeItem.setIs_global(isGlobal);

        if (null == editActivity) {
            //设置描述内容
            SharedPreferences sp = getApplication().getSharedPreferences(
                    "editor_content", Context.MODE_PRIVATE);
            String html = sp.getString("html", "");
            if (!TextUtils.isEmpty(html)) {
                isCanBack = false;
                activeItem.setDescription(html);
            }
        } else {
//            activeItem.setDescription(editActivity.getDescription());
            SharedPreferences sp = getApplication().getSharedPreferences(
                    "editor_content_edit", Context.MODE_PRIVATE);
            String html = sp.getString("html", "");
            if (!TextUtils.isEmpty(html)) {
                isCanBack = false;
                activeItem.setDescription(html);
            }
        }

        String number = activeMemberLimit.getInputText();
        if (!TextUtils.isEmpty(number)) {
            isCanBack = false;
            activeItem.setLimit(Integer.parseInt(number));
        }


        Boolean charge = activeIsCharge.isCharge();
        if (charge != null) {
            isCanBack = false;
            activeItem.setIs_chargeable(charge);
        }

        String signType = activeSign.getSignType();
        if (!TextUtils.isEmpty(signType)) {
            isCanBack = false;
            activeItem.setSign_type(signType);

            if (signType.equals("once")) {
                activeItem.setRecord_time(String.valueOf(onceTime));
            }
        }

        if (null != editActivity) {
            showFinishEditAlertDialog();
        } else {
            if (isCanBack) {
                finish();
            } else {
                showFinishAlertDialog();
            }
        }
    }


    private SimpleCustomDialog finishDialog;

    /**
     * 是否保存的对话框
     */
    private void showFinishAlertDialog() {

        if (finishDialog == null) {
            finishDialog = new SimpleCustomDialog(mContext);

            finishDialog.setOnPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDialog.dismiss();

                    SharedPreferencesUtil.setObject(mContext,
                            ApiConstants.SharedKey.CACHED_ACTIVE_KEY, activeItem);
                    ToastUtils.showShort(mContext, "保存成功");
                    finish();
                }
            });

            finishDialog.setOnNavigateListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clearCache();
                    finishDialog.dismiss();
                    finish();
                }
            });

        }

        finishDialog.show();
    }

    private FinishEditDialog finishEditDialog;

    /**
     * 是否保存的对话框
     */
    private void showFinishEditAlertDialog() {

        if (finishEditDialog == null) {
            finishEditDialog = new FinishEditDialog(mContext);

            finishEditDialog.setOnPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishEditDialog.dismiss();
                }
            });

            finishEditDialog.setOnNavigateListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finishEditDialog.dismiss();
                    SharedPreferences sp = PublishActivity.this.getSharedPreferences(
                            "editor_content_edit", Context.MODE_PRIVATE);
                    sp.edit().remove("html").commit();
                    finish();
                }
            });

        }

        finishEditDialog.show();
    }

    private void clearCache() {
        //清空富文本内容
        SharedPreferences sp = PublishActivity.this.getSharedPreferences(
                "editor_content", Context.MODE_PRIVATE);
        sp.edit().remove("html").commit();
        //清空缓存信息
        SharedPreferencesUtil.removeString(mContext,
                ApiConstants.SharedKey.CACHED_ACTIVE_KEY);

        SharedPreferencesUtil.removeString(getApplicationContext(), IS_EDITOR);
        SharedPreferencesUtil.removeString(getApplicationContext(), SP_STARTDATE);
        SharedPreferencesUtil.removeString(getApplicationContext(), SP_ENDDATE);
    }

    /**
     * 点击发布是  先check数据
     *
     * @param isPublish true 发布，false 预览
     */
    private void checkData(boolean isPublish) {

        if (!NetUtil.detectAvailable(mContext)) {
            ToastUtils.showShort(getApplicationContext(), "请检查网络");
            return;
        }

        if (activeItem == null) {
            activeItem = new ActiveItem();
        }

        String title = activeName.getInputText();
        if (TextUtils.isEmpty(title)) {
            ToastUtils.showShort(mContext, "请输入活动标题");
            return;
        }

        activeItem.setTitle(title);

        if (selectedCategory == null) {
            ToastUtils.showShort(mContext, "请选择活动从属");
            return;
        }

        activeItem.setCategory_id(selectedCategory.getId());

        if (TextUtils.isEmpty(activeItem.getStarted_at())) {
            ToastUtils.showShort(mContext, "请设置活动开始时间");
            return;
        }

        if (TextUtils.isEmpty(activeItem.getEnded_at())) {
            ToastUtils.showShort(mContext, "请设置活动结束时间");
            return;
        }

        if (TextUtils.isEmpty(locationLatAndLog)) {
            ToastUtils.showShort(mContext, "请设置活动地点");
            return;
        }
        activeItem.setCoordinate(locationLatAndLog);

        if (TextUtils.isEmpty(locationName)) {
            ToastUtils.showShort(mContext, "请设置活动地点");
            return;
        }

        activeItem.setAddress(locationName);


        if (!TextUtils.isEmpty(addressDescription)) {
            activeItem.setAddress_description(addressDescription);
        }

        activeItem.setIs_global(isGlobal);

        if (null == editActivity) {
            //设置描述内容
            SharedPreferences sp = getApplication().getSharedPreferences(
                    "editor_content", Context.MODE_PRIVATE);
            String html = sp.getString("html", "");
            activeItem.setDescription(html);
        } else {
//            activeItem.setDescription(editActivity.getDescription());
            SharedPreferences sp = getApplication().getSharedPreferences(
                    "editor_content_edit", Context.MODE_PRIVATE);
            String html = sp.getString("html", "");
            if (!TextUtils.isEmpty(html)) {
                isCanBack = false;
                activeItem.setDescription(html);
            }
        }


        String number = activeMemberLimit.getInputText();
        if (TextUtils.isEmpty(number)) {
            ToastUtils.showShort(mContext, "请设置活动人数");
            return;
        }

        activeItem.setLimit(Integer.parseInt(number));

        Boolean charge = activeIsCharge.isCharge();
        if (charge == null) {
            ToastUtils.showShort(mContext, "请设置是否收费");
            return;
        }

        activeItem.setIs_chargeable(charge);


        if (!isApplySettingAdded) {
            ToastUtils.showShort(mContext, "请设置报名设置");
            return;
        }

        activeItem.setApplication_config(activeApplyItems);

        String signType = activeSign.getSignType();
        if (TextUtils.isEmpty(signType)) {
            ToastUtils.showShort(mContext, "请设置签到审核");
            return;
        }

        activeItem.setSign_type(signType);

        if (signType.equals("once")) {
            activeItem.setRecord_time(String.valueOf(onceTime));
        }

        if (imageUploadEntity == null) {
            ToastUtils.showShort(mContext, "请上传活动封面");
            return;
        }
        activeItem.setCover_image_id(imageUploadEntity.getId());

        /*if((null==activeApplyItems||activeApplyItems.size()==0)&&editActivity!=null){
            ArrayList activeApplyItems1 = new ArrayList<>();
            activeApplyItems1.add(0, new ActiveApplyItem("电话", ActiveApplyItem.TYPE_SINGLE_INPUT, true));
            activeApplyItems1.add(0, new ActiveApplyItem("姓名", ActiveApplyItem.TYPE_SINGLE_INPUT, true));
            activeItem.setApplication_config(activeApplyItems1);
        }*/

        Gson gson = new Gson();
        String json = gson.toJson(activeItem);

        if (isPublish) {
            if (null == editActivity) {
                publish(json);
            } else {
                edit(json);
            }
        } else {
            preview(json);
        }


    }

    /**
     * 发布
     */
    public void publish(String json) {
        mHandler.sendEmptyMessage(0);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization",
                AppPreference.getInstance(mContext).readToken());
        HttpRequestUtil.getInstance(
                ApiConstants.POST_PUBLISH_API)
                //.setDefaultHeader()
                .setHeader(header)
                .setRequestBody(json)
                .post(
                        PublishBean.class, new ModelRequestListener<PublishBean>() {
                            @Override
                            public void onSuccess(PublishBean publishBean) {
                                mHandler.sendEmptyMessage(1);
                                ToastUtils.showShort(getApplicationContext(), "发布成功");
                                clearCache();
                                ActivitiesBean activity = new ActivitiesBean();
                                activity.setId(String.valueOf(publishBean.getId()));
                                activity.setTitle(publishBean.getTitle());
                                activity.setDescription(publishBean.getDescription());
                                activity.setCoordinate(publishBean.getCoordinate());
                                activity.setAddress(publishBean.getAddress());
                                activity.setCategory_id(String.valueOf(publishBean.getCategory_id()));
                                UserBean user = new UserBean();
                                user.setId(AppPreference.getInstance(PublishActivity.this).readUerId());
                                user.setAvatar(AppPreference.getInstance(PublishActivity.this).readAvatar());
                                user.setName(AppPreference.getInstance(PublishActivity.this).readUserName());
                                activity.setUser(user);
                                activity.setIs_started(false);
                                activity.setIs_finished(false);
                                activity.setLimit(publishBean.getLimit());
                                activity.setApplication_total(publishBean.getApplication_total());
                                String longitude = AppPreference.getInstance(PublishActivity.this).readLongitude();
                                String latitude = AppPreference.getInstance(PublishActivity.this).readLatitude();
                                boolean isLogin = AppPreference.getInstance(PublishActivity.this).readLogin();
                                String mToken = AppPreference.getInstance(PublishActivity.this).readToken();
                                Intent intent = new Intent(PublishActivity.this, ActivitDetailActivity.class);
                                intent.putExtra("bean", activity);
                                String url = "";
                                if (isLogin) {
                                    url = ApiConstants.BASE_URL + "/activities/" + activity.getId() + "?token=" + mToken + "&user_coordinate=" + longitude + "," + latitude;
                                } else {
                                    url = ApiConstants.BASE_URL + "/activities/" + activity.getId() + "?user_coordinate=" + longitude + "," + latitude;
                                }

                                intent.putExtra("url", url);
                                PublishActivity.this.startActivity(intent);
                                finish();

                            }

                            @Override
                            public void onFailure(String message) {
//                        Log.e("publist.error",message);
                                mHandler.sendEmptyMessage(1);

                                if (StringUtil.isNotNull(message)) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(message);
                                        String msg = jsonObject.optString("message");
                                        ToastUtils.showShort(getApplicationContext(), "发布失败，" + msg);
                                    } catch (Exception e) {
                                        e.toString();
                                    }
                                }

                            }
                        });
    }


    public void edit(String json) {
        String token = AppPreference.getInstance(mContext).readToken();
        RequestBody requestBody = RequestBody.create(JSON, json);
        OkHttpUtils.patch().url(ApiConstants.POST_PUBLISH_API + "/" + editActivity.getId()).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.println("----->" + response);
                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.optString("message");

                        Bundle bundle = new Bundle();
                        bundle.putString("message", message);

                        Message msg = new Message();
                        msg.what = 0;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }


//                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                ToastUtils.showShort(getApplicationContext(), "编辑成功");
                SharedPreferences sp = PublishActivity.this.getSharedPreferences(
                        "editor_content_edit", Context.MODE_PRIVATE);
                sp.edit().remove("html").commit();
                Type type = new TypeToken<ActivitiesBean>() {
                }.getType();
                ActivitiesBean activity = new Gson().fromJson(response, type);
                UserBean user = new UserBean();
                user.setId(AppPreference.getInstance(PublishActivity.this).readUerId());
                user.setAvatar(AppPreference.getInstance(PublishActivity.this).readAvatar());
                user.setName(AppPreference.getInstance(PublishActivity.this).readUserName());
                activity.setUser(user);
                user.setId(AppPreference.getInstance(PublishActivity.this).readUerId());
                user.setAvatar(AppPreference.getInstance(PublishActivity.this).readAvatar());
                user.setName(AppPreference.getInstance(PublishActivity.this).readUserName());
                String longitude = AppPreference.getInstance(PublishActivity.this).readLongitude();
                String latitude = AppPreference.getInstance(PublishActivity.this).readLatitude();
                boolean isLogin = AppPreference.getInstance(PublishActivity.this).readLogin();
                String mToken = AppPreference.getInstance(PublishActivity.this).readToken();
                Intent intent = new Intent(PublishActivity.this, ActivitDetailActivity.class);
                intent.putExtra("bean", activity);
                String url = "";
                if (isLogin) {
                    url = ApiConstants.BASE_URL + "/activities/" + activity.getId() + "?token=" + mToken + "&user_coordinate=" + longitude + "," + latitude;
                } else {
                    url = ApiConstants.BASE_URL + "/activities/" + activity.getId() + "?user_coordinate=" + longitude + "," + latitude;
                }

                intent.putExtra("url", url);
                PublishActivity.this.startActivity(intent);
                EventBus.getDefault().post("refresh_publish");
                finish();
            }
        });
    }

    /**
     * 预览
     *
     * @param json
     */
    public void preview(String json) {
        HttpRequestUtil.getInstance(
                ApiConstants.POST_PREVIEW_API).setDefaultHeader().setRequestBody(json).post(
                new StringRequestListener() {

                    @Override
                    public void onSuccess(String result) {
                        LogUtil.i(TAG, "result  :  " + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String link = jsonObject.getString("link");

                            if (TextUtils.isEmpty(link)) {
                                ToastUtils.showShort(getApplicationContext(), "预览失败---");
                            } else {
                                H5Activity.launch(PublishActivity.this, link);
                            }
                        } catch (Exception e) {
                            ToastUtils.showShort(getApplicationContext(), e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showShort(getApplicationContext(), message);
                    }
                });
    }


    private void showLoadingDialog() {
        if (publishDialog == null) {
            publishDialog = new PublishDialog(mContext);
        }
        publishDialog.show();
    }

    /**
     * 剪裁图片
     */
    private void cropPicture(Uri uri) {
        Intent intent = new Intent();

        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 9);// 裁剪框比例
        intent.putExtra("aspectY", 4);
        intent.putExtra("outputX", 640);// 输出图片大小
        intent.putExtra("outputY", 360);
        intent.putExtra("return-data", true);

        File croppedImageFile = new File(getFilesDir(), "test.jpg");
        Uri croppedImage = Uri.fromFile(croppedImageFile);
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(9, 4, 640, 360, croppedImage);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(uri);

        startActivityForResult(cropImage.getIntent(this), requestCodeCrop);
    }

    //请求返回上一个页面方法
    private void requestBack() {
        if (isKeyboardShowing) {
            KeyBoardUtil.closeKeyboard(mContext);
        } else {
            finishEdit();
        }
    }

    //图片选择框
    private void showAlbumChoiceDialog() {


        if (Build.VERSION.SDK_INT >= 23) {
            PermissionUtils.requestPermission(PublishActivity.this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
        }


        if (albumChoiceDialog == null) {

            List<SimpleDialogItem> data = new ArrayList<>();
            data.add(new SimpleDialogItem("从相册选择", R.color.publish_picture_choice_text_color_blue));
            data.add(new SimpleDialogItem("拍照", R.color.publish_picture_choice_text_color_blue));
            data.add(new SimpleDialogItem("取消", R.color.publish_picture_choice_text_color_gray));

            albumChoiceDialog = SimpleDialog.create(mContext, data, new OnItemClickListener() {
                @Override
                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    albumChoiceDialog.dismiss();
                    switch (position) {
                        case 0:
                            openAlbum();
                            break;
                        case 1:
                            openCamera();
                            break;
                    }
                }
            });
        }
        albumChoiceDialog.show();
    }

    /**
     * 鉴权打开系统相册
     */
    private void openAlbum() {

        //文件读写权限Check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            List permissions = new ArrayList();
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            applyPermission(permission_album, permissions);
        } else {
            executeOpenAlbum();
        }
    }

    /**
     * 执行打开系统相册的方法
     */
    private void executeOpenAlbum() {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT; // 4.4
        Intent pictureIntent;
        if (isKitKat) {
            pictureIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (!isIntentAvailable(getApplicationContext(), pictureIntent)) {
                ToastUtils.showShort(mContext, "很抱歉，当前您的手机不支持相册选择功能，请安装相册软件");
                return;
            }
        } else {
            pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pictureIntent.setType("image/*");
            if (pictureIntent.resolveActivity(this.getPackageManager()) == null) {
                ToastUtils.showShort(mContext, "很抱歉，当前您的手机不支持相册选择功能，请安装相册软件");
                return;
            }
        }
        try {
            startActivityForResult(pictureIntent, requestCodeAlbum);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 判断是否有可以接受的Activity
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) return false;
        return context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 鉴权打开摄像头方法
     */
    private void openCamera() {

        List permissions = new ArrayList();
        //文件读写权限Check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        //调用摄像头权限Check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        //两个权限都有  不需要在申请
        if (permissions.size() == 0) {
            executeOpenCamera();
        }
        //至少需要申请一个权限
        else {
            applyPermission(permission_camera, permissions);
        }
    }

    /**
     * 执行打开摄像头的方法
     */
    private void executeOpenCamera() {
        try {
            Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            intentFromCamera.putExtra("return-data", true);
            startActivityForResult(intentFromCamera, requestCodeCamera);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(mContext, "需要打开使用照相机权限才能正常使用");
        }
    }

    /**
     * 申请运行时权限
     *
     * @param code        申请码
     * @param permissions 所需要申请的权限
     */
    private void applyPermission(int code, List permissions) {
        if (permissions != null && permissions.size() > 0) {
            String[] permissionArray = (String[]) permissions.toArray(
                    new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, permissionArray, code);
        }
    }

    /**
     * 校验权限是否完全通过
     */
    private boolean isPermissionSuccess(int[] grantResults) {
        boolean isSuccess = true;

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isSuccess = false;
                break;
            }
        }
        return isSuccess;
    }

    /**
     * 获取拍照后的图片的uri
     */
    protected Uri getImageUri() {
        return Uri.fromFile(new File(FileUtils.getImagePath(mContext), upImageName));
    }

    //=================业务方法 End==================//

    //============启动方法==========//
    public static void launch(Context context) {
        Intent intent = new Intent(context, PublishActivity.class);
        context.startActivity(intent);
    }

    public static void launchWithActivity(Context context, ActivitiesBean bean) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("activity", bean);
        context.startActivity(intent);
    }


    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            LogUtils.e("==onPermissionGranted==" + requestCode);
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    openCamera();
                    break;

                default:
                    break;
            }
        }
    };

}
