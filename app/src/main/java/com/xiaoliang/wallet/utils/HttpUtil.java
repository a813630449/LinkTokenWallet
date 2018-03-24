package com.xiaoliang.wallet.utils;


import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.text.TextUtils;

import com.xiaoliang.wallet.model.WalletApiRequestVo;

public class HttpUtil {
    private static final String WALLET_URL = "https://walletapi.onethingpcs.com";

    public static Response postWalletApi(WalletApiRequestVo data) throws IOException {
        String json = data.toJson();
        return postWalletApi("", json, null);
    }

    public static Response postWalletApi(WalletApiRequestVo data, Headers headers) throws IOException {
        String json = data.toJson();
        return postWalletApi("", json, headers);
    }
    public static Response postWalletApiByProxy(WalletApiRequestVo data, Headers headers,String ip,int port) throws IOException {
        String json = data.toJson();
        return postWalletApiByProxy("", json, headers,ip,port);
    }

    public static Response postWalletApi(String path, WalletApiRequestVo data, Headers headers) throws IOException {
        String json = data.toJson();
        return postWalletApi(path, json, headers);
    }

    public static Response postWalletApi(String path, String data) throws IOException {
        return postWalletApi(path, data, null);
    }

    public static Response postWalletApi(String path, String data, Headers headers) throws IOException {
        String url = WALLET_URL + (TextUtils.isEmpty(path) ? "" : "/" + path);
        OkHttpClient client = OkhttpUtils.getClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if (headers != null) {
            builder.headers(headers);
        }
        builder.addHeader("Content-Type", "application/json")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept", "*/*");
        Response response = client.newCall(builder.build()).execute();
        return response;
    }
    public static Response postWalletApiByProxy(String path, String data, Headers headers,String ip,int port) throws IOException {
    	String url = WALLET_URL + (TextUtils.isEmpty(path) ? "" : "/" + path);
    	OkHttpClient client = OkhttpUtils.getProxyClient(ip, port);
    	MediaType mediaType = MediaType.parse("application/json");
    	RequestBody body = RequestBody.create(mediaType, data);
    	Request.Builder builder = new Request.Builder()
    	.url(url)
    	.post(body);
    	if (headers != null) {
    		builder.headers(headers);
    	}
    	builder.addHeader("Content-Type", "application/json")
    	.addHeader("Accept-Encoding", "gzip, deflate, br")
    	.addHeader("Accept", "*/*");
    	Response response = client.newCall(builder.build()).execute();
    	return response;
    }
}
