package com.hm.iou.base.utils;

import android.content.Context;

import com.hm.iou.base.comm.AuthWayResBean;
import com.hm.iou.base.comm.CommApi;
import com.hm.iou.base.constants.HMConstants;
import com.hm.iou.tools.SPUtil;

import io.reactivex.functions.Consumer;

/**
 * 实名认证认证方式
 */
public class RealNameChannelUtil {

    /**
     * 在线更新实名认证方式
     *
     * @param context
     */
    public static void updateRealNameChannel(Context context) {
        final Context app = context.getApplicationContext();
        CommApi.getAuthWay().map(RxUtil.<AuthWayResBean>handleResponse())
                .subscribe(new Consumer<AuthWayResBean>() {
                    @Override
                    public void accept(AuthWayResBean data) throws Exception {
                        if (data != null) {
                            SPUtil.put(app, HMConstants.SP_SYS_CONFIG, HMConstants.SP_KEY_REALNAME_CHANNEL, data.getAuthChannel());
                            SPUtil.put(app, HMConstants.SP_SYS_CONFIG, HMConstants.SP_KEY_REALNAME_OCR_WAY, data.getTakePhotosWay());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 判断实名认证渠道，默认是采用商汤科技
     *
     * @param context
     * @return true-商汤科技，false-今始科技
     */
    public static boolean isSenseTimeChannel(Context context) {
        String channel = SPUtil.getString(context, HMConstants.SP_SYS_CONFIG, HMConstants.SP_KEY_REALNAME_CHANNEL);
        if ("LINKFACE".equals(channel)) {
            return false;
        }
        return true;
    }

    /**
     * 身份证OCR时是否自动识别
     *
     * @param context
     * @return
     */
    public static boolean isOcrAuto(Context context) {
        int way = SPUtil.getInt(context, HMConstants.SP_SYS_CONFIG, HMConstants.SP_KEY_REALNAME_OCR_WAY);
        return way == 1;
    }

}