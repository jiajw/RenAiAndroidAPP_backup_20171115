package com.yousails.chrenai.publish.event;

import com.baidu.location.Poi;
import com.baidu.mapapi.search.core.PoiInfo;

/**
 * Author:WangKunHui
 * Date: 2017/7/24 17:55
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PoiDataChoiceEvent {

    private Poi poi;

    private PoiInfo poiInfo;

    private boolean isDefaultPoi;

    public PoiDataChoiceEvent(Poi poi) {
        this.poi = poi;
        this.isDefaultPoi = true;
    }

    public PoiDataChoiceEvent(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
        this.isDefaultPoi = false;
    }

    public Poi getPoi() {
        return poi;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }

    public PoiInfo getPoiInfo() {
        return poiInfo;
    }

    public void setPoiInfo(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    public boolean isDefaultPoi() {
        return isDefaultPoi;
    }

    public void setDefaultPoi(boolean defaultPoi) {
        isDefaultPoi = defaultPoi;
    }
}
