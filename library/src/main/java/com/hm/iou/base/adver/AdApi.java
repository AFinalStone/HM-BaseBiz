package com.hm.iou.base.adver;

import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by syl on 2018/6/28.
 */
public class AdApi {

    private static AdService getService() {
        return HttpReqManager.getInstance().getService(AdService.class);
    }

    /**
     * 根据广告位获取广告
     *
     * @param adPosition
     * @return
     */
    public static Flowable<BaseResponse<List<AdBean>>> getAdvertiseList(String adPosition) {
        return getService().getAdvertise(adPosition).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}