package com.yousails.chrenai.publish.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 14:18
 * Desc:
 * E-mail:life_artist@163.com
 */
public class SimpleTitleView extends RelativeLayout {

    private TextView content;

    private ImageView leftIcon;

    private TextView rightText;

    public SimpleTitleView(Context context) {
        this(context, null);
    }

    public SimpleTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.widget_simple_title, this, true);
        leftIcon = (ImageView) findViewById(R.id.iv_left_icon);
        content = (TextView) findViewById(R.id.tv_title_content);
        rightText = (TextView) findViewById(R.id.tv_right_text);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleTitleView);
        String string = typedArray.getString(R.styleable.SimpleTitleView_title_content);

        typedArray.recycle();

        if (TextUtils.isEmpty(string)) {
            content.setText(string);
        }
    }

    public void setTitle(int res) {
        content.setText(res);
    }

    public void setOnLeftClickListener(OnClickListener listener) {
        leftIcon.setOnClickListener(listener);
    }

    public void setOnRightClickListener(OnClickListener listener) {
        rightText.setOnClickListener(listener);
    }
}
