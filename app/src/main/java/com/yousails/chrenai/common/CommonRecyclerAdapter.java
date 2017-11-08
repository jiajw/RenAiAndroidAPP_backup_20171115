package com.yousails.chrenai.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView适配器基类
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CommonHolder.OnNotifyChangeListener {

    protected List<T> dataList = new ArrayList<>();

    CommonHolder headHolder;

    ViewGroup rootView;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        rootView = parent;
        //设置ViewHolder
        int type = getItemViewType(position);
        return setViewHolder(parent, type);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        runEnterAnimation(holder.itemView, position);
        //数据绑定
        ((CommonHolder) holder).bindData(dataList.get(position));
        ((CommonHolder) holder).setOnNotifyChangeListener(this);
    }

    public ViewGroup getRootView() {
        return rootView;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private int lastAnimatedPosition = -1;
    protected boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(ScreenUtil.dip2px(view.getContext(), 100));//(position+1)*50f
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    }).start();
        }
    }

    @Override
    public void onNotify() {
        //提供给CommonHolder方便刷新视图
        notifyDataSetChanged();
    }

    public void setDataList(List<T> data) {
        dataList.clear();
        if (null != data) {
            dataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加数据到前面
     */
    public void addItemsAtFront(List<T> datas) {
        if (null == datas) return;
        dataList.addAll(0, datas);
        notifyDataSetChanged();
    }

    /**
     * 添加数据到尾部
     */
    public void addItems(List<T> datas) {
        if (null == datas) return;
        dataList.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 添加单条数据
     */
    public void addItem(T data) {
        if (null == data) return;
        dataList.add(data);
        notifyDataSetChanged();
    }

    /**
     * 删除单条数据
     */
    public void deletItem(T data) {
        dataList.remove(data);
        Log.d("deletItem: ", dataList.remove(data) + "");
        notifyDataSetChanged();
    }

    /**
     * 子类重写实现自定义ViewHolder
     */
//    public abstract CommonHolder<T> setViewHolder(ViewGroup parent);
    public abstract CommonHolder<T> setViewHolder(ViewGroup parent, int type);

    public class HeadHolder extends CommonHolder<Void> {
        public HeadHolder(View itemView) {
            super(itemView);
        }

        public HeadHolder(Context context, ViewGroup root, int layoutRes) {
            super(context, root, layoutRes);
        }

        @Override
        public void bindData(Void aVoid) {//不用实现
        }
    }
}