package com.hm.iou.base.file;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.model.BaseResponse;

import java.io.File;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by hjy on 2018/5/24.
 */

public class FileApi {

    public static Flowable<BaseResponse<FileUploadResult>> uploadFile(File file, Map<String, Object> map) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part partFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return HttpReqManager.getInstance().getService(FileService.class, BaseBizAppLike.getInstance().getFileServer())
                .uploadImage(partFile, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}