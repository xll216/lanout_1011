package com.lanou.lilyxiao.myapplication.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

/**
 * 进行截屏工具类
 * 
 */
public class ScreenShotUtils {

    /**
     * 进行截取屏幕
     * 
     * @param pActivity
     * @return bitmap
     */
    public static Bitmap takeScreenShot(Activity pActivity) {
        Bitmap bitmap = null;
        View view = pActivity.getWindow().getDecorView();
        // 设置是否可以进行绘图缓存
        view.setDrawingCacheEnabled(true);
        // 如果绘图缓存无法，强制构建绘图缓存
        view.buildDrawingCache();
        // 返回这个缓存视图
        bitmap = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        // 测量屏幕宽和高
        view.getWindowVisibleDisplayFrame(frame);
        int stautsHeight = frame.top;

        int width = pActivity.getWindowManager().getDefaultDisplay().getWidth();
        int height = pActivity.getWindowManager().getDefaultDisplay().getHeight();
        // 根据坐标点和需要的宽和高创建bitmap
        // 由于高度问题在任意检修详情页面点击“求安慰”按钮->弹出提示“很抱歉，路宝已停止运行”后应用crash 
        try{
        bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height - stautsHeight);
        }catch (Exception e) {
			// TODO: handle exception
		}
        return bitmap;
    }

    /**
     * 保存图片到sdcard中
     * 
     * @param pBitmap
     */
    public static boolean savePic(Bitmap pBitmap,File tmpDir) {
        if (pBitmap == null) {
            return false;
        }
        FileOutputStream fos = null;
        File file = null;
        try {
            // 缓存存放目录
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }
            file = new File(tmpDir, "share.png");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            fos = new FileOutputStream(file);
            if (null != fos) {
                pBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                return true;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 截图
     * 
     * @param pActivity
     * @return 截图并且保存sdcard成功返回true，否则返回false
     */
    public static boolean shotBitmap(Activity pActivity,File savePath) {
        return ScreenShotUtils.savePic(takeScreenShot(pActivity),savePath);
    }

    /**
     * 得到保存的图片的Uri
     */
    public static String getBitmapPath(File shareDir) {
        shareBitmapFile = null;
        shareBitmapFile = new File(shareDir, "share.png");
        return shareBitmapFile.getAbsolutePath();
    }

    public static void delTmpShot() {
        try {
            if (null != shareBitmapFile && shareBitmapFile.exists()) {
                shareBitmapFile.delete();
            }
        } catch (Exception e) {
        }
    }

    public static int titleHeight;
    private static File shareBitmapFile;// 用于分享的图片临时保存

}
