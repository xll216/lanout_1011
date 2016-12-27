package com.lanou.lilyxiao.myapplication.net.base;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;
import retrofit2.Retrofit;

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
public class HttpRequestUtil {

    private static HttpRequestUtil instance;

    public static HttpRequestUtil getInstance() {
        if (instance == null) {
            instance = new HttpRequestUtil();
        }
        return instance;
    }

    private HttpRequestUtil() {
    }

    private Retrofit mRetrofit;

    public <T> T create(Class<T> service, String baseUrl) {
        Log.d("HttpRequestUtil", baseUrl);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new OkHttpInterceptor())
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(RetrofitConvertFactory.create())
                .client(client)
                .build();
        return mRetrofit.create(service);
    }

    private static class RetrofitConvertFactory extends Converter.Factory {
        public static RetrofitConvertFactory create() {
            return create(new Gson());
        }

        public static RetrofitConvertFactory create(Gson gson) {
            return new RetrofitConvertFactory(gson);
        }

        private RetrofitConvertFactory(Gson gson) {
            if (gson == null) {
                throw new NullPointerException("gson == null");
            }
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new GsonResponseBodyConverter<>(type);
        }

        final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
            private final Type type;

            GsonResponseBodyConverter(Type type) {
                this.type = type;
            }

            @Override
            public T convert(ResponseBody value) throws IOException {
                /*解析之后再返回*/
                T t = new Gson().fromJson(value.string(), type);
                return t;
            }
        }
    }

    private static class OkHttpInterceptor implements Interceptor {
        protected String TAG = this.getClass().getSimpleName();
        private static final Charset UTF8 = Charset.forName("UTF-8");

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Connection connection = chain.connection();
            Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            Log.d(TAG, requestStartMessage);

            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();

            if (bodyEncoded(response.headers())) {

            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                Charset charset = UTF8;
                if (contentLength != 0) {
                    Log.d("OkHttpInterceptor", buffer.clone().readString(charset));
                }
            }

            return response;
        }

        private boolean bodyEncoded(Headers headers) {
            String contentEncoding = headers.get("Content-Encoding");
            return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
        }
    }


}
