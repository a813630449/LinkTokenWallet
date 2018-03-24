package com.xiaoliang.wallet.utils;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkhttpUtils {
    private static Proxy proxy;
    private static OkHttpClient sOkHttpClient;
    private static OkHttpClient sProxyOkHttpClient;

    public static void initProxy(String ip, int port) {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    }

    public static void stopProxy() {
        proxy = null;
    }

    public static OkHttpClient getClient() {
        if (sOkHttpClient == null) {
            sOkHttpClient = getClientFollowRedirects(true);
        }
        return sOkHttpClient;
    }
    public static OkHttpClient getProxyClient(String ip,int port) {
    	if (sProxyOkHttpClient == null) {
    		sProxyOkHttpClient = getProxyClientFollowRedirects(true,ip,port);
    	}
    	return sProxyOkHttpClient;
    }

    public static OkHttpClient getClientFollowRedirects(boolean follow) {
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.followRedirects(follow).followSslRedirects(follow);
        mBuilder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
        return getClient(mBuilder);
    }
    public static OkHttpClient getProxyClientFollowRedirects(boolean follow,String ip,int port) {
    	OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
    	mBuilder.followRedirects(follow).followSslRedirects(follow);
    	mBuilder.connectTimeout(30, TimeUnit.SECONDS)
    	.readTimeout(30, TimeUnit.SECONDS)
    	.writeTimeout(30, TimeUnit.SECONDS);
    	return getProxyClient(mBuilder,ip,port);
    }
    

    public static OkHttpClient getClient(OkHttpClient.Builder builder) {
        if (proxy != null) {
            builder.proxy(proxy);
        }
        return builder.build();
    }
    public static OkHttpClient getProxyClient(OkHttpClient.Builder builder,String ip,int port) {
    	if (proxy == null) {
    		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    		builder.proxy(proxy);
    	}
    	return builder.build();
    }
}
