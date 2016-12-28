package com.lanou.lilyxiao.myapplication.activity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanou.lilyxiao.myapplication.R;
import com.lanou.lilyxiao.myapplication.bean.LatestEntiry;
import com.lanou.lilyxiao.myapplication.bean.ThemesEntity;
import com.lanou.lilyxiao.myapplication.net.base.ApiCallback;
import com.lanou.lilyxiao.myapplication.net.api.ApiRequest;
import com.lanou.lilyxiao.myapplication.util.PicassoUtils;

public class MainActivity extends BaseActivity {
    private TextView start;
    private ImageView mImageView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        start = (TextView) findViewById(R.id.start);
        mImageView = (ImageView) findViewById(R.id.imageView);
        String path ="http://img4.duitang.com/uploads/item/201501/12/20150112022149_YNCGt.thumb.700_0.jpeg";
        PicassoUtils.loadImageViewSize(this,path,600,600,mImageView);
    }

    @Override
    protected void setOnClick() {
        start.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:
                ApiRequest request = ApiRequest.getInstance();
                request.call(new ApiCallback<LatestEntiry>() {
                    @Override
                    public void onSuccess(LatestEntiry response) {
                        Log.d("MainActivity", response.getDate());
                    }

                    @Override
                    public void onError(String err_msg) {
                        Log.d("MainActivity", err_msg);

                    }

                    @Override
                    public void onFailure() {

                    }
                });

                request.callThemes(new ApiCallback<ThemesEntity>() {
                    @Override
                    public void onSuccess(ThemesEntity response) {
                        Log.d("MainActivity", "response.getLimit():" + response.getLimit());
                    }

                    @Override
                    public void onError(String err_msg) {

                    }

                    @Override
                    public void onFailure() {

                    }
                });


                break;
        }
    }
}
