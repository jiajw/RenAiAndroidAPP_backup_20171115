package com.yousails.chrenai.home.adapter;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yousails.chrenai.R;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.db.BannerDBService;
import com.yousails.chrenai.home.bean.BannerBean;
import com.yousails.chrenai.home.ui.BannerActivity;
import com.yousails.chrenai.utils.GlideImageLoader;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.UiUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.yousails.chrenai.config.ModelApplication.mContext;

/**
 * Created by Administrator on 2017/7/3.
 */

public class BannderHolder extends RecyclerView.ViewHolder{
    private List<String> imageUrl = new ArrayList<String>();
    private List<String> titles = new ArrayList<String>();
    private List<BannerBean> bannerLists = new ArrayList<BannerBean>();
    private BannerDBService bannerDBService;
    private Handler mHandler;
    private Banner banner;
    public BannderHolder(View headerView) {
        super(headerView);
        banner = (Banner)headerView.findViewById(R.id.banner);
        banner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtil.SCREEN_WIDTH /3));
       if(bannerDBService==null){
           bannerDBService=new BannerDBService(mContext);
       }
        handleMessage();
    }

    private void startBanner() {
        //设置banner样式(显示圆形指示器)
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置指示器位置（指示器居右）
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(imageUrl);
        //设置banner动画效果
        // banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(2500);
        //banner设置方法全部调用完毕时最后调用
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent=new Intent(mContext,BannerActivity.class);
                intent.putExtra("url",bannerLists.get(position).getLink());
                intent.putExtra("title",bannerLists.get(position).getTitle());
                intent.putExtra("image",bannerLists.get(position).getImage());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

        });

        banner.start();
    }

    public void bindItem(){
        getCarousels();
    }

    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        startBanner();
                        break;

                }
            }
        };
    }

    /**
     * 轮播图片
     */
    private void getCarousels() {

        OkHttpUtils.get().url(ApiConstants.GET_CAROUSELS_API).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, String response, Exception e, int id) {

                if(bannerDBService!=null){
                    bannerLists= bannerDBService.getBannerList();
                    if(bannerLists!=null&&bannerLists.size()>0){
                        imageUrl.clear();
                        titles.clear();
                        for(int i=0;i<bannerLists.size();i++){
                            imageUrl.add(bannerLists.get(i).getImage());
                            titles.add(bannerLists.get(i).getTitle());
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.optJSONArray("data");


                        Type type = new TypeToken<ArrayList<BannerBean>>() {}.getType();

                        ArrayList<BannerBean> bannerList = new Gson().fromJson(jsonArray.toString(), type);
                        imageUrl.clear();
                        titles.clear();
                        bannerLists.clear();
                        if (bannerList != null && bannerList.size() > 0&&bannerDBService!=null) {
                            for (int i = 0; i < bannerList.size(); i++) {
                                BannerBean bannerBean = bannerList.get(i);
                                bannerLists.add(bannerBean);
                                imageUrl.add(bannerBean.getImage());
                                titles.add(bannerBean.getTitle());
                                //需插入到数据库
                                bannerDBService.insertBanner(bannerBean);
                            }

                            mHandler.sendEmptyMessage(1);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
