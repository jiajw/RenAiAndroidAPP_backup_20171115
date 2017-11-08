package com.yousails.chrenai.publish.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.CommonItemDecoration;
import com.yousails.chrenai.common.GsonUtils;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.common.SharedPreferencesUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.net.HttpRequestUtil;
import com.yousails.chrenai.net.listener.ModelRequestListener;
import com.yousails.chrenai.publish.adapter.CategoryAdapter;
import com.yousails.chrenai.publish.bean.Category;
import com.yousails.chrenai.publish.bean.CategoryParser;
import com.yousails.chrenai.publish.event.CategoryClickEvent;
import com.yousails.common.event.EventBusManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 10:52
 * Desc: 选择活动从属Activity
 * E-mail:life_artist@163.com
 */
public class CategoryChoiceActivity extends BaseActivity {

    public static String CATEGORY_EXTRA = "categoryExtra";

    private ImageView btnBack;

    private RecyclerView categoryContainer;

    private CategoryAdapter mAdapter;

    private Category selectedCategory;

    @Override
    protected void setContentView() {
        EventBusManager.getInstance().register(this);
        setContentView(R.layout.activity_active_category);
    }

    @Override
    protected void init() {
        selectedCategory = (Category) getIntent().getSerializableExtra(CATEGORY_EXTRA);
    }

    @Override
    protected void initViews() {

        mAdapter = new CategoryAdapter(selectedCategory);
        categoryContainer.setLayoutManager(new LinearLayoutManager(mContext));
        categoryContainer.addItemDecoration(new CommonItemDecoration(mContext, 0.5f, 0));
        categoryContainer.setAdapter(mAdapter);

        CategoryParser categoryParser = SharedPreferencesUtil.getObject(mContext, ApiConstants.SharedKey.CATEGORY_KEY, CategoryParser.class);
        if (categoryParser != null && categoryParser.getData() != null) {
            mAdapter.setDataList(categoryParser.getData());
        }

        requestCategory();
    }

    private void requestCategory() {

        HttpRequestUtil.getInstance(ApiConstants.GET_CATEGORIES_API).getModel(CategoryParser.class, new ModelRequestListener<CategoryParser>() {
            @Override
            public void onSuccess(CategoryParser categoryParser) {
                LogUtil.i(TAG, "HttpRequestUtil  :  " + GsonUtils.toJson(categoryParser));
                if (categoryParser != null && categoryParser.getData() != null) {
                    List<Category> list = categoryParser.getData();
                    list.remove(0);
                    mAdapter.setDataList(list);
                    categoryParser.setData(list);
                    SharedPreferencesUtil.setObject(mContext, ApiConstants.SharedKey.CATEGORY_KEY, categoryParser);
                }
            }

            @Override
            public void onFailure(String message) {
                LogUtil.e(TAG, "message  :  " + message);
            }
        });
    }

    @Override
    protected void findViews() {
        btnBack = (ImageView) findViewById(R.id.iv_back);
        categoryContainer = (RecyclerView) findViewById(R.id.category_container);
    }

    @Override
    protected void setListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishWithResult();
            }
        });
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        EventBusManager.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(CategoryClickEvent event) {
        if (event == null) {
            return;
        }

        selectedCategory = event.getCategory();
        onFinishWithResult();
    }

    private void onFinishWithResult() {

        if (selectedCategory != null) {
            Intent intent = new Intent();
            intent.putExtra(CATEGORY_EXTRA, selectedCategory);

            setResult(RESULT_OK, intent);
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        onFinishWithResult();
    }

    public static void launchForResult(Activity activity, Category category, int requestCode) {
        Intent intent = new Intent(activity, CategoryChoiceActivity.class);
        if (category != null) {
            intent.putExtra(CATEGORY_EXTRA, category);
        }
        activity.startActivityForResult(intent, requestCode);
    }
}
