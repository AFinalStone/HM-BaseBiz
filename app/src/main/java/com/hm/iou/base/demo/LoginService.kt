package com.hm.iou.base.demo

import com.hm.iou.sharedata.model.BaseResponse
import com.hm.iou.sharedata.model.UserInfo
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by hjy on 2018/5/29.
 */

interface LoginService {

    @POST("/api/iou/user/v1/mobileLogin")
    fun mobileLogin(@Body mobileLoginReqBean: MobileLoginReqBean): Flowable<BaseResponse<UserInfo>>

    @POST("/api/iou/user/v1/mobileLogin")
    suspend fun testMobileLogin(@Body mobileLoginReqBean: MobileLoginReqBean): BaseResponse<UserInfo>


    @POST("/api/iou/user/v1/mobileLogin")
    fun mobileLoginEx(@Body mobileLoginReqBean: MobileLoginReqBean): retrofit2.Call<BaseResponse<UserInfo>>
}