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
    fun upload(@Part file: MultipartBody.Part, @QueryMap params: Map<String, Any>): Flowable<BaseResponse<FileUploadResult>>

    @Streaming
    @GET
    fun downLoadFile(@Url url: String): Flowable<ResponseBody>

}
