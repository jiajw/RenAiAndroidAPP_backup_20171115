package com.yousails.chrenai.person.ui;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.yousails.chrenai.person.bean.AuthorityBean;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.NoDoubleClickUtils;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;

/**
 * Created by Administrator on 2017/9/5.
 */

public class EntrusteSettingActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView moreView;
    private TextView titleView;
    private EditText jobEditText;
    private LinearLayout contentLayout;


    private List<AuthorityBean> authorityList = new ArrayList<AuthorityBean>();
    private ArrayList<String> selectArray = new ArrayList<String>();

    private String[] nameArray ;
    private String[] idArray ;
    private String entrustTitle="";
    private RegisterInforBean registerInforBean;
    private String aId="";//活动id;
    private String bId="";//表单id
    private String type;
    private boolean is_chargeable;
    private String url;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_entrusted_setting);
    }

    @Override
    protected void init() {
        type=getIntent().getStringExtra("type");
        is_chargeable=getIntent().getBooleanExtra("is_chargeable",false);
        registerInforBean=(RegisterInforBean)getIntent().getSerializableExtra("registerInfor");
        if(registerInforBean!=null){
            aId=registerInforBean.getActivity_id();
            bId=registerInforBean.getId();
        }
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("委托设置");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.VISIBLE);

        moreView = (TextView) findViewById(R.id.txt_more);
        moreView.setText("完成");
        moreView.setTextColor(getResources().getColor(R.color.main_blue_color));

        jobEditText = (EditText) findViewById(R.id.job_edit_text);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);

        if(is_chargeable){
            nameArray  = new String[] {"报名管理","超出报名审核","查看取消报名","签到/工时审核","缴费审核" };
            idArray  = new String[] {"manage_applications","check_additional_applications","visit_canceled_applications","check_sign_in","check_payments" };
        }else{
            nameArray  = new String[] {"报名管理","超出报名审核","查看取消报名","签到/工时审核" };
            idArray  = new String[] {"manage_applications","check_additional_applications","visit_canceled_applications","check_sign_in"};
        }

        for (int i = 0; i < nameArray.length; i++) {
            String title=nameArray[i];
            String id=idArray[i];
            View view = LayoutInflater.from(mContext).inflate(R.layout.entrusted_setting_item, null, false);
            TextView titleView = (TextView) view.findViewById(R.id.title_tview);
            ImageView selectView = (ImageView) view.findViewById(R.id.select_imageview);
            titleView.setText(title);
            view.setOnClickListener(new ItemClickListener(id, selectView));
            view.setTag(idArray[i]);
            view.setSelected(false);
            contentLayout.addView(view);
        }


    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    entrustedActivities();
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
                        initView();
                        break;
                    case 2:
                        if("editor".equals(type)){
                            Toast.makeText(mContext,"编辑成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext,"委托成功",Toast.LENGTH_SHORT).show();
                        }
                        setResult(1);
                        finish();
                        break;
                }
            }
        };
    }


    private void initView() {
        jobEditText.setText(entrustTitle);
        if(authorityList!=null&&authorityList.size()>0){
            for (int i = 0; i < authorityList.size(); i++) {
                AuthorityBean authorityBean = authorityList.get(i);
//                String name=authorityBean.getDisplay_name();
                String name=authorityBean.getName();

                for(int j=0;j<nameArray.length;j++){
                    LinearLayout itemView= (LinearLayout)contentLayout.getChildAt(j);
                    String title=(String)itemView.getTag();
//                    String title=((TextView)itemView.findViewById(R.id.title_tview)).getText().toString();
                    ImageView imageView=(ImageView)itemView.findViewById(R.id.select_imageview);
                    if(name.equals(title)){
                        itemView.setSelected(true);
                        selectArray.add(authorityBean.getName());
                        imageView.setBackgroundResource(R.drawable.ic_checkbox_selected);
                        break;
                    }
                }

            }
        }

    }


    /**
     * 获得某个报名表的权限
     */
    @Override
    public void initData() {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络", Toast.LENGTH_SHORT).show();
            return;
        }

        url=ApiConstants.GET_ACTIVITIES_API+"/"+aId+"/applications/"+bId;
        String token = AppPreference.getInstance(mContext).readToken();

        OkHttpUtils.get().url(url).addHeader("Authorization", token).addParams("include","perms").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        entrustTitle=jsonObject.optString("entrust_title");
                        JSONObject permsObject = jsonObject.optJSONObject("perms");

                        JSONArray jsonArray= permsObject.optJSONArray("data");

                        Type type = new TypeToken<ArrayList<AuthorityBean>>() {
                        }.getType();

                        authorityList = new Gson().fromJson(jsonArray.toString(), type);

                        mHandler.sendEmptyMessage(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    class ItemClickListener implements View.OnClickListener {
        private ImageView selectView;//当前点击的view
        private String id;

        public ItemClickListener(String id, ImageView selectView) {
            this.selectView = selectView;
            this.id = id;
        }

        @Override
        public void onClick(View view) {
           boolean isSelected= view.isSelected();
            if(isSelected){
                view.setSelected(false);
                selectArray.remove(id);
                selectView.setBackgroundResource(R.drawable.ic_checkbox_unselected);
            }else{
                view.setSelected(true);
                selectArray.add(id);
                selectView.setBackgroundResource(R.drawable.ic_checkbox_selected);
            }

        }
    }



    /**
     * 委托活动
     */
    private void entrustedActivities(){
        String content=jobEditText.getText().toString().trim();
        if(StringUtil.isEmpty(content)){
            Toast.makeText(mContext,"请输入委托标题",Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectArray.size()==0){
            Toast.makeText(mContext,"请选择委托活动选项",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!NetUtil.detectAvailable(mContext)){
            Toast.makeText(mContext,"请连接网络",Toast.LENGTH_SHORT).show();
            return;
        }

        String url=ApiConstants.GET_COMMENTS_API+aId+"/entrusted/applications/"+bId;
        String token=AppPreference.getInstance(mContext).readToken();

        final FormBody.Builder builder = new FormBody.Builder();

        for (int i=0;i<selectArray.size();i++) {
            builder.add("permission_names["+i+"]",selectArray.get(i));
        }
        builder.add("entrust_title",content);
        builder.add("pid",aId);
        builder.add("id",bId);

        OkHttpUtils.put().url(url).addHeader("Authorization",token).requestBody(builder.build()).build().execute(new StringCallback(){
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
                if("editor".equals(type)){
                    Toast.makeText(mContext,"编辑失败",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"委托失败",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponse(String response, int id) {
                System.out.print(response);
                mHandler.sendEmptyMessage(2);
            }
        });
    }

}
