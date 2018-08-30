package com.hm.iou.base.file;

import android.support.annotation.NonNull;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.interceptor.file.ProgressListener;
import com.hm.iou.sharedata.model.BaseResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by hjy on 2018/5/24.
 */

public class FileApi {

    /**
     * 旧的上传文件/图片的方式，以后不再使用，这里保留兼容老的代码
     *
     * @param file
     * @param map
     * @return
     */
    public static Flowable<BaseResponse<FileUploadResult>> uploadFile(File file, Map<String, Object> map) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part partFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return HttpReqManager.getInstance().getService(FileService.class, BaseBizAppLike.getInstance().getFileServer())
                .uploadImage(partFile, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传文件
     *
     * @param file
     * @param bizType 文件所属业务类型
     * @return
     */
    public static Flowable<BaseResponse<FileUploadResult>> uploadFile(File file, int bizType) {
        Map<String, Object> map = new HashMap<>();
        map.put("bizType", bizType);
        map.put("fileType", 0);
        return upload(file, map);
    }

    /**
     * 上传文件/图片
     *
     * @param file
     * @param map
     * @return
     */
    public static Flowable<BaseResponse<FileUploadResult>> upload(File file, Map<String, Object> map) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part partFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return HttpReqManager.getInstance().getService(FileService.class)
                .upload(partFile, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传图片
     *
     * @param file
     * @param bizType 图片所属业务类型
     * @return
     */
    public static Flowable<BaseResponse<FileUploadResult>> uploadImage(File file, int bizType) {
        Map<String, Object> map = new HashMap<>();
        map.put("bizType", bizType);
        map.put("fileType", 1);
        return upload(file, map);
    }


    /**
     * 下载文件
     *
     * @param url              文件地址
     * @param destDir          目标文件夹
     * @param fileName         文件名称
     * @param progressListener 下载进度监听对象
     * @return
     */
    public static Flowable<File> downLoadFile(@NonNull String url, final String destDir, final String fileName, ProgressListener progressListener) {
        return HttpReqManager.getInstance().getService(FileService.class, progressListener)
                .downLoadFile(url)
                .subscribeOn(Schedulers.io())//subscribeOn和ObserOn必须在io线程，如果在主线程会出错
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.computation())//需要
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        return saveFile(responseBody, destDir, fileName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存File
     *
     * @param response
     * @param destFileDir
     * @param destFileName
     * @return
     * @throws IOException
     */
    public static File saveFile(ResponseBody response, String destFileDir, String destFileName) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;

        try {
            is = response.byteStream();
            final long total = response.contentLength();
            long currentSize = 0L;
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);

            int len1;
            while ((len1 = is.read(buf)) != -1) {
                currentSize += (long) len1;
                fos.write(buf, 0, len1);
                fos.flush();
            }
            File var11 = file;
            return var11;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}