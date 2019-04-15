package com.hm.iou.base.comm;

import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.PersonalCenterInfo;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by hjy on 18/12/24.<br>
 * <p>
 * 通用API服务
 */
public interface CommService {

    @POST("/api/iou/share/v1/shareX")
    Flowable<BaseResponse<Object>> reportShareResult(@Body ReportShareReqBean shareReqBean);

    @POST("/api/base/ref/v1/short/gen")
    Flowable<BaseResponse<String>> getShortLink(@Body ShortLinkReqBean shortLinkReqBean);

    @GET("/api/iou/user/v1/getPersonalCenter")
    Flowable<BaseResponse<PersonalCenterInfo>> getPersonalCenter();

    @POST("/api/base/shearPlate/v1/powerSearch")
    Flowable<BaseResponse<PowerSearchResult>> powerSearch(@Body PowerSearchReqBean data);
}