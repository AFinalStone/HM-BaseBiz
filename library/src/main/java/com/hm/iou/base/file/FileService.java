package com.hm.iou.base.file;

import com.hm.iou.sharedata.model.BaseResponse;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 * Created by hjy on 2018/5/24.
 */

public interface FileService {

    @Multipart
    @POST("/fs/uploadReturnPath")
    Flowable<BaseResponse<FileUploadResult>> uploadImage(@Part MultipartBody.Part file, @QueryMap Map<String, Object> params);

}
