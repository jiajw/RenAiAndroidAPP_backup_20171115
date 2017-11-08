package com.yousails.chrenai.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.db.SelectedDBService;
import com.yousails.chrenai.login.adapter.SelectAdapter;
import com.yousails.chrenai.login.bean.SelectBean;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/8.
 */

public class DataSelectActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private ListView listView;
    private ArrayList<SelectBean> selectBeanList = new ArrayList<SelectBean>();
    private SelectAdapter selectAdapter;
    private String title;
    private String type;
    private String from;
    private String selected;

    private SelectBean selectedBean;//被选中数据
    private SelectedDBService selectedDBService = null;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_data_select);
    }

    @Override
    protected void init() {

        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        selected = getIntent().getStringExtra("selected");
        from = getIntent().getStringExtra("from");

        selectedDBService = new SelectedDBService(mContext);
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);

        TextView submitView = (TextView) findViewById(R.id.txt_more);
        submitView.setText("提交");

        if (StringUtil.isEmpty(from)) {
            sharedLayout.setVisibility(View.INVISIBLE);
        }

        listView = (ListView) findViewById(R.id.listview);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SelectBean selectBean = (SelectBean) adapterView.getItemAtPosition(i);
                if (selectBeanList != null && selectBeanList.size() > 0) {
                    for (SelectBean bean : selectBeanList) {
                        if (selectBean.getId().equals(bean.getId())) {
                            if (bean.getIsSelected().equals("1")) {
                                bean.setIsSelected("0");
                                selectedBean = null;
                            } else {
                                bean.setIsSelected("1");
                                selectedBean = selectBean;
                            }

                        } else {
                            bean.setIsSelected("0");
                        }
                    }

                    selectAdapter.setDataChanged(selectBeanList);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                setResult();
                break;
            case R.id.btn_more:
                if (selectedBean != null) {
                    updateReligion(selectedBean.getId());
                } else {
                    CustomToast.createToast(mContext, "请选您的择宗教信仰");
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
                    case 0:
//                        Bundle bundle= msg.getData();
//                        String message=bundle.getString("message");

                        CustomToast.createToast(mContext, "更改失败");
                        break;
                    case 1:
                        if (selectAdapter == null) {
                            selectAdapter = new SelectAdapter(mContext, selectBeanList);
                            listView.setAdapter(selectAdapter);
                        } else {
                            selectAdapter.setDataChanged(selectBeanList);
                        }

                        break;
                    case 2:
                        CustomToast.createToast(mContext, "更改成功");

                        if (selectedBean != null) {
                            AppPreference.getInstance(mContext).writeReligion(selectedBean.getName());
                            Intent intent = new Intent();
                            intent.putExtra("religion", selectedBean.getName());
                            setResult(3, intent);
                        }

                        finish();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {

        if (StringUtil.isNotNull(type)) {
            if ("dgrees".equals(type)) {
                selectBeanList = selectedDBService.getDgrees();
            } else {
                selectBeanList = selectedDBService.getReligions();
            }
        }

        if (selectBeanList != null && selectBeanList.size() > 0) {
            if (StringUtil.isNotNull(selected)) {
                for (int i = 0; i < selectBeanList.size(); i++) {
                    SelectBean selectBean = selectBeanList.get(i);
                    if (selected.equals(selectBean.getName())) {
                        selectBean.setIsSelected("1");
                        this.selectedBean = selectBean;
                        break;
                    }
                }
            }
            selectAdapter = new SelectAdapter(mContext, selectBeanList);
            listView.setAdapter(selectAdapter);
        } else {
            getDegrees();
        }

    }

    /**
     * 获取学历数据,宗教数据
     */
    private void getDegrees() {

        if (StringUtil.isNotNull(type)) {
            String url;
            if ("dgrees".equals(type)) {
                url = ApiConstants.GET_DEGREES_API;
            } else {
                url = ApiConstants.GET_RELIGIONS_API;
            }
            OkHttpUtils.get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, String response, Exception e, int id) {
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (StringUtil.isNotNull(response)) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                                    int length = jsonArray.length();
                                    if (length > 0) {
                                        SelectBean selectBean = null;
                                        if (selectBeanList == null) {
                                            selectBeanList = new ArrayList<SelectBean>();
                                        } else {
                                            selectBeanList.clear();
                                        }

                                        for (int i = 0; i < length; i++) {
                                            JSONObject jObject = jsonArray.optJSONObject(i);
                                            selectBean = new SelectBean();
                                            selectBean.setId(jObject.optString("id"));
                                            selectBean.setName(jObject.optString("name"));
                                            selectBean.setIsSelected("0");
                                            selectBeanList.add(selectBean);

                                            if (StringUtil.isNotNull(type)) {
                                                if ("dgrees".equals(type)) {
                                                    selectedDBService.insertDgrees(selectBean);
                                                } else {
                                                    selectedDBService.insertReligions(selectBean);
                                                }
                                            }

                                        }
                                        //保存到数据库
                                        mHandler.sendEmptyMessage(1);
                                    }

                                } catch (Exception e) {
                                    e.toString();
                                }
                            }
                        }
                    });
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            setResult();

        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 回传数据
     */
    private void setResult() {
        if (selectedBean != null) {
            Intent intent = new Intent();
            intent.putExtra("selected", selectedBean);
            setResult(1, intent);
        } else {
            setResult(1);
        }

        finish();
    }


    /**
     * 更改宗教信仰
     */
    private void updateReligion(String rId) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        String token = AppPreference.getInstance(mContext).readToken();

        RequestBody requestBody = new FormBody.Builder().add("religion_id", rId).build();
        OkHttpUtils.patch().url(ApiConstants.UPDATE_USERINFOR_API).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
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
                System.out.println("----->" + response);
                mHandler.sendEmptyMessage(2);
            }
        });
    }
}
