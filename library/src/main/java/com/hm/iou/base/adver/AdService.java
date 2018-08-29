package com.hm.iou.base.adver;

import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author syl
 * @time 2018/8/28 下午12:03
 */
public interface AdService {

    @GET("/pay/iou/v1/ad/getByPosition")
    Flowable<BaseResponse<List<AdBean>>> getAdvertise(@Query("adPosition") String adPosition);

}