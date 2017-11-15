package com.yousails.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.common.EventBusManager;
import com.yousails.common.R;
import com.yousails.common.event.PublishTitleClickEvent;

/**
 * Author:WangKunHui
 * Date: 2017/7/12 13:33
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PublishTitleView extends RelativeLayout {

    /**
     * 返回View
     */
    private ImageView btnBack;

    /**
     * 预览View
     */
    private TextView preview;

    /**
     * 发布View
     */
    private TextView publish;

    public PublishTitleView(Context context) {
        this(context, null);
    }

    public PublishTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PublishTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.common_widget_pulish_title, this, true);
        setBackgroundResource(R.color.common_publish_title_bg);

        btnBack = (ImageView) findViewById(R.id.iv_back);
        preview = (TextView) findViewById(R.id.tv_preview);
        publish = (TextView) findViewById(R.id.tv_publish);

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusManager.getInstance().post(new PublishTitleClickEvent(PublishTitleClickEvent.TYPE_BACK));
            }
        });

        preview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusManager.getInstance().post(new PublishTitleClickEvent(PublishTitleClickEvent.TYPE_PREVIEW));
            }
        });

        publish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusManager.getInstance().post(new PublishTitleClickEvent(PublishTitleClickEvent.TYPE_PUBLISH));
            }
        });
    }
}
