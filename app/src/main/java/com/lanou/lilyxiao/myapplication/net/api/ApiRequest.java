package com.lanou.lilyxiao.myapplication.net.api;

import com.lanou.lilyxiao.myapplication.bean.LatestEntiry;
import com.lanou.lilyxiao.myapplication.bean.ThemesEntity;
import com.lanou.lilyxiao.myapplication.net.base.ApiCallback;
import com.lanou.lilyxiao.myapplication.net.base.HttpRequestUtil;
import com.lanou.lilyxiao.myapplication.net.base.RetrofitCallback;
import com.lanou.lilyxiao.myapplication.util.Constant;

import retrofit2.Call;
import retrofit2.http.GET;

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
 * 获取最新消息
 */
public class ApiRequest {
    private ServiceAPI mServiceAPI;
    private static ApiRequest instance;

    public static ApiRequest getInstance() {
        if (instance == null) {
            instance = new ApiRequest();
        }
        return instance;
    }

    private ApiRequest() {
        mServiceAPI = HttpRequestUtil.getInstance().create(ServiceAPI.class,
                Constant.APIURL.BASE_API_URL);
    }

    public void call(ApiCallback<LatestEntiry> callback) {
        Call<LatestEntiry> mCall = mServiceAPI.call();
        mCall.enqueue(new RetrofitCallback<LatestEntiry>(callback));
    }

    public void callThemes(ApiCallback<ThemesEntity> callback) {
        Call<ThemesEntity> mCall = mServiceAPI.callThemes();
        mCall.enqueue(new RetrofitCallback<ThemesEntity>(callback));
    }
}
