package com.hm.iou.base.comm;

import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.PersonalCenterInfo;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    /**
     * 搜索剪切板
     *
     * @param content
     * @return
     */
    @GET("/api/base/shearPlate/v1/checkShearPlate")
    Flowable<BaseResponse<ClipBoardBean>> searchClipBoard(@Query("link") String content);

    @GET("/api/base/shearPlate/v1/checkShearPlateOnLable")
    Flowable<BaseResponse<ClipBoardBean>> searchClipBoardOnLabel(@Query("link") String content);


    @GET("/api/coupon/v1/user/coupons")
    Flowable<BaseResponse<List<CouponInfo>>> getCouponList(@Query("scene") int scene);

    @POST("/api/base/msg/v1/sendMessage")
    Flowable<BaseResponse<String>> sendMessage(@Body SendMessageReqBean sendMessageReqBean);

    @POST("/api/behavior/v1/iou/borrow")
    Flowable<BaseResponse<Object>> userBehaviorStatistic(@Body UserBehaviorReqBean reqBean);

    @GET("/api/encrypt/v1/currentRsaPublicKey")
    Flowable<BaseResponse<CurrRsaKeyBean>> getCurrentRsaKey();

}