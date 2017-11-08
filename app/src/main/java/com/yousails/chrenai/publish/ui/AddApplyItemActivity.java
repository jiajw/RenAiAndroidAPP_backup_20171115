package com.yousails.chrenai.publish.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.CommonItemDecoration;
import com.yousails.chrenai.common.ToastUtils;
import com.yousails.chrenai.publish.adapter.OptionsAdapter;
import com.yousails.chrenai.publish.bean.ActiveApplyItem;
import com.yousails.chrenai.publish.widget.SimpleTitleView;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 14:33
 * Desc:
 * E-mail:life_artist@163.com
 */
public class AddApplyItemActivity extends BaseActivity {

    /**
     * 传递选项参数的key
     */
    public static String EXTRA_APPLY_ITEM = "extraApplyItem";

    private SimpleTitleView titleView;

    private LinearLayout necessaryView;

    private LinearLayout notNecessaryView;

    private Button necessaryButton;

    private Button notNecessaryButton;

    private EditText inputView;

    private ImageView inputClear;

    private TextView desc;

    private LinearLayout choiceContainer;

    private RecyclerView optionsContainer;

    private OptionsAdapter optionsAdapter;

    private KPSwitchPanelLinearLayout panelLinearLayout;

    private boolean isNecessary = true;

    private List<String> optionsResults = new ArrayList<>();

    private ActiveApplyItem activeApplyItem;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_add_apply_item);
    }

    @Override
    protected void init() {

        activeApplyItem = (ActiveApplyItem) getIntent().getSerializableExtra(EXTRA_APPLY_ITEM);

        if (activeApplyItem == null) {
            finish();
            return;
        }
    }

    @Override
    protected void findViews() {
        titleView = (SimpleTitleView) findViewById(R.id.title_view);

        necessaryView = (LinearLayout) findViewById(R.id.ll_necessary);
        notNecessaryView = (LinearLayout) findViewById(R.id.ll_not_necessary);
        necessaryButton = (Button) findViewById(R.id.btn_necessary);
        notNecessaryButton = (Button) findViewById(R.id.btn_not_necessary);

        inputView = (EditText) findViewById(R.id.et_input_view);
        inputClear = (ImageView) findViewById(R.id.iv_input_clear);

        desc = (TextView) findViewById(R.id.tv_desc);
        choiceContainer = (LinearLayout) findViewById(R.id.ll_choice_container);
        optionsContainer = (RecyclerView) findViewById(R.id.item_container);

        panelLinearLayout = (KPSwitchPanelLinearLayout) findViewById(R.id.panel_root);

    }

    @Override
    protected void initViews() {

        KeyboardUtil.attach(this, panelLinearLayout,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {

                    }
                });

        if (!TextUtils.isEmpty(activeApplyItem.getTitle())) {
            inputView.setText(activeApplyItem.getTitle());
        }

        isNecessary = activeApplyItem.isRequired();

        switch (activeApplyItem.getType()) {
            case ActiveApplyItem.TYPE_SINGLE_INPUT:
                titleView.setTitle(R.string.apply_single_input);
                desc.setText("需要填写较少的信息，如微信号、邮箱、职位等");
                desc.setVisibility(View.VISIBLE);
                choiceContainer.setVisibility(View.GONE);
                break;
            case ActiveApplyItem.TYPE_MULTI_INPUT:
                titleView.setTitle(R.string.apply_multi_input);
                desc.setText("需要填写较多的信息，如个人简介、项目介绍、团队介绍等");
                desc.setVisibility(View.VISIBLE);
                choiceContainer.setVisibility(View.GONE);
                break;
            case ActiveApplyItem.TYPE_SINGLE_CHOICE:
                titleView.setTitle(R.string.apply_single_choice);
                desc.setVisibility(View.GONE);
                choiceContainer.setVisibility(View.VISIBLE);

                setDefaultData();
                break;
            case ActiveApplyItem.TYPE_MULTI_CHOICE:
                titleView.setTitle(R.string.apply_multi_choice);
                desc.setVisibility(View.GONE);
                choiceContainer.setVisibility(View.VISIBLE);

                setDefaultData();
                break;
        }

        setRadioGroupStatus();

    }

    /**
     * 设置选项列表
     */
    private void setDefaultData() {
        optionsAdapter = new OptionsAdapter(activeApplyItem.getOptions());
        optionsContainer.setLayoutManager(new LinearLayoutManager(mContext));
        optionsContainer.addItemDecoration(new CommonItemDecoration(mContext));
        optionsContainer.setAdapter(optionsAdapter);
    }

    /**
     * 设置单选按钮组状态
     */
    private void setRadioGroupStatus() {
        if (isNecessary) {
            necessaryButton.setSelected(true);
            notNecessaryButton.setSelected(false);
        } else {
            necessaryButton.setSelected(false);
            notNecessaryButton.setSelected(true);
        }
    }

    @Override
    protected void setListeners() {
        titleView.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResult();
            }
        });

        necessaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNecessary) {
                    isNecessary = true;
                    setRadioGroupStatus();
                }
            }
        });

        notNecessaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNecessary) {
                    isNecessary = false;
                    setRadioGroupStatus();
                }
            }
        });

        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    inputClear.setVisibility(View.INVISIBLE);
                } else {
                    inputClear.setVisibility(View.VISIBLE);
                }
            }
        });

        inputClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputView.setText("");
            }
        });
    }

    /**
     * 获取结果
     */
    private void getResult() {

        String content = inputView.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            finish();
            return;
        }

        ActiveApplyItem item = new ActiveApplyItem();
        item.setType(activeApplyItem.getType());
        item.setRequired(isNecessary);

        item.setTitle(content);
        item.setPosition(activeApplyItem.getPosition());

        if (activeApplyItem.getType().equals(ActiveApplyItem.TYPE_SINGLE_CHOICE) || activeApplyItem.getType().equals(ActiveApplyItem.TYPE_MULTI_CHOICE)) {
            if (optionsAdapter != null) {
                optionsResults.clear();
                int itemCount = optionsAdapter.getItemCount();
                for (int i = 0; i < itemCount - 1; i++) {
                    View childAt = optionsContainer.getChildAt(i);
                    String trim = ((EditText) childAt.findViewById(R.id.et_input_view)).getText().toString().trim();

                    if (!TextUtils.isEmpty(trim)) {
                        optionsResults.add(trim);
                    }
                }

                if (optionsResults.size() < 2) {
                    ToastUtils.showShort(mContext, "请设置两个以上的答案选项");
                    return;
                }

                item.setOptions(optionsResults);
            }
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_APPLY_ITEM, item);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    public static void launchForResult(Activity activity, ActiveApplyItem item, int requestCode) {
        Intent intent = new Intent(activity, AddApplyItemActivity.class);
        intent.putExtra(EXTRA_APPLY_ITEM, item);
        activity.startActivityForResult(intent, requestCode);
    }
}
