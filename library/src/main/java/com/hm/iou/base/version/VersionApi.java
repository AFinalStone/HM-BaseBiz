package com.hm.iou.base.version;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.model.BaseResponse;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VersionApi {

    /**
     * 检验是否需要更新
     *
     * @return
     */
    public static Flowable<BaseResponse<CheckVersionResBean>> checkVersion() {
        String apiServer = BaseBizAppLike.getInstance().getApiServer();
        apiServer = apiServer.replace("https", "http");
        VersionService service = HttpReqManager.getInstance().getService(VersionService.class, apiServer);
        return service.checkVersion().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
