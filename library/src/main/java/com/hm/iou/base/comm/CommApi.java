package com.hm.iou.base.comm;

import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.PersonalCenterInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hjy on 2018/12/24.
 */
public class CommApi {

    private static CommService getService() {
        return HttpReqManager.getInstance().getService(CommService.class);
    }

    /**
     * 向服务端上报分享结果
     *
     * @param shareReqBean
     * @return
     */
    public static Flowable<BaseResponse<Object>> reportShareResult(ReportShareReqBean shareReqBean) {
        return getService().reportShareResult(shareReqBean).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取个人中心用户的摘要信息
     *
     * @return
     */
    public static Flowable<BaseResponse<PersonalCenterInfo>> getPersonalCenter() {
        return getService().getPersonalCenter().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 向服务端上报分享结果
     *
     * @param bizType      1:电子借条，2:电子收条，3:平台借条，4:纸质借条，5:纸质收条，6:娱乐借条，7:娱乐借条模板，8:资讯文章，9:记债本分享
     * @param umShareMedia
     * @param result       1:成功，2:取消，其他:失败
     * @param errMsg       分享失败的原因
     */
    public static void reportShareResult(int bizType, SHARE_MEDIA umShareMedia, int result, String errMsg) {
        int channel;
        if (umShareMedia == SHARE_MEDIA.WEIXIN) {
            channel = 1;
        } else if (umShareMedia == SHARE_MEDIA.WEIXIN_CIRCLE) {
            channel = 3;
        } else if (umShareMedia == SHARE_MEDIA.SINA) {
            channel = 4;
        } else if (umShareMedia == SHARE_MEDIA.QQ) {
            channel = 2;
        } else {
            //其他不处理
            return;
        }
        ReportShareReqBean reqBean = new ReportShareReqBean();
        reqBean.setBiz(bizType);
        reqBean.setScene(1);
        reqBean.setChannel(channel);
        if (result == 1) {
            reqBean.setMemo("true");
        } else if (result == 2) {
            reqBean.setMemo("cancel");
        } else {
            reqBean.setMemo(errMsg);
        }
        reportShareResult(reqBean).subscribe(new Consumer<BaseResponse<Object>>() {
            @Override
            public void accept(BaseResponse<Object> objectBaseResponse) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    public static Flowable<BaseResponse<String>> getShortLink(String url) {
        ShortLinkReqBean reqBean = new ShortLinkReqBean();
        reqBean.setOriginUrl(url);
        return getService().getShortLink(reqBean).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 搜索
     *
     * @param content 搜索内容
     * @param purpose 搜索用途，未知=0，借条合同=1，附属合同=2，好友=3
     * @return
     */
    public static Flowable<BaseResponse<PowerSearchResult>> powerSearch(String content, int purpose) {
        PowerSearchReqBean data = new PowerSearchReqBean();
        data.setContent(content);
        data.setPurpose(purpose);
        return getService().powerSearch(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}