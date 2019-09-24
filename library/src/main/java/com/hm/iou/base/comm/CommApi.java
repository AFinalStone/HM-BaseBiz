package com.hm.iou.base.comm;

import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.PersonalCenterInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

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
     * @param bizType      1:电子借条，2:电子收条，3:平台借条，4:纸质借条，5:纸质收条，6:娱乐借条，7:娱乐借条模板，8:资讯文章，9:记债本分享，10:信用卡分享，11:租房合同，12:房贷合同
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

    /**
     * 搜索剪切板
     *
     * @param content
     * @return
     */
    public static Flowable<BaseResponse<ClipBoardBean>> searchClipBoard(String content) {
        return getService().searchClipBoard(content).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<ClipBoardBean>> searchClipBoardOnLabel(String content) {
        return getService().searchClipBoardOnLabel(content).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取优惠券列表
     *
     * @return
     */
    public static Flowable<BaseResponse<List<CouponInfo>>> getCouponList(int scene) {
        return getService().getCouponList(scene).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发送短信或者验证码
     *
     * @param purpose 1:短信注册码，2:短信重置密码 + 微信注册时短信发送，3:修改手机号，4:绑定邮箱，5:重置邮箱,
     *                6:邮件重置密码，10：合同短信确认，11：短信注销，12：短信登录,13:申请仲裁 ,
     * @param to      手机号或者邮箱
     * @param mobile  邮箱重置需要的手机号，可选字段
     * @return
     */
    public static Flowable<BaseResponse<String>> sendMessage(int purpose, String to, String mobile) {
        SendMessageReqBean reqBean = new SendMessageReqBean();
        reqBean.setPurpose(purpose);
        reqBean.setTo(to);
        reqBean.setMobile(mobile);
        return getService().sendMessage(reqBean).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户行为统计
     *
     * @param behaviorCode
     * @param userInput
     */
    public static void userBehaviorStatistic(String behaviorCode, String userInput) {
        UserBehaviorReqBean reqBean = new UserBehaviorReqBean();
        reqBean.behaviorCode = behaviorCode;
        reqBean.userInput = userInput;
        getService().userBehaviorStatistic(reqBean).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<Object>>() {
                    @Override
                    public void accept(BaseResponse<Object> objectBaseResponse) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public static Flowable<BaseResponse<CurrRsaKeyBean>> getCurrentRsaKey() {
        return getService().getCurrentRsaKey()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}