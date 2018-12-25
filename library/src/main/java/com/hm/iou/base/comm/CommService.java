package com.hm.iou.base.comm;

import com.hm.iou.sharedata.model.BaseResponse;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by hjy on 18/12/24.<br>
 *
 * 通用API服务
 */
public interface CommService {

    @POST("http://192.168.1.108:3000/api/iou/share/v1/shareX")
    Flowable<BaseResponse<Object>> reportShareResult(@Body ReportShareReqBean shareReqBean);

}