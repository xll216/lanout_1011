package com.lanou.lilyxiao.myapplication.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 */

public class OkHttpUtils {

    //TAG
    private static final String TAG = OkHttpUtils.class.getSimpleName();

    //声明客户端
    private OkHttpClient client;
    //防止多个线程同时访问所造成的安全隐患
    private volatile static OkHttpUtils okHttpUtils;
    //定义提交类型Json
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //定义提交类型String
    private static final MediaType STRING = MediaType.parse("text/x-markdown;charset=utf-8");
    //子线程
    private Handler handler;

    //构造方法
    private OkHttpUtils() {
        //初始化
        client = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }


    //单例模式
    public static OkHttpUtils getInstance() {
        OkHttpUtils okUtils = null;
        if (okHttpUtils == null) {
            //线程同步
            synchronized (OkHttpUtils.class) {
                if (okUtils == null) {
                    okUtils = new OkHttpUtils();
                    okHttpUtils = okUtils;
                }
            }
        }
        return okUtils;
    }

    /**
     * 请求的返回结果是json字符串
     *
     * @param jsonValue
     * @param callBack
     */
    private void onsuccessJsonStringMethod(final String jsonValue, final FuncJsonString callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        //解析json
                        callBack.onResponse(jsonValue);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    /**
     * 求的返回结果是json对象
     *
     * @param jsonValue
     * @param callBack
     */
    private void onsuccessJsonObjectMethod(final String jsonValue, final FuncJsonObject callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(new JSONObject(jsonValue));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 求的返回结果是json数组
     *
     * @param data
     * @param callBack
     */
    private void onsuccessJsonByteMethod(final byte[] data, final FuncJsonObjectByte callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onResponse(data);
                }
            }
        });
    }

    /**
     * 同步请求,不是很常用，因为会阻塞线程
     *
     * @param url
     * @return
     */
    public String syncGetByURL(String url) {
        //构建一个Request请求
        Request request = new Request.Builder().url(url).build();
        Response response = null;

        try {
            //同步请求数据
            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {

        }

        return null;
    }


    /**
     * 请求指定的url，返回的结果是json字符串
     *
     * @param url
     * @param callback
     */
    public void syncJsonStringByURL(String url, final FuncJsonString callback) {
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "解析失败");
            }

            //解析成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onsuccessJsonStringMethod(response.body().string(), callback);
                }
            }
        });
    }


    /**
     * 返回字符串json的接口
     */
    interface FuncJsonString {
        //处理我们返回的结果
        void onResponse(String result);
    }

    /**
     * 返回json对象的接口
     */
    interface FuncJsonObject {
        //处理我们返回的结果
        void onResponse(JSONObject jsonObject);
    }

    /**
     * 返回json对象的接口
     */
    interface FuncJsonObjectByte {
        //处理我们返回的结果
        void onResponse(byte[] result);
    }

    /**
     * 返回json对象的接口
     */
    interface FuncJsonObjectBitmap {
        //处理我们返回的结果
        void onResponse(Bitmap bitmap);
    }
}
