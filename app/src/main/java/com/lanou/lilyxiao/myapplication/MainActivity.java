package com.lanou.lilyxiao.myapplication;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "test");
        init();
    }

    @SuppressLint("NewApi")
    public void init(){
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String[] ids = cameraManager.getCameraIdList();
            for (int i = 0; i < ids.length; i++) {
                Log.d("MainActivity", ids[i]);
            }


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
}
