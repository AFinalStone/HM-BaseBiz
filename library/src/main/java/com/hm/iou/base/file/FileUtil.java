package com.hm.iou.base.file;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hm.iou.base.constants.HMConstants;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.functions.Consumer;

/**
 * @author : syl
 * @Date : 2018/6/8 22:57
 * @E-Mail : shiyaolei@dafy.com
 */
public class FileUtil {

    /**
     * 保存图片
     *
     * @param activity
     * @param pictureUrl
     */
    public static void savePicture(final Activity activity, final String pictureUrl) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //保存图片
                    downloadPic(activity.getApplicationContext(), pictureUrl);
                } else {
                    Toast.makeText(activity, "请开启读写手机存储权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void downloadPic(final Context context, final String url) {
        toastResultShort(context, "开始保存图片，请稍候...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = getImageStream(url);
                    if (is != null) {
                        File dir = new File(Environment.getExternalStorageDirectory(), HMConstants.IMAGE_CACHE_DIR);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, System.currentTimeMillis() + ".jpg");
                        saveInputStreamToFile(is, file);

                        //通知扫描存储卡设备
                        Uri uri = Uri.fromFile(file);
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                        toastResult(context, "图片保存路径为" + file.getAbsolutePath());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                toastResult(context, "图片保存失败");
            }
        }).start();
    }

    private static void toastResult(final Context context, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void toastResultShort(final Context context, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    private static boolean saveInputStreamToFile(InputStream is, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
