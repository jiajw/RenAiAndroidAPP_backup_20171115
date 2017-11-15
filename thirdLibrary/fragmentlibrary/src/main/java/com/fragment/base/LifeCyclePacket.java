package com.fragment.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by sym on 2/29/16.
 */
public class LifeCyclePacket {
    public SFragment from;
    public Class<?> cls;
    public Bundle data;
    public int code;
    public boolean animated;
    public PacketPreparer listener;

    @Override
    public String toString() {
        return "LifeCyclePacket{" +
                "data=" + data +
                ", code=" + code +
                ", from=" + from +
                ", animated=" + animated +
                '}';
    }

    public static interface PacketPreparer {
        /**
         * 这个地方可以为Fragment设置一些动画
         * @param fragment
         * @param ft
         */
        public void prepare(SFragment fragment, FragmentTransaction ft);
    }

    public static LifeCyclePacket create(SFragment from, Class<?> cls, Bundle data, int code, boolean animated) {

        LifeCyclePacket packet = new LifeCyclePacket();
        packet.from = from;
        packet.cls = cls;
        packet.data = data;
        packet.code = code;
        packet.animated = animated;

        return packet;
    }
}
