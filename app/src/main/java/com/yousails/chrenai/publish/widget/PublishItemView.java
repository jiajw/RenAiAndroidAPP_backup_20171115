package com.yousails.chrenai.publish.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.publish.event.PublishItemEvent;
import com.yousails.common.event.EventBusManager;

/**
 * Author:WangKunHui
 * Date: 2017/7/12 14:39
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PublishItemView extends RelativeLayout {

    private String TAG = "PublishItemView";

    //文本输入
    private final int type_input = 1;

    //item不带剪头
    private final int type_choice = 2;

    //item右边带有剪头
    private final int type_select = 3;

    //选择是否收费
    private final int type_charge = 4;

    //签到
    private final int type_sign = 5;

    private String itemName;

    private int itemType;

    private String itemInputHint;

    private String itemChoiceHint;

    private String itemSelectHint;

    //输入框
    private EditText inputView;

    //item名称
    private TextView activeName;

    //item描述
    private TextView itemDescView;

    //收费
    private TextView isChargeYes;

    //不收费
    private TextView isChargeNo;

    //扫码两次
    private TextView signByScanTwice;

    //扫码一次
    private TextView signByScanOnce;

    //手工签到
    private TextView signByManual;

    private LinearLayout helpContainer;

    private LinearLayout nameContainer;

    private TextView onceScanDesc;

    private String signType;

    private String onceSelectedTime;

    /**
     * 是否收费
     */
    private Boolean isChargeStatus = null;

    public PublishItemView(Context context) {
        this(context, null);
    }

    public PublishItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PublishItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.widget_publish_item, this, true);

        setBackgroundResource(android.R.color.white);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        getAttrs(context, attrs);
        showViewByType();

    }

    private void showViewByType() {
        activeName = (TextView) findViewById(R.id.tv_item_name);
        activeName.setText(itemName + "：");

        switch (itemType) {
            case type_input:
                findViewById(R.id.rl_input_root).setVisibility(View.VISIBLE);
                inputView = (EditText) findViewById(R.id.et_input_view);
                inputView.setHint(itemInputHint + "");

                if (getResources().getString(R.string.publish_active_member_limit).equals(itemName)) {
                    inputView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                }

                break;
            case type_choice:
                setClickable(true);
                setBackgroundResource(R.drawable.selector_normal_white_gray_bg);
                findViewById(R.id.rl_choice_or_select_root).setVisibility(View.VISIBLE);
                itemDescView = (TextView) findViewById(R.id.tv_desc);
                if (!TextUtils.isEmpty(itemChoiceHint)) {
                    itemDescView.setText(itemChoiceHint);
                }

                break;
            case type_select:
                setClickable(true);
                setBackgroundResource(R.drawable.selector_normal_white_gray_bg);
                findViewById(R.id.rl_choice_or_select_root).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_right_arrow).setVisibility(View.VISIBLE);

                itemDescView = (TextView) findViewById(R.id.tv_desc);
                if (!TextUtils.isEmpty(itemSelectHint)) {
                    itemDescView.setText(itemSelectHint);
                }
                break;
            case type_charge:
                findViewById(R.id.rl_charge_choice_root).setVisibility(View.VISIBLE);
                isChargeYes = (TextView) findViewById(R.id.tv_yes);
                isChargeNo = (TextView) findViewById(R.id.tv_no);

                setChargeSelected();
                break;
            case type_sign:

                nameContainer = (LinearLayout) findViewById(R.id.ll_name_container);
                onceScanDesc = (TextView) findViewById(R.id.tv_once_scan_desc);

                onceScanDesc.setVisibility(View.INVISIBLE);

                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.topMargin = (int) getResources().getDimension(R.dimen.publish_sign_top_margin);
                params.leftMargin = (int) getResources().getDimension(R.dimen.publish_active_item_left_margin);
                nameContainer.setLayoutParams(params);
                findViewById(R.id.rl_sign_root).setVisibility(View.VISIBLE);

                helpContainer = (LinearLayout) findViewById(R.id.ll_help_container);
                helpContainer.setVisibility(View.VISIBLE);

                helpContainer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBusManager.getInstance().post(new PublishItemEvent(PublishItemEvent.TYPE_HELP));
                    }
                });

                signByScanTwice = (TextView) findViewById(R.id.tv_scan_twice);
                signByScanOnce = (TextView) findViewById(R.id.tv_scan_once);
                signByManual = (TextView) findViewById(R.id.tv_manual_sign);

                //默认扫码两次
