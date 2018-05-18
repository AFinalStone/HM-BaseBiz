package com.hm.iou.base.photo;

import android.content.Context;
import android.graphics.Bitmap;

import com.hm.iou.tools.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
     * 压缩图片
     *
     * @param context 上下文
     * @param picturePath 图片路径
     * @param compressListener
     */
    public static void compressPic(Context context, String picturePath, OnCompressListener compressListener) {
        Flowable.just(picturePath)
                .observeOn(Schedulers.io())
                .map(path -> Luban.with(context).load(path).get(path))
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
     * 将图片保存到目标文件路径下
     *
     * @param target 保存的文件
     * @param bitmap 图片
     * @return 是否保存成功
     */
    public static boolean saveBitmapToTargetFile(File target, Bitmap bitmap) {
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}