package com.rong.lcdbusview.tools;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * http下载工具类
 */
public class HttpUtils {
    private static final AtomicReference<HttpUtils> INSTANCE = new AtomicReference<>();
    private final OkHttpClient mOkHttpClient;

    private final static int CONNECT_TIMEOUT = 30;
    private final static int WRITE_TIMEOUT = 60;
    private final static int READ_TIMEOUT = 60;

    /**
     * @return HttpUtil实例对象
     */
    public static HttpUtils getInstance() {
        for (; ; ) {
            HttpUtils current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new HttpUtils();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    private HttpUtils(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        mOkHttpClient  = builder.build();
    }

    /**
     * 异步请求
     */
    private void doAsync(Request request, Callback callback) throws IOException {
        //创建请求会话
        Call call = mOkHttpClient.newCall(request);
        //同步执行会话请求
        call.enqueue(callback);
    }

    /**
     * 同步请求
     */
    private Response doSync(Request request) throws IOException {

        //创建请求会话
        Call call = mOkHttpClient.newCall(request);
        //同步执行会话请求
        return call.execute();
    }

    /**
     * 异步请求
     */
    public void postAsync(String url,String contentType,String requestMsg ,Callback callback) throws IOException {
        RequestBody body = FormBody.create(MediaType.parse(contentType), requestMsg);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        doAsync(request,callback);
    }

    public void postAsyncAndHead(String url,String session,String contentType,String requestMsg ,Callback callback) throws IOException {
        RequestBody body = FormBody.create(MediaType.parse(contentType), requestMsg);
        Request request = new Request.Builder()
                .addHeader("cookie",session)
                .post(body)
                .url(url)
                .build();
        doAsync(request,callback);
    }

    /**
     * 通过GET异步请求下载文件
     */
    public void downloadAsyncFile(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        doAsync(request,callback);
    }

    /**
     * 通过GET同步请求下载文件
     */
    public Response downloadSyncFile(String url) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return doSync(request);
    }
}
