package com.yousails.chrenai.publish.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.Address;
import com.baidu.location.Poi;
import com.baidu.mapapi.search.core.PoiInfo;
import com.yousails.chrenai.R;
import com.yousails.chrenai.common.CommonHolder;
import com.yousails.chrenai.common.CommonRecyclerAdapter;
import com.yousails.chrenai.publish.event.PoiDataChoiceEvent;
import com.yousails.common.event.EventBusManager;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/24 12:19
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PoiAdapter extends CommonRecyclerAdapter {

    private Address address;

    @Override
    public CommonHolder setViewHolder(ViewGroup parent, int type) {
        return new PoiHolder(parent);
    }

    public void setData(List<Poi> data, Address address) {
        this.address = address;
        dataList.clear();
        if (null != data) {
            dataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    private class PoiHolder extends CommonHolder {

        private TextView addressName;

        private TextView addressDesc;

        private LinearLayout addressContainer;

        private View bottomDivider;

        public PoiHolder(ViewGroup root) {
            super(root.getContext(), root, R.layout.adapter_item_poi);
        }

        @Override
        public void bindView() {
            addressName = (TextView) itemView.findViewById(R.id.address_name);
            addressDesc = (TextView) itemView.findViewById(R.id.address_desc);
            addressContainer = (LinearLayout) itemView.findViewById(R.id.address_container);
            bottomDivider = itemView.findViewById(R.id.bottom_divider);
        }

        @Override
        public void bindData(final Object object) {

            if (object != null) {

                if (object instanceof Poi) {
                    final Poi poi = (Poi) object;
                    addressName.setText(poi.getName());
                    if (address != null) {
                        addressDesc.setText(address.district + "");
                    }

                    addressContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBusManager.getInstance().post(new PoiDataChoiceEvent(poi));
                        }
                    });
                } else if (object instanceof PoiInfo) {
                    final PoiInfo poiInfo = (PoiInfo) object;

                    String address = poiInfo.address;
                    String name = poiInfo.name;

                    addressName.setText(name + "");
                    addressDesc.setText(address + "");

                    addressContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBusManager.getInstance().post(new PoiDataChoiceEvent(poiInfo));
                        }
                    });
                }

                if (getLayoutPosition() == dataList.size() - 1) {
                    bottomDivider.setVisibility(View.VISIBLE);
                } else {
                    bottomDivider.setVisibility(View.GONE);
                }
            }
        }
    }
}
