package com.hm.iou.base.adver

import com.hm.iou.sharedata.model.BaseResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author syl
 * @time 2018/8/28 下午12:03
 */
interface AdService {

    @GET("/pay/iou/v1/ad/getByPosition")
    fun getAdvertise(@Query("adPosition") adPosition: String): Flowable<BaseResponse<List<AdBean>>>

    @GET("/pay/iou/v1/ad/getByPosition")
    suspend fun getAdvertiseByCoroutine(@Query("adPosition") adPosition: String): BaseResponse<List<AdBean>>
}