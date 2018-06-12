package com.hm.iou.base.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.hm.iou.base.webview.event.WebViewNativeSelectPicEvent;
import com.hm.iou.base.webview.event.WebViewRightButtonEvent;
import com.hm.iou.base.webview.event.WebViewTitleBgColorEvent;
import com.hm.iou.base.webview.event.WebViewTitleTextEvent;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.socialshare.ShareDataTypeEnum;
import com.hm.iou.tools.StringUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AFinalStone on 2018/4/25.
 */

public class WebViewJsObject {

    private static final String TAG = "WebViewJsObject";

    private Activity mActivity;
    private String mTag;

    private Handler mHandler;

    private String mCameraCallBackName;     //拍照等返回回调H5的方法名
    private int mCameraCallCutWidth;        //拍照要进行裁切的宽度
    private int mCameraCallCutHeight;       //拍照要进行裁切的高度
    private String mShareCallBackName;      //分享回调方法名
    private String mQrScanCallBackName;     // 扫描回调方法名

    public WebViewJsObject(Activity activity) {
        mActivity = activity;
        mHandler = new Handler();
    }

    public void setPageTag(String tag) {
        mTag = tag;
    }

    public String getCameraCallBackName() {
        return mCameraCallBackName;
    }

    public int getCameraCallCutWidth() {
        return mCameraCallCutWidth;
    }

    public int getCameraCallCutHeight() {
        return mCameraCallCutHeight;
    }

    public String getShareCallBackName() {
        return mShareCallBackName;
    }

    public String getQrScanCallBackName() {
        return mQrScanCallBackName;
    }


    /**
     * 设置WebView的标题文字
     *
     * @param name 标题名字
     */
    @JavascriptInterface
    public void setWebViewTitleName(String name) {
        if (TextUtils.isEmpty(name))
            return;
        EventBus.getDefault().post(new WebViewTitleTextEvent(mTag, name));
    }

    /**
     * 设置WebView的标题的背景颜色
     *
     * @param colorRGB 颜色rgb
     */
    @JavascriptInterface
    public void setWebViewTitleBgColor(String colorRGB) {
        EventBus.getDefault().post(new WebViewTitleBgColorEvent(mTag, colorRGB));
    }

    /**
     * 设置WebView的右上角按钮
     *
     * @param btnText      按钮显示的文字
     * @param functionName 用户点击按钮回调H5的方法名称
     */
    @JavascriptInterface
    public void setWebViewRightButton(String btnText, String functionName) {
        EventBus.getDefault().post(new WebViewRightButtonEvent(mTag, btnText, functionName));
    }

    /**
     * 获取用户信息
     *
     * @return {phone:"";token:"";userId:""}
     */
    @JavascriptInterface
    public String getUserInfo() {
        UserInfo userDataBean = UserManager.getInstance(mActivity).getUserInfo();
        return new Gson().toJson(userDataBean);
    }

    /**
     * 获取请求头信息
     *
     * @return 返回请求头Map的json字符串
     */
    @JavascriptInterface
    public String getHeaders() {
        HttpRequestConfig config = HttpReqManager.getInstance().getRequestConfig();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("deviceId", config.getDeviceId());
        headers.put("rptTime", System.currentTimeMillis() + "");
        headers.put("operKind", "CUSTOMER");
        headers.put("osType", "Android");
        headers.put("osVer", Build.VERSION.RELEASE);
        headers.put("appChannel", config.getAppChannel());
        headers.put("deviceType", Build.BRAND + " " + Build.MODEL);
        headers.put("appVer", config.getAppVersion());
        headers.put("rptGpsX", config.getGpsX());
        headers.put("rptGpsY", config.getGpsY());
        return new Gson().toJson(headers);
    }


    /**
     * 判断用户是否登录
     *
     * @return （true为已经登录，false为未登录）
     */
    @JavascriptInterface
    public Boolean checkLogin() {
        return UserManager.getInstance(mActivity).isLogin();
    }

    /**
     * 打开用户登录的页面
     */
    @JavascriptInterface
    public void userLoginWindow() {
        //TODO 采用路由来进行跳转
    }

    /**
     * 关闭webview
     */
    @JavascriptInterface
    public void finishView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.finish();
            }
        });
    }

    /**
     * 分享图片
     *
     * @param imageUrl 图片链接
     * @return
     */
    @JavascriptInterface
    public void shareImageView(final String imageUrl) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(mActivity, Class.forName("com.hm.iou.hmreceipt.ui.activity.ShareDataActivity"));
                    intent.putExtra("intent_share_iou_type", ShareDataTypeEnum.shareFunIOU);
                    intent.putExtra("intent_image_url", imageUrl);
                    mActivity.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 拨打电话
     *
     * @param phoneNume
     */
    @JavascriptInterface
    public void nativeCallPhone(final String phoneNume) {
        Logger.d(TAG, "nativeCallPhone-->phoneNume " + phoneNume);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phoneNume);
                intent.setData(data);
                mActivity.startActivity(intent);
            }
        });
    }

    /**
     * 拍照等
     *
     * @param callbackName
     */
    @JavascriptInterface
    public void nativeSelectPic(String selectType, String width, String height, String callbackName) {
        Logger.d("selectType " + selectType + " callback: " + callbackName);
        Logger.d("width " + width + " height: " + height);
        if (StringUtil.isEmpty(selectType))
            return;

        if (StringUtil.isEmpty(callbackName))
            return;

        mCameraCallBackName = callbackName;
        try {
            mCameraCallCutWidth = Integer.parseInt(width);
            mCameraCallCutHeight = Integer.parseInt(height);
        } catch (Exception e) {
            mCameraCallCutWidth = 0;
            mCameraCallCutHeight = 0;
        }
        EventBus.getDefault().post(new WebViewNativeSelectPicEvent(mTag, selectType));
    }


}
