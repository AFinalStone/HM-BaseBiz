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
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.loading.LoadingDialogUtil;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;

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

    /**
     * 下载图片
     *
     * @param context
     * @param url
     */
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

    /**
     * 吐司
     *
     * @param context
     * @param msg
     */
    private static void toastResult(final Context context, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取缓存文件的根目录
     * <p>
     * getCacheDir()方法用于获取/data/data/<application package>/cache目录
     * getFilesDir()方法用于获取/data/data/<application package>/files目录
     * <p>
     * 应用程序在运行的过程中如果需要向手机上保存数据，一般是把数据保存在SDcard中的。
     * 大部分应用是直接在SDCard的根目录下创建一个文件夹，然后把数据保存在该文件夹中。
     * 这样当该应用被卸载后，这些数据还保留在SDCard中，留下了垃圾数据。
     * 如果你想让你的应用被卸载后，与该应用相关的数据也清除掉，该怎么办呢？
     * 通过Context.getExternalFilesDir()方法可以获取到 SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
     * 通过Context.getExternalCacheDir()方法可以获取到 SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
     * 如果使用上面的方法，当你的应用在被用户卸载后，SDCard/Android/data/你的应用的包名/ 这个目录下的所有文件都会被删除，不会留下垃圾信息。
     * <p>
     * <p>
     * 获取外部SD卡的缓存路径，需要访问外部存储设备的权限，如果外部Cache获取失败，会尝试获取内部存储设备的cache
     *
     * @param context
     * @return
     */
    public static String getExternalCacheDirPath(@NonNull Context context) {
        String cachePath = null;
        if (hasSdcard()) {
            File file = context.getExternalCacheDir();
            if (file != null) {
                cachePath = file.getAbsolutePath();
            }
        }
        if (cachePath == null) {
            cachePath = getCacheDirPath(context);
        }
        return cachePath;
    }

    /**
     * 获取APP本身的缓存路径
     *
     * @param context
     * @return
     */
    private static String getCacheDirPath(@NonNull Context context) {
        return context.getCacheDir().getAbsolutePath();
    }


    /**
     * 判断是否存在SD卡
     *
     * @return
     */
    private static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || !Environment.isExternalStorageRemovable();
    }


}
