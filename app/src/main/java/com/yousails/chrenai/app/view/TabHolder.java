package com.yousails.chrenai.app.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;


public class TabHolder extends ViewHolder<Object, Object> {
    private final static int TAB_COUNT = 3;
    public final static String[] names = new String[]{"首页", "发布","我的"};
    private final static int[] defaultRes = new int[]{
            R.drawable.ic_home_normal, R.drawable.ic_publish_normal,
            R.drawable.ic_me_normal};
    private final static int[] pressRes = new int[]{
            R.drawable.ic_home_selected, R.drawable.ic_publish_selected,
            R.drawable.ic_me_selected};
    private Context context;
    private int currentPosition = -1;
    private static final int ID = 997;
    private static final int INDEX_3 = 3;

    public TabHolder(Context context) {
        super(null);
        this.context = context;
        initViews(context);
    }

    public interface OnSwitchListener {
        void onSwitch(int position);
    }

    public void setOnSwitchListener(final OnSwitchListener switchListener) {
        LinearLayout linearLayout = (LinearLayout) mRootView;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            final int index = i;
           /* String tag = "";
            switch (index) {
                case 0:
                    tag = MobclickListener.ClickTag.TAB_INDEX;
                    break;
                case 1:
                    tag = MobclickListener.ClickTag.TAB_CLASSROOM;
                    break;
                case 2:
                    tag = MobclickListener.ClickTag.TAB_DISCOVERY;
                    break;
                case 3:
                    tag = MobclickListener.ClickTag.TAB_MY;
                    break;
                default:
                    break;
            }
            linearLayout.getChildAt(index).setOnClickListener(
                    new MobclickListener(context, tag) {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            setCurrentState(index);
                            if (switchListener != null) {
                                switchListener.onSwitch(index);
                            }
                        }
                    });*/
            linearLayout.getChildAt(index).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCurrentState(index);
                    if (switchListener != null) {
                        switchListener.onSwitch(index);
                    }
                }
            });
        }

    }

    public void setCurrentState(int position) {
        if (currentPosition == position) {
            return;
        }
        currentPosition = position;
        resetState(currentPosition);
    }

    private void resetState(int currentPosition) {
        LinearLayout layout = (LinearLayout) mRootView;
        for (int i = 0; i < layout.getChildCount(); i++) {
            TextView item = (TextView) layout.getChildAt(i).findViewById(i);
            if (i == currentPosition) {
                item.setTextColor(Color.parseColor("#1f9eff"));
                item.setCompoundDrawablesWithIntrinsicBounds(null, context
                        .getResources().getDrawable(pressRes[i]), null, null);
            } else {
                item.setTextColor(Color.parseColor("#666666"));
                item.setCompoundDrawablesWithIntrinsicBounds(null, context
                        .getResources().getDrawable(defaultRes[i]), null, null);
            }
        }
    }

    private void initViews(Context context) {
        LinearLayout root = new LinearLayout(context);
        root.setPadding(0, 15, 0, 10);
        root.setBackgroundColor(Color.WHITE);
        root.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < TAB_COUNT; i++) {
            View item = getTabItem(context, defaultRes[i], names[i], i);
            root.addView(item, i);
        }
        mRootView = root;
        // setCurrentState(0);
    }

    private View getTabItem(Context context, int resid, String text, int index) {
        RelativeLayout layout = new RelativeLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        layoutParams.gravity = Gravity.CENTER;
        layout.setLayoutParams(layoutParams);
        TextView item = new TextView(context);
        item.setId(index);
        item.setTextColor(Color.parseColor("#666666"));
        // item.setShadowLayer(2, 1, 1, R.color.gray);
        item.setCompoundDrawablesWithIntrinsicBounds(null, context
                .getResources().getDrawable(resid), null, null);
        item.setCompoundDrawablePadding(6);
        item.setGravity(Gravity.CENTER);
        item.setTextSize(12);
        item.setText(text);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        item.setLayoutParams(textParams);
//        if (index == INDEX_3) {
//            addIcon(layout, index);
//        }
        layout.addView(item);
        return layout;
    }

   /* private void addIcon(RelativeLayout layout, int index) {
        ImageView imageView = new ImageView(context);
        imageView.setVisibility(View.GONE);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageView.setImageResource(R.drawable.my_ico_new);
        imageParams.addRule(RelativeLayout.RIGHT_OF, index);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageParams.rightMargin = 5;
        imageView.setLayoutParams(imageParams);
        imageView.setId(ID + index);
        layout.addView(imageView);
    }*/

   /* public void showIcon(boolean show) {
        LinearLayout layout = (LinearLayout) mRootView;
        try {
            layout.getChildAt(INDEX_3).findViewById(ID + INDEX_3)
                    .setVisibility(show ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
