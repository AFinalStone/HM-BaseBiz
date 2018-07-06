package com.hm.iou.base.version;

import com.hm.iou.sharedata.model.BaseResponse;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface VersionService {

    @GET("/api/iou/user/v1/checkVersion")
    Flowable<BaseResponse<CheckVersionResBean>> checkVersion();

}