//                signByScanTwice.setSelected(true);
                setSignSelectListener();
                break;
        }
    }

    public void setOnceSelected(String time) {
        if (TextUtils.isEmpty(time)) {
            return;
        }

        this.onceSelectedTime = time;
        if (onceScanDesc != null) {
            onceScanDesc.setText(time);
        }
        setSignSelector("once");
    }

    public String getOnceSelectedTime() {
        return onceSelectedTime;
    }

    public void setSignSelector(String choice) {

        signType = choice;

        switch (choice) {
            case "twice":
                signByScanTwice.setSelected(true);
                signByScanOnce.setSelected(false);
                signByManual.setSelected(false);

                if (onceScanDesc != null) {
                    onceScanDesc.setVisibility(View.INVISIBLE);
                }
                break;
            case "once":
                signByScanTwice.setSelected(false);
                signByScanOnce.setSelected(true);
                signByManual.setSelected(false);

                if (onceScanDesc != null) {
                    onceScanDesc.setVisibility(View.VISIBLE);
                }
                break;
            case "manual":
                signByScanTwice.setSelected(false);
                signByScanOnce.setSelected(false);
                signByManual.setSelected(true);

                if (onceScanDesc != null) {
                    onceScanDesc.setVisibility(View.INVISIBLE);
                }
                break;

        }
    }

    /**
     * 设置签到选择
     */
    private void setSignSelectListener() {
        signByScanTwice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signByScanTwice == null || signByScanOnce == null || signByManual == null) {
                    LogUtil.e(TAG, "signByScanTwice or signByScanOnce or signByManual is null!");
                    return;
                }

                if (signByScanTwice.isSelected()) {
                    return;
                }

                setSignSelector("twice");
            }
        });

        signByScanOnce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signByScanTwice == null || signByScanOnce == null || signByManual == null) {
                    LogUtil.e(TAG, "signByScanTwice or signByScanOnce or signByManual is null!");
                    return;
                }

                EventBusManager.getInstance().post(new PublishItemEvent(PublishItemEvent.TYPE_ONCE_SCAN));
            }
        });

        signByManual.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signByScanTwice == null || signByScanOnce == null || signByManual == null) {
                    LogUtil.e(TAG, "signByScanTwice or signByScanOnce or signByManual is null!");
                    return;
                }

                if (signByManual.isSelected()) {
                    return;
                }

                setSignSelector("manual");
            }
        });
    }

    /**
     * 设置选择是否收费
     */
    private void setChargeSelected() {
        isChargeYes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChargeYes == null || isChargeNo == null) {
                    LogUtil.e(TAG, "isChargeYes or isChargeNo is null!");
                    return;
                }

                if (isChargeYes.isSelected()) {
                    return;
                }

                isChargeStatus = true;

                isChargeYes.setSelected(true);
                isChargeNo.setSelected(false);
            }
        });

        isChargeNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChargeYes == null || isChargeNo == null) {
                    LogUtil.e(TAG, "isChargeYes or isChargeNo is null!");
                    return;
                }

                if (isChargeNo.isSelected()) {
                    return;
                }

                isChargeStatus = false;

                isChargeYes.setSelected(false);
                isChargeNo.setSelected(true);
            }
        });
    }

    public Boolean isCharge() {
        return isChargeStatus;
    }

    public void setCharge(Boolean isCharge) {
        if (isCharge != null) {
            isChargeStatus = isCharge;

            if (isChargeStatus) {
                isChargeYes.setSelected(true);
                isChargeNo.setSelected(false);
            } else {
                isChargeYes.setSelected(false);
                isChargeNo.setSelected(true);
            }
        }
    }

    public String getSignType() {
        return signType;
    }

    /**
     * 获取输入框中输入的文字
     *
     * @return
     */
    public String getInputText() {
        if (inputView != null) {
            return inputView.getText().toString().trim();
        }
        return null;
    }

    public void descClear() {

        if (inputView != null && inputView.getVisibility() == View.VISIBLE) {
            inputView.setText("");
        }

        if (itemDescView != null && itemDescView.getVisibility() == View.VISIBLE) {
            itemDescView.setText("-请选择-");
        }
    }

    /**
     * 设置选择后的文本
     *
     * @param desc
     */
    public void setItemDesc(String desc) {
        if (TextUtils.isEmpty(desc)) {
            return;
        }
        if (inputView != null && inputView.getVisibility() == View.VISIBLE) {
            inputView.setText(desc);
        }
        if (itemDescView != null && itemDescView.getVisibility() == View.VISIBLE) {
            itemDescView.setText(desc);
        }
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PublishItemView);

        itemName = typedArray.getString(R.styleable.PublishItemView_item_name);
        itemType = typedArray.getInt(R.styleable.PublishItemView_item_type, 0);
        itemInputHint = typedArray.getString(R.styleable.PublishItemView_item_input_hint);
        itemChoiceHint = typedArray.getString(R.styleable.PublishItemView_item_choice_hint);
        itemSelectHint = typedArray.getString(R.styleable.PublishItemView_item_select_hint);

        typedArray.recycle();
    }

}
