
package com.lanou.lilyxiao.myapplication.util;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Images;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 系统工具类。
 */
public class SystemUtil {

    // 小屏大小定义（小于这个大小的都定义为小屏幕）
    public static final int SMALL_SCREEN_THRESHOLD = 400;

    /**
     * 获得设备density
     * 
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 拨打电话。将跳到电话页面，并填入号码。
     * 
     * @param context
     * @param tel 电话号码
     */
    public static void dial(Context context, String tel) {
        String toTell = "tel:";
        toTell += tel;
        Uri callUri = Uri.parse(toTell);
        Intent it = new Intent(Intent.ACTION_DIAL, callUri);
        try {
            context.startActivity(it);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "号码不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据指定url 打开浏览器
     * 
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "浏览器不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开短信控制台 并且填充内容
     * 
     * @param context
     * @param msg
     */
    public static void openSMS(Context context, String msg) {
        try {
            Intent intent = null;
            if (Build.VERSION.SDK_INT > 19 - 1) {
                String smsPackageName = get44DefaultSmsPackage(context);
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                intent.setPackage(smsPackageName);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra("sms_body", msg);
                intent.setType("vnd.android-dir/mms-sms");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "短信未发现", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开短信控制台 指定电话号码 并且填充内容
     * 
     * @param context
     * @param phoneNum
     * @param msg
     */
    public static void openSMS(Context context, String phoneNum, String msg) {
        try {
            Intent intent = null;
            if (Build.VERSION.SDK_INT > 19 - 1) {
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNum)));
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, msg);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra("address", phoneNum);
                intent.putExtra("sms_body", msg);
                intent.setType("vnd.android-dir/mms-sms");
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "短信未发现", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 不经过短信控制台 发送短信
     * 
     * @param targetPhoneNum 目标短信号码
     * @param msg 短信内容
     */
    public static void sendSM(String targetPhoneNum, String msg) {
        try {
            SmsManager.getDefault().sendTextMessage(targetPhoneNum, null, msg, null, null);
        } catch (Exception e) {
        }
    }

    /**
     * 保存到图片相册
     * 
     * @param title
     * @param name
     * @param filePath
     * @param bitmap
     * @param context
     */
    public static void storeImgToAlbum(String title, String name, String filePath, Bitmap bitmap,
            Context context) {
        // 插入到系统图库，系统会以路径最后一级目录名展现在图库里
        final Uri STORAGE_URI = Images.Media.EXTERNAL_CONTENT_URI;
        final String IMAGE_MIME_TYPE = "image/jpg";

        ContentValues values = new ContentValues(7);

        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, name);
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(Images.Media.MIME_TYPE, IMAGE_MIME_TYPE);
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);// 图片路径
        values.put(Images.Media.SIZE, bitmap2Bytes(bitmap).length);

        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.insert(STORAGE_URI, values);

        // 把图片存到相册文件夹
        ContentResolver cr = context.getContentResolver();
        Images.Media.insertImage(cr, bitmap, title, name);
    }

    /**
     * 获取Android 4.4默认短信包名
     * 
     * @param context
     * @return
     */
    private static String get44DefaultSmsPackage(Context context) {
        String smsPackageName = null;
        try {
            Class c = Context.class.getClassLoader().loadClass("android.provider.Telephony$Sms");
            Method m = c.getMethod("getDefaultSmsPackage", Context.class);
            Object o = m.invoke(c, context);
            smsPackageName = (String)o;
        } catch (Exception e) {
        }
        return smsPackageName;
    }

    /**
     * 把Bitmap转Byte
     */
    private static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    /**
     * 得到手机的IMSI码
     * 
     * @param context
     */
    public static String getIMSI(Context context) {
        TelephonyManager telManager = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);
         return telManager.getSubscriberId();
    }

    /**
     * 得到手机号码
     * 
     * @param context
     * @return 有可能得不到 返回null
     */
    public static String getPhoneNum(Context context) {
        try {
            TelephonyManager telManager = (TelephonyManager)context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return telManager.getLine1Number();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 保存位图到sd卡
     * 
     * @param ctx
     * @param bitmap
     * @param path
     * @param fileName must use .png to name the file
     */
    public static File saveBitmapToSDCard(Context ctx, Bitmap bitmap, String path, String fileName,File appDir) {

        File file = null;
        if (appDir != null) {
            File dir = new File(appDir, path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file = new File(dir, fileName);
            if (file.exists()) {
                file.delete();
            }
        } else {
            file = new File(ctx.getFilesDir(), fileName);
            if (file.exists()) {
                file.delete();
            }
        }

        FileOutputStream out = null;
        try {
            if (appDir != null) {
                out = new FileOutputStream(file);
            } else {
                out = ctx.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            }
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
                throw new Exception("write fail!");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }




    public static final int SETTING_WIFI = 0;

    public static final int SETTING_GPS = 1;

    public static final int SETTING_AUTO = 2;


    public static String getDeviceId(Context context) {
        try {
            TelephonyManager telephonyMgr = (TelephonyManager)context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyMgr.getDeviceId();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 添加快捷方式
     * 
     * @param intentActivityClass
     */
    public static void addShortcut(Class<? extends Activity> intentActivityClass,Context context) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "应用");
        shortcut.putExtra("duplicate", false);

        Intent shortcutIntent = new Intent(context, intentActivityClass);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(context,
//                R.drawable.icon);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        context.sendBroadcast(shortcut);
    }

    // public static String getStringDateShort() {
    // Date currentTime = new Date();
    // SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    // String dateString = formatter.format(currentTime);
    // return dateString;
    // }

    // public static String getStringDateHour() {
    // Date currentTime = new Date();
    // SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
    // String dateString = formatter.format(currentTime);
    // return dateString;
    // }

    public static String getStringDateFormat(String format) {
        // TODO: SimpleDateFormat是非线程安全的类，这里暂时利用try catch， 下期优化可以加锁控制
        String dateString = "";
        try {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            dateString = formatter.format(currentTime);
        } catch (NumberFormatException e) {
            // Do Nothing
        }
        return dateString;
    }

    /**
     * 获取屏幕宽度
     * 
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     * 
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 判断是否是竖屏
     * 
     * @return
     */
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 获取顶栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
        }
        return statusBarHeight;
    }


    /**
     * 得到opengles的版本
     * 
     * @param context
     * @return
     */
    public static int getOpenglesVersion(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion;
    }

    /**
     * 获得运维提供的网络模块需要的appid
     * 
     * @param context
     * @return
     */
    public static String getBeaconAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("APPKEY_DENGTA");
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

}
