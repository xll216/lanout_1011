package com.lanou.lilyxiao.myapplication.camera;

import android.graphics.Point;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lanou.lilyxiao.myapplication.R;
import com.lanou.lilyxiao.myapplication.activity.BaseActivity;

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

public class Camera2Activity extends BaseActivity implements CameraInterface.CamOpenOverCallback {
    private CameraSurfaceView surfaceView;
    private ImageButton shutterBtn;
    float previewRate = -1f;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera2;
    }

    @Override
    protected void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                CameraInterface.getInstance().doOpenCamera(Camera2Activity.this);
            }
        }).start();

        initUI();
        initViewParams();
    }

    @Override
    protected void setOnClick() {

    }

    @Override
    protected void initData() {

    }

    private void initUI() {
        surfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
    }

    private void initViewParams() {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        surfaceView.setLayoutParams(params);

        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        ViewGroup.LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);
        ;
        shutterBtn.setLayoutParams(p2);

    }

    @Override
    protected void initListener() {
        shutterBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        CameraInterface.getInstance().doTakePicture();
    }

    @Override
    public void cameraHasOpened() {
        SurfaceHolder holder = surfaceView.getHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate);
    }
}
