package com.hm.iou.base.file;

import android.support.annotation.NonNull;

import com.hm.iou.sharedata.model.BaseResponse;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by hjy on 2018/5/24.
 */

public interface FileService {

    @Multipart
    @POST("/fs/uploadReturnPath")
    Flowable<BaseResponse<FileUploadResult>> uploadImage(@Part MultipartBody.Part file, @QueryMap Map<String, Object> params);

    @Multipart
    @POST("/api/fs/v1/upload")
    Flowable<BaseResponse<FileUploadResult>> upload(@Part MultipartBody.Part file, @QueryMap Map<String, Object> params);


    @Streaming
    @GET
    Flowable<ResponseBody> downLoadFile(@NonNull @Url String url);

}
