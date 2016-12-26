package com.lanou.lilyxiao.myapplication;

import android.hardware.Camera;
import android.view.SurfaceHolder;

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
public class CameraOperationHelper {

    public static synchronized CameraOperationHelper getInstance() {
        return new CameraOperationHelper();
    }

    /*打开相机
    * @param callback 相机回调
    * @param holder 相机显示矩柄
    * */
    public void doOpenCamera(CameraOverCallback callback, SurfaceHolder holder) {

    }

    private Camera getCameraInstance(int mCameraId) {
        return null;
    }

    public void doStartPreview() {

    }

    public void doStopPreview() {

    }

    public void doTakePicture(int x, int y, int width, int height) {

    }

    public void doCameraErrorreport(String errMsg) {

    }

    public void doSwitchCameraRate(CameraOverCallback callback) {

    }

    public void doSwitchFlashMode(CameraOverCallback callback) {

    }

    public void doSwitchCameraFacing(CameraOverCallback callback) {

    }

    public boolean checkHasFrontCamera() {
        return false;
    }

    public void doAutoFocus() {

    }

    public void doAutoFocusBeforeTakePhoto() {

    }

}
