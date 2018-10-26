package com.hm.iou.base.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.hm.iou.tools.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by hjy on 18/5/10.<br>
 */

public class CompressPictureUtil {

    public interface OnCompressListener {
        void onCompressPicSuccess(File file);
    }

    /**
     * 异步方法进行图片压缩
     *
     * @param context          上下文
     * @param picturePath      图片路径
     * @param compressListener
     */
    public static void compressPic(final Context context, String picturePath, final OnCompressListener compressListener) {
        if (context == null || TextUtils.isEmpty(picturePath)) {
            ToastUtil.showMessage(context, "图片获取失败");
            return;
        }
        Flowable.just(picturePath)
                .observeOn(Schedulers.io())
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String path) throws Exception {
                        return Luban.with(context).load(path).get(path);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        if (compressListener != null) {
                            compressListener.onCompressPicSuccess(file);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showMessage(context, "图片压缩失败");
                    }
                });
    }

    /**
     * 同步方法进行图片压缩
     *
     * @param context
     * @param picturePath
     * @return
     */
    public static File compressPic(final Context context, String picturePath) {
        if (context == null || TextUtils.isEmpty(picturePath)) {
            ToastUtil.showMessage(context, "图片获取失败");
            return null;
        }
        File file = null;
        try {
            file = Luban.with(context).load(picturePath).get(picturePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

//    /**
//     * 将图片保存到目标文件路径下
//     *
//     * @param target 保存的文件
//     * @param bitmap 图片
//     * @return 是否保存成功
//     */
//    public static boolean saveBitmapToTargetFile(File target, Bitmap bitmap) {
//        try {
//            FileOutputStream out = new FileOutputStream(target);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 将图片保存到目标文件路径下
     *
     * @param target         保存的文件
     * @param bitmap         图片
     * @param compressFormat 图片格式
     * @return 是否保存成功
     */
    public static boolean saveBitmapToTargetFile(File target, Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(compressFormat, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
