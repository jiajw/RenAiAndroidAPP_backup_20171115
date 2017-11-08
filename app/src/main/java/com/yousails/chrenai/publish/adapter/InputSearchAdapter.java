package com.yousails.chrenai.publish.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.yousails.chrenai.R;
import com.yousails.chrenai.common.CommonHolder;
import com.yousails.chrenai.common.CommonRecyclerAdapter;
import com.yousails.common.event.EventBusManager;

/**
 * Author:WangKunHui
 * Date: 2017/7/24 16:30
 * Desc:
 * E-mail:life_artist@163.com
 */
public class InputSearchAdapter extends CommonRecyclerAdapter<PoiInfo> {


    @Override
    public CommonHolder<PoiInfo> setViewHolder(ViewGroup parent, int type) {
        return new InputSearchHolder(parent);
    }

    private class InputSearchHolder extends CommonHolder<PoiInfo> {

        private TextView addressName;

        private TextView addressDesc;

        private LinearLayout addressContainer;

        private View bottomDivider;

        private View topDivider;

        public InputSearchHolder(ViewGroup root) {
            super(root.getContext(), root, R.layout.adapter_item_poi);
        }

        @Override
        public void bindView() {
            addressName = (TextView) itemView.findViewById(R.id.address_name);
            addressDesc = (TextView) itemView.findViewById(R.id.address_desc);
            addressContainer = (LinearLayout) itemView.findViewById(R.id.address_container);
            bottomDivider = itemView.findViewById(R.id.bottom_divider);
            topDivider = itemView.findViewById(R.id.top_divider);
        }

        @Override
        public void bindData(final PoiInfo poiInfo) {
            if (poiInfo != null) {
                String address = poiInfo.address;
                String name = poiInfo.name;

                addressName.setText(name + "");
                addressDesc.setText(address + "");

                if (getLayoutPosition() == 0) {
                    topDivider.setVisibility(View.GONE);
                } else {
                    topDivider.setVisibility(View.VISIBLE);
                }

                if (getLayoutPosition() == dataList.size() - 1) {
                    bottomDivider.setVisibility(View.VISIBLE);
                } else {
                    bottomDivider.setVisibility(View.GONE);
                }

                addressContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBusManager.getInstance().post(poiInfo);
                    }
                });
            }
        }
    }
}
