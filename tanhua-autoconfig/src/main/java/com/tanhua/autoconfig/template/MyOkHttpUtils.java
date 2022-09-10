package com.tanhua.autoconfig.template;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description: OkHttp Client
 * @Author: Spike Wong
 * @Date: 2022/6/6
 */
@Slf4j
public class MyOkHttpUtils {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final  OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS).build();
    private static String jsonOfRequest = null;

    /**
     *  post usage
     * @param url
     * @return
     * @throws IOException
     */
    public String postMethod(String url) throws IOException {
        RequestBody body = RequestBody.create(JSON, jsonOfRequest);
        Request request = new Request.Builder().url(url)
                .addHeader("Host", "")
                .addHeader("Connection", "")
                .addHeader("Accept", "")
                .addHeader("X-Requested-With", "")
                .addHeader("Accept-Language", "")
                .addHeader("Accept-Encoding", "")
                .addHeader("Content-Type", "")
                .addHeader("Origin", "")
                .addHeader("MT-APP-Version", "")
                .addHeader("User-Agent", "")
                .addHeader("Referer", "")
                .addHeader("x-csrf-token", "")
                .addHeader("MT-BIZID", "")
                .addHeader("Cookie", "").post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * get usage
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGetMethod(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}