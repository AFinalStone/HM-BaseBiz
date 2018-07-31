package com.hm.iou.base.file;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hm.iou.base.constants.HMConstants;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.loading.LoadingDialogUtil;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
                    downloadPic(activity, pictureUrl);
                } else {
                    toastResult(activity, "请开启读写手机存储权限");
                }
            }
        });
    }

    /**
     * 保存图片
     *
     * @param activity
     * @param bitmap
     */
    public static void savePicture(final Activity activity, final Bitmap bitmap) {
        if (bitmap == null)
            return;
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    try {
                        //保存图片
                        File dir = new File(Environment.getExternalStorageDirectory(), "54jietiao" + File.separator + "image");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, System.currentTimeMillis() + ".jpg");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                        //通知扫描存储卡设备
                        Uri uri = Uri.fromFile(file);
                        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        toastResult(activity, "图片保存路径为" + file.getAbsolutePath());
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toastResult(activity, "图片保存失败");
                } else {
                    toastResult(activity, "请开启读写手机存储权限");
                }
            }
        });
    }

    private static void downloadPic(final Activity context, final String url) {
        final Dialog dialog = LoadingDialogUtil.showLoading(context, "图片保存中...", false);
        Flowable.just(url)
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {
                        Bitmap bmp = Picasso.get().load(url).get();
                        File dir = new File(Environment.getExternalStorageDirectory(), "54jietiao" + File.separator + "image");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, System.currentTimeMillis() + ".jpg");
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                        return file;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        dialog.dismiss();

                        //通知扫描存储卡设备
                        Uri uri = Uri.fromFile(file);
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                        ToastUtil.showMessage(context, "图片保存路径为" + file.getAbsolutePath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dialog.dismiss();
                        ToastUtil.showMessage(context, "图片保存失败");
                    }
                });
    }

    private static void toastResult(final Context context, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

}
