package com.yousails.chrenai.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yousails.chrenai.R;

/**
 * Description: TODO
 * author: Wang
 * date: 3/19/17 15:59
 * email:life_artist@163.com
 * Copyright©2017 by wang. All rights reserved.
 */
public class CommonItemDecoration extends RecyclerView.ItemDecoration {

    private String TAG = "CommonItemDecoration";

    private int dividerHeight;

    private Paint dividerPaint;

    private int marginLeftAndRight = 0;

    public CommonItemDecoration(Context context) {
        dividerHeight = ScreenUtil.dip2px(context, 0.5f);
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, R.color.e0e0e0));
    }

    public CommonItemDecoration(Context context, float dividerHeight, int marginLeftAndRight) {
        this.dividerHeight = ScreenUtil.dip2px(context, dividerHeight);
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, R.color.e0e0e0));
    }

    public CommonItemDecoration(Context context, int marginLeftAndRight) {
        dividerHeight = ScreenUtil.dip2px(context, 0.5f);
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, R.color.e0e0e0));
        this.marginLeftAndRight = ScreenUtil.dip2px(context, marginLeftAndRight);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(0, 0, 0, dividerHeight);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft() + marginLeftAndRight;
        int right = parent.getWidth() - parent.getPaddingRight() - marginLeftAndRight;

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();

            float bottom = view.getBottom() + dividerHeight;

            c.drawRect(left, top, right, bottom, dividerPaint);

        }
    }
}
