package com.yousails.chrenai.utils.https;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by liuwen on 2017/8/30.
 */

public class TrustAllCerts implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
}
