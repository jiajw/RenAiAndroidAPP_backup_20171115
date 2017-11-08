package com.yousails.chrenai.net.listener;


/**
 * Created by sym on 17/7/20.
 */

public interface LoadingRequestListener extends ModelRequestListener {

    void start(long totalBytes);

    void loading(long current, long total, float percent, float speed);

    void finish();
}
