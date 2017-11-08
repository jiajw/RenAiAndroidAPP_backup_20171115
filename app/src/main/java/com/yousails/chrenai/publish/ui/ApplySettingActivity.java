package com.yousails.chrenai.publish.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.publish.adapter.ApplySettingAdapter;
import com.yousails.chrenai.publish.bean.ActiveApplyItem;
import com.yousails.chrenai.publish.event.ApplySettingEvent;
import com.yousails.chrenai.publish.util.SimpleDialog;
import com.yousails.chrenai.publish.widget.SimpleTitleView;
import com.yousails.common.event.EventBusManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 11:34
 * Desc: 报名设置
 * E-mail:life_artist@163.com
 */
public class ApplySettingActivity extends BaseActivity {

    public static final int type_single_input = 1;

    public static final int type_multi_input = 2;

    public static final int type_single_choice = 3;

    public static final int type_multi_choice = 4;

    private final int requestCodeAddApply = 1;

    public static String APPLY_ITEMS = "applyItems";

    private List<ActiveApplyItem> items;

    private SimpleTitleView titleView;

    private RecyclerView itemContainer;

    private ApplySettingAdapter mAdapter;

    private DialogPlus addDialog;

    @Override
    protected void setContentView() {
        EventBusManager.getInstance().register(this);
        setContentView(R.layout.activity_apply_setting);
    }

    @Override
    protected void init() {

        items = (List<ActiveApplyItem>) getIntent().getSerializableExtra(APPLY_ITEMS);
        if (items == null) {
            items = new ArrayList<>();
        }

        items.add(0, new ActiveApplyItem("电话", ActiveApplyItem.TYPE_SINGLE_INPUT, true));
        items.add(0, new ActiveApplyItem("姓名", ActiveApplyItem.TYPE_SINGLE_INPUT, true));

        if (items == null) {
            LogUtil.i(TAG, "数据获取失败");
            finish();
            return;
        }

        ActiveApplyItem addView = new ActiveApplyItem();
        addView.setAddView(true);
        items.add(addView);

    }

    @Override
    protected void findViews() {
        titleView = (SimpleTitleView) findViewById(R.id.title_view);
        itemContainer = (RecyclerView) findViewById(R.id.item_container);
    }

    @Override
    protected void initViews() {
        mAdapter = new ApplySettingAdapter();
        itemContainer.setLayoutManager(new LinearLayoutManager(mContext));

        HorizontalDividerItemDecoration build = new HorizontalDividerItemDecoration.Builder(
                this).size(1).color(R.color.e0e0e0).build();
        itemContainer.addItemDecoration(build);
        itemContainer.setAdapter(mAdapter);

        mAdapter.setDataList(items);
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
                Intent intent = new Intent();

                items.remove(0);
                items.remove(0);
                items.remove(items.size() - 1);

                intent.putExtra(APPLY_ITEMS, (Serializable) items);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    @Subscribe
    public void onEvent(ApplySettingEvent event) {
        if (event == null) {
            return;
        }

        int position;

        switch (event.getType()) {
            case ApplySettingEvent.TYPE_ADD:
                showAddDialog();
                break;
            case ApplySettingEvent.TYPE_DEL:
                position = event.getPosition();

                items.remove(position);
                mAdapter.removePosition(position);

                break;
            case ApplySettingEvent.TYPE_MODIFY:
                position = event.getPosition();

                ActiveApplyItem item = items.get(position);
                item.setPosition(position);

                AddApplyItemActivity.launchForResult(mContext, item, requestCodeAddApply);
                break;
        }
    }

    private void showAddDialog() {
        if (addDialog == null) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.widget_add_apply_dalog,
                    null);


            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    switch (view.getId()) {
                        case R.id.item_single_input:
                            openAddActivity(ActiveApplyItem.TYPE_SINGLE_INPUT);
                            break;
                        case R.id.item_multi_input:
                            openAddActivity(ActiveApplyItem.TYPE_MULTI_INPUT);
                            break;
                        case R.id.item_single_choice:
                            openAddActivity(ActiveApplyItem.TYPE_SINGLE_CHOICE);
                            break;
                        case R.id.item_multi_choice:
                            openAddActivity(ActiveApplyItem.TYPE_MULTI_CHOICE);
                            break;
                        case R.id.item_cancel:
                            if (addDialog != null && addDialog.isShowing()) {
                                addDialog.dismiss();
                            }
                            break;
                    }
                }
            };
            addDialog = SimpleDialog.create(mContext, inflate, listener);
        }
        addDialog.show();
    }

    private void openAddActivity(String type) {
        if (addDialog != null && addDialog.isShowing()) {
            addDialog.dismiss();
        }

        ActiveApplyItem item = new ActiveApplyItem();
        item.setType(type);
        AddApplyItemActivity.launchForResult(mContext, item, requestCodeAddApply);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case requestCodeAddApply:
                    ActiveApplyItem item = (ActiveApplyItem) data.getSerializableExtra(
                            AddApplyItemActivity.EXTRA_APPLY_ITEM);
                    if (item.getPosition() != -1) {
                        //修改
                        items.remove(item.getPosition());
                        items.add(item.getPosition(), item);
                    } else {
                        //新增
                        items.add(items.size() - 1, item);
                    }
                    mAdapter.setDataList(items);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBusManager.getInstance().unregister(this);
    }

    public static void launchForResult(Activity activity, List<ActiveApplyItem> data,
                                       int requestCode) {
        Intent intent = new Intent(activity, ApplySettingActivity.class);
        intent.putExtra(APPLY_ITEMS, (Serializable) data);
        activity.startActivityForResult(intent, requestCode);
    }
}
