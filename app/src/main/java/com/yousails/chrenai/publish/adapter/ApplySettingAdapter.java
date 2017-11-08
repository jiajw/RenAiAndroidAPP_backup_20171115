package com.yousails.chrenai.publish.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yousails.chrenai.R;
import com.yousails.chrenai.common.CommonHolder;
import com.yousails.chrenai.common.CommonRecyclerAdapter;
import com.yousails.chrenai.publish.bean.ActiveApplyItem;
import com.yousails.chrenai.publish.event.ApplySettingEvent;
import com.yousails.common.event.EventBusManager;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 11:49
 * Desc:
 * E-mail:life_artist@163.com
 */
public class ApplySettingAdapter extends CommonRecyclerAdapter<ActiveApplyItem> {

    private String TAG = "ApplySettingAdapter";

    @Override
    public CommonHolder<ActiveApplyItem> setViewHolder(ViewGroup parent, int type) {
        return new ItemHolder(parent);
    }

    public void removePosition(int position) {
        if (dataList.size() > position) {
            dataList.remove(position);

            notifyItemRemoved(position);
        }
    }

    private class ItemHolder extends CommonHolder<ActiveApplyItem> {

        private SwipeMenuLayout rootItem;

        private LinearLayout rootOption;

        public ItemHolder(ViewGroup root) {
            super(root.getContext(), root, R.layout.adapter_item_apply_setting);
        }

        @Override
        public void bindView() {
            rootItem = (SwipeMenuLayout) itemView.findViewById(R.id.item_root);
            rootOption = (LinearLayout) itemView.findViewById(R.id.option_root);
        }

        @Override
        public void bindData(ActiveApplyItem singleWarp) {

            if (singleWarp == null) {
                return;
            }

            if (singleWarp.isAddView()) {
                rootItem.setVisibility(View.GONE);
                rootOption.setVisibility(View.VISIBLE);

                rootOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBusManager.getInstance().post(new ApplySettingEvent(ApplySettingEvent.TYPE_ADD));
                    }
                });
            } else {
                rootItem.setVisibility(View.VISIBLE);
                rootOption.setVisibility(View.GONE);

                LinearLayout itemContainer = (LinearLayout) itemView.findViewById(R.id.ll_container);

                ImageView logo = (ImageView) itemView.findViewById(R.id.iv_logo);
                TextView content = (TextView) itemView.findViewById(R.id.tv_content);
                TextView isMast = (TextView) itemView.findViewById(R.id.tv_is_mast);

                Button delBtn = (Button) itemView.findViewById(R.id.btn_del);

                rootItem.setSwipeEnable(true);
                if (singleWarp.getTitle().equals("姓名")) {
                    logo.setImageResource(R.drawable.ic_person);
                    rootItem.setSwipeEnable(false);
                } else if (singleWarp.getTitle().equals("电话")) {
                    logo.setImageResource(R.drawable.ic_phone_number);
                    rootItem.setSwipeEnable(false);
                } else {
                    switch (singleWarp.getType()) {
                        case ActiveApplyItem.TYPE_SINGLE_INPUT:
                            logo.setImageResource(R.mipmap.publish_single_input);
                            break;
                        case ActiveApplyItem.TYPE_SINGLE_CHOICE:
                            logo.setImageResource(R.mipmap.publish_single_choice);
                            break;
                        case ActiveApplyItem.TYPE_MULTI_INPUT:
                            logo.setImageResource(R.mipmap.publish_multi_input);
                            break;
                        case ActiveApplyItem.TYPE_MULTI_CHOICE:
                            logo.setImageResource(R.mipmap.publish_multi_choice);
                            break;
                    }
                }

                content.setText(singleWarp.getTitle() + "");
                if (singleWarp.isRequired()) {
                    isMast.setText("必填项");
                    isMast.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.publish_picture_choice_text_color_blue));
                } else {
                    rootItem.setIos(true);
                    isMast.setText("非必填");
                    isMast.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.publish_picture_choice_text_color_gray));
                }


                itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int layoutPosition = getLayoutPosition();
                        if (layoutPosition >= 2) {
                            EventBusManager.getInstance().post(new ApplySettingEvent(ApplySettingEvent.TYPE_MODIFY, getLayoutPosition()));
                        }
                    }
                });

                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBusManager.getInstance().post(new ApplySettingEvent(ApplySettingEvent.TYPE_DEL, getLayoutPosition()));
                    }
                });
            }
        }
    }
}
