package com.hm.iou.base.file

import com.hm.iou.sharedata.model.BaseResponse
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by hjy on 2018/5/24.
 */

interface FileService {

    @Multipart
    @POST("/api/fs/v1/upload")
    fun upload(@Part file: MultipartBody.Part, @QueryMap params: Map<String, Int>): Flowable<BaseResponse<FileUploadResult>>

    @Streaming
    @GET
    fun downLoadFile(@Url url: String): Flowable<ResponseBody>

    //suspend 函数，在 coroutine 中使用
    @Multipart
    @POST("/api/fs/v1/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part, @QueryMap params: Map<String, Int>): BaseResponse<FileUploadResult>

}
