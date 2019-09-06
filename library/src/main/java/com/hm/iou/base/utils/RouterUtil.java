package com.hm.iou.base.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.router.Router;

/**
 * Created by hjy on 2019/1/15.
 */

public class RouterUtil {

    /**
     * 如果是网页地址，则直接通过WebView来打开，如果是原生页面路由地址，则通过路由打开
     *
     * @param context
     * @param linkUrl
     */
    public static void clickMenuLink(Context context, String linkUrl) {
        if (TextUtils.isEmpty(linkUrl)) {
            return;
        }
        if (linkUrl.startsWith("http")) {
            Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                    .withString("url", linkUrl)
                    .navigation(context);
        } else if (linkUrl.startsWith("hmiou")) {
            Router.getInstance().buildWithUrl(linkUrl).navigation(context);
        }
    }

    /**
     * 进入资讯反馈页面
     *
     * @param context
     * @param sceneCode 场景code
     * @param labelCode 标签code
     */
    public static void toSubmitFeedback(Context context, String sceneCode, String labelCode) {
        Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                .withString("url", BaseBizAppLike.getInstance().getH5Server() + "/apph5/iou-feedback/#/feedBackInput?sceneCode=" + sceneCode + "&labelCode=" + labelCode)
                .navigation(context);
    }
}
