package com.hm.iou.base.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.hm.iou.base.ActivityManager;
import com.hm.iou.base.ImageGalleryActivity;
import com.hm.iou.base.constants.HMConstants;
import com.hm.iou.base.file.FileUtil;
import com.hm.iou.base.webview.event.JsNotifyEvent;
import com.hm.iou.base.webview.event.SelectCityEvent;
import com.hm.iou.base.webview.event.ShareResultEvent;
import com.hm.iou.base.webview.event.ShowBackIconEvent;
import com.hm.iou.base.webview.event.WebViewLeftButtonEvent;
import com.hm.iou.base.webview.event.WebViewNativeSelectPicEvent;
import com.hm.iou.base.webview.event.WebViewRightButtonEvent;
import com.hm.iou.base.webview.event.WebViewTitleTextEvent;
import com.hm.iou.logger.Logger;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.socialshare.bean.PlatFormBean;
import com.hm.iou.socialshare.business.UMShareUtil;
import com.hm.iou.socialshare.business.view.SharePlatformDialog;
import com.hm.iou.socialshare.dict.PlatformEnum;
import com.hm.iou.tools.SPUtil;
import com.hm.iou.tools.SystemUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.HMTopBarView;
import com.hm.iou.uikit.dialog.HMAlertDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hjy on 2018/8/1.
 */

public class WebViewJsObject {

    private Activity mActivity;
    private String mPageTag;            //用于表示该js object绑定的是哪个WebView
    private WebView mWebView;

    private Handler mHandler;
    private volatile Gson mGson;

    private String mPicCallbackName;    //拍照等返回回调H5的方法名
    private int mPicCropWidth;          //拍照要进行裁切的宽度
    private int mPicCropHeight;         //拍照要进行裁切的高度
    private String mSelectCityCallbackName; //选择城市之后的回调函数名

    private UMShareUtil mUMShareUtil;
    private List<SharePlatformDialog> mShareList = new ArrayList<>();

    private IWXAPI mIWXAPI;

    public WebViewJsObject(Activity activity) {
        mActivity = activity;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void onDestroy() {
        if (mUMShareUtil != null) {
            mUMShareUtil.onDestroy();
            mUMShareUtil = null;
        }
        for (SharePlatformDialog dialog : mShareList) {
            if (dialog != null) {
                dialog.onDestroy();
                dialog.dismiss();
            }
        }
        mShareList.clear();
        if (mIWXAPI != null) {
            mIWXAPI.detach();
            mIWXAPI = null;
        }
        mWebView = null;
    }

    public void setPageTag(String tag) {
        mPageTag = tag;
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public String getPicCallbackName() {
        return mPicCallbackName;
    }

    public int getPicCropWidth() {
        return mPicCropWidth;
    }

    public int getPicCropHeight() {
        return mPicCropHeight;
    }

    public String getSelectCityCallbackName() {
        return mSelectCityCallbackName;
    }

    @JavascriptInterface
    public boolean checkLogin() {
        return UserManager.getInstance(mActivity).isLogin();
    }

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
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson.toJson(headers);
    }

    @JavascriptInterface
    public String getUserInfo() {
        UserInfo userDataBean = UserManager.getInstance(mActivity).getUserInfo();
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson.toJson(userDataBean);
    }

    @JavascriptInterface
    public void closeWebView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mActivity != null) {
                    mActivity.finish();
                }
            }
        });
    }

    @JavascriptInterface
    public void finishView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mActivity != null) {
                    mActivity.finish();
                }
            }
        });
    }

    @JavascriptInterface
    public void callPhone(final String phone) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + phone);
                    intent.setData(data);
                    mActivity.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @JavascriptInterface
    public void setStorage(String key, String value) {
        SPUtil.put(mActivity, HMConstants.SP_WEBVIEW, key, value);
    }

    @JavascriptInterface
    public String getStorage(String key) {
        return SPUtil.getString(mActivity, HMConstants.SP_WEBVIEW, key);
    }

    @JavascriptInterface
    public void openUrlThroughBrowser(final String url) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                SystemUtil.openWebBrowser(mActivity, url);
            }
        });
    }

    @JavascriptInterface
    public void openUrlThroughWebView(final String url) {
        Logger.d("openUrlThroughWebView: " + url);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                        .withString("url", url)
                        .navigation(mActivity);
            }
        });
    }

    @JavascriptInterface
    public void navigateByRouter(final String routerUrl) {
        Logger.d("navigateByRouter:" + routerUrl);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Router.getInstance().buildWithUrl(routerUrl)
                        .navigation(mActivity);
            }
        });
    }

    @JavascriptInterface
    public void selectPicture(String selectType, int width, int height, String callback) {
        if (TextUtils.isEmpty(selectType) || TextUtils.isEmpty(callback)) {
            return;
        }
        mPicCallbackName = callback;
        mPicCropWidth = width;
        mPicCropHeight = height;
        EventBus.getDefault().post(new WebViewNativeSelectPicEvent(mPageTag, selectType));
    }

    @JavascriptInterface
    public void setNavigationBarRightMenu(String btnText, String functionName, String params) {
        EventBus.getDefault().post(new WebViewRightButtonEvent(mPageTag, btnText, functionName, params));
    }

    @JavascriptInterface
    public void savePicture(final String url) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                FileUtil.savePicture(mActivity, url);
            }
        });
    }

    private PlatFormBean getPlatform(String channel) {
        if ("save".equals(channel)) {
            return new PlatFormBean(PlatformEnum.SAVE);
        } else if ("weixin".equals(channel)) {
            return new PlatFormBean(PlatformEnum.WEIXIN);
        } else if ("wxcircle".equals(channel)) {
            return new PlatFormBean(PlatformEnum.WEIXIN_CIRCLE);
        } else if ("weibo".equals(channel)) {
            return new PlatFormBean(PlatformEnum.WEIBO);
        } else if ("qq".equals(channel)) {
            return new PlatFormBean(PlatformEnum.QQ);
        } else if ("sms".equals(channel)) {
            return new PlatFormBean(PlatformEnum.SMS);
        } else if ("email".equals(channel)) {
            return new PlatFormBean(PlatformEnum.EMAIL);
        }
        return null;
    }

    private String getChannelByUMMedia(SHARE_MEDIA media) {
        if (media == SHARE_MEDIA.WEIXIN) {
            return "weixin";
        } else if (media == SHARE_MEDIA.WEIXIN_CIRCLE) {
            return "wxcircle";
        } else if (media == SHARE_MEDIA.SINA) {
            return "weibo";
        } else if (media == SHARE_MEDIA.QQ) {
            return "qq";
        }
        return media.name();
    }

    UMShareListener mShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            EventBus.getDefault().post(new ShareResultEvent(mPageTag, getChannelByUMMedia(share_media), true));
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            EventBus.getDefault().post(new ShareResultEvent(mPageTag, getChannelByUMMedia(share_media), false));
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            EventBus.getDefault().post(new ShareResultEvent(mPageTag, getChannelByUMMedia(share_media), false));
        }
    };

    @JavascriptInterface
    public void shareImage(final String imageUrl, final String channels) {
        shareImageV2(imageUrl, channels, 0, null);
    }

    @JavascriptInterface
    public void shareImageV2(final String imageUrl, final String channels, final int type, final String shareTitle) {
        if (TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(channels)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String[] channelArr = channels.split(",");
                List<PlatFormBean> list = new ArrayList<>();
                for (String channel : channelArr) {
                    PlatFormBean platform = getPlatform(channel);
                    if (platform != null) {
                        list.add(platform);
                    }
                }
                if (list.isEmpty()) {
                    return;
                }

                if (list.size() == 1) {
                    if (mUMShareUtil == null) {
                        mUMShareUtil = new UMShareUtil(mActivity);
                    }
                    mUMShareUtil.setShareListener(mShareListener);
                    mUMShareUtil.sharePicture(list.get(0).getUMSharePlatform(), imageUrl);
                    return;
                }

                if (mActivity.isFinishing())
                    return;
                SharePlatformDialog dialog = new SharePlatformDialog.Builder(mActivity)
                        .setPicUrl(imageUrl)
                        .setPlatforms(list)
                        .setShareListener(mShareListener)
                        .setTitle(shareTitle)
                        .setShowImage(type == 1 ? true : false)
                        .show();
                mShareList.add(dialog);
            }
        });
    }

    @JavascriptInterface
    public void shareLink(final String title, final String desc, final String url, final String channels) {
        shareLinkV2(title, desc, url, channels, null);
    }

    @JavascriptInterface
    public void shareLinkV2(final String title, final String desc, final String url, final String channels, final String shareTitle) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(url) || TextUtils.isEmpty(channels)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String[] channelArr = channels.split(",");
                List<PlatFormBean> list = new ArrayList<>();
                for (String channel : channelArr) {
                    PlatFormBean platform = getPlatform(channel);
                    if (platform != null) {
                        list.add(platform);
                    }
                }
                if (list.isEmpty()) {
                    return;
                }
                if (list.size() == 1) {
                    if (mUMShareUtil == null) {
                        mUMShareUtil = new UMShareUtil(mActivity);
                    }
                    mUMShareUtil.setShareListener(mShareListener);
                    mUMShareUtil.shareWebH5Url(list.get(0).getUMSharePlatform(), title, desc, url, null);
                    return;
                }

                if (mActivity.isFinishing())
                    return;
                SharePlatformDialog dialog = new SharePlatformDialog.Builder(mActivity)
                        .setWebUrlTitle(title)
                        .setWebUrlDesc(desc)
                        .setWebUrl(url)
                        .setPlatforms(list)
                        .setShareListener(mShareListener)
                        .setTitle(shareTitle)
                        .show();
                mShareList.add(dialog);
            }
        });
    }

    @JavascriptInterface
    public void shareText(final String text, final String channels) {
        shareTextV2(text, channels, null);
    }

    @JavascriptInterface
    public void shareTextV2(final String text, final String channels, final String shareTitle) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(channels)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String[] channelArr = channels.split(",");
                List<PlatFormBean> list = new ArrayList<>();
                for (String channel : channelArr) {
                    PlatFormBean platform = getPlatform(channel);
                    if (platform != null) {
                        list.add(platform);
                    }
                }
                if (list.isEmpty()) {
                    return;
                }
                if (list.size() == 1) {
                    if (mUMShareUtil == null) {
                        mUMShareUtil = new UMShareUtil(mActivity);
                    }
                    mUMShareUtil.setShareListener(mShareListener);
                    mUMShareUtil.shareText(list.get(0).getUMSharePlatform(), text);
                    return;
                }

                if (mActivity.isFinishing())
                    return;
                SharePlatformDialog dialog = new SharePlatformDialog.Builder(mActivity)
                        .setText(text)
                        .setPlatforms(list)
                        .setShareListener(mShareListener)
                        .setTitle(shareTitle)
                        .show();
                mShareList.add(dialog);
            }
        });
    }

    @JavascriptInterface
    public void shareImageByBase64(final String imgBase64Str, final String channels) {
        shareImageByBase64V2(imgBase64Str, channels, null);
    }

    @JavascriptInterface
    public void shareImageByBase64V2(final String imgBase64Str, final String channels, final String shareTitle) {
        if (TextUtils.isEmpty(imgBase64Str) || TextUtils.isEmpty(channels)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String[] channelArr = channels.split(",");
                List<PlatFormBean> list = new ArrayList<>();
                for (String channel : channelArr) {
                    PlatFormBean platform = getPlatform(channel);
                    if (platform != null) {
                        list.add(platform);
                    }
                }
                if (list.isEmpty()) {
                    return;
                }

                byte[] bytes;
                try {
                    bytes = Base64.decode(imgBase64Str, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                Bitmap bmp;
                try {
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (list.size() == 1) {
                    if (mUMShareUtil == null) {
                        mUMShareUtil = new UMShareUtil(mActivity);
                    }
                    mUMShareUtil.setShareListener(mShareListener);
                    mUMShareUtil.sharePicture(list.get(0).getUMSharePlatform(), bmp);
                    return;
                }

                if (mActivity.isFinishing())
                    return;
                SharePlatformDialog dialog = new SharePlatformDialog.Builder(mActivity)
                        .setBitmap(bmp)
                        .setPlatforms(list)
                        .setShareListener(mShareListener)
                        .setTitle(shareTitle)
                        .show();
                mShareList.add(dialog);
            }
        });
    }

    @JavascriptInterface
    public void toUserLoginPage() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //TODO 一个类似iOS在游客模式下，弹出的登录对话框
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/login/selecttype")
                        .navigation(mActivity);
            }
        });
    }

    @JavascriptInterface
    public void userLogout() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                UserManager.getInstance(mActivity).logout();
                EventBus.getDefault().post(new LogoutEvent());
                HttpReqManager.getInstance().setUserId("");
                HttpReqManager.getInstance().setToken("");
                ActivityManager.getInstance().exitAllActivities();
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/login/selecttype")
                        .navigation(mActivity);
            }
        });
    }

    @JavascriptInterface
    public void setWebViewTitle(String title) {
        if (TextUtils.isEmpty(title))
            return;
        EventBus.getDefault().post(new WebViewTitleTextEvent(mPageTag, title));
    }

    @JavascriptInterface
    public void viewLargeImage(final String images, final int index) {
        if (TextUtils.isEmpty(images)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String[] urlArr = images.split(",");
                Intent intent = new Intent(mActivity, ImageGalleryActivity.class);
                intent.putExtra(ImageGalleryActivity.EXTRA_KEY_INDEX, index);
                intent.putExtra(ImageGalleryActivity.EXTRA_KEY_IMAGES, urlArr);
                mActivity.startActivity(intent);
            }
        });
    }

    @JavascriptInterface
    public void toast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showMessage(mActivity, msg);
            }
        });
    }

    @JavascriptInterface
    public void showAlertDialog(final String configJson) {
        if (TextUtils.isEmpty(configJson)) {
            return;
        }
        try {
            if (mGson == null) {
                mGson = new Gson();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    DialogConfig config = mGson.fromJson(configJson, DialogConfig.class);
                    if (config != null) {
                        if (config.getButtons() == null || config.getButtons().isEmpty()) {
                            return;
                        }
                        if (mActivity.isFinishing())
                            return;
                        HMAlertDialog.Builder builder = new HMAlertDialog.Builder(mActivity);
                        builder.setTitle(config.getTitle());
                        builder.setMessage(config.getMsg());
                        final List<DialogConfigButton> btnList = config.getButtons();
                        builder.setPositiveButton(btnList.get(0).getName());
                        if (btnList.size() > 1) {
                            builder.setNegativeButton(btnList.get(1).getName());
                        }
                        builder.setOnClickListener(new HMAlertDialog.OnClickListener() {
                            @Override
                            public void onPosClick() {
                                String callback = btnList.get(0).getCallback();
                                if (!TextUtils.isEmpty(callback)) {
                                    mWebView.evaluateJavascript(callback + "()", null);
                                }
                            }

                            @Override
                            public void onNegClick() {
                                String callback = btnList.get(1).getCallback();
                                if (!TextUtils.isEmpty(callback)) {
                                    mWebView.evaluateJavascript(callback + "()", null);
                                }
                            }
                        });

                        builder.create().show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void selectCity(String callback) {
        if (TextUtils.isEmpty(callback))
            return;
        mSelectCityCallbackName = callback;
        EventBus.getDefault().post(new SelectCityEvent(mPageTag));
    }

    @JavascriptInterface
    public void notifyEvent(String eventName, String params) {
        if (TextUtils.isEmpty(eventName))
            return;
        EventBus.getDefault().post(new JsNotifyEvent(eventName, params, mPageTag));
    }

    @JavascriptInterface
    public void notifyEvent(String params) {
        if (TextUtils.isEmpty(params)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(params);
            String eventName = obj.getString("eventName");
            String result = obj.getString("result");
            EventBus.getDefault().post(new JsNotifyEvent(eventName, result, mPageTag));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public int getStatusBarHeight() {
        try {
            int height = HMTopBarView.getStatusBarHeight(mActivity);
            int webHeight = (int) (height / mActivity.getResources().getDisplayMetrics().density);
            if (webHeight > 0) {
                return webHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 24;
    }

    @JavascriptInterface
    public void toHomePage(final int index) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String type = "";
                if (index == 0) {
                    type = "home";
                } else if (index == 1) {
                    type = "news";
                } else if (index == 2) {
                    type = "recommend";
                } else if (index == 3) {
                    type = "personal";
                }
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/main/index")
                        .withString("tab_type", type)
                        .navigation(mActivity);
            }
        });
    }

    @JavascriptInterface
    public void saveImageWithBase64(final String imgBase64Str) {
        if (TextUtils.isEmpty(imgBase64Str)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                byte[] bytes;
                try {
                    bytes = Base64.decode(imgBase64Str, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                Bitmap bmp;
                try {
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showMessage(mActivity, "图片保存失败");
                    return;
                }
                FileUtil.savePicture(mActivity, bmp);
            }
        });
    }

    @JavascriptInterface
    public void showNavigationBarLeftIcon(boolean showBackIcon) {
        EventBus.getDefault().post(new ShowBackIconEvent(mPageTag, showBackIcon));
    }

    @JavascriptInterface
    public void setNavigationBarLeftMenu(String btnText, String functionName, String params) {
        EventBus.getDefault().post(new WebViewLeftButtonEvent(mPageTag, btnText, functionName, params));
    }

    /**
     * 唤起微信支付
     *
     * @param payReqStr
     */
    @JavascriptInterface
    public void toPayByWeixin(final String payReqStr) {
        if (TextUtils.isEmpty(payReqStr)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String partnerId = null;
                String prepayid = null;
                String packageValue = null;
                String nonceStr = null;
                String timeStamp = null;
                String sign = null;
                try {
                    JSONObject obj = new JSONObject(payReqStr);
                    partnerId = obj.getString("partnerid");
                    prepayid = obj.getString("prepayid");
                    packageValue = obj.getString("package");
                    nonceStr = obj.getString("noncestr");
                    timeStamp = obj.getString("timestamp");
                    sign = obj.getString("sign");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (partnerId == null || packageValue == null)
                    return;

                PlatformConfig.APPIDPlatform weixin = (PlatformConfig.APPIDPlatform) PlatformConfig.configs.get(SHARE_MEDIA.WEIXIN);
                String appId = weixin.appId;
                if (mIWXAPI == null) {
                    mIWXAPI = WXAPIFactory.createWXAPI(mActivity, appId);
                }
                if (!mIWXAPI.isWXAppInstalled()) {
                    ToastUtil.showMessage(mActivity, "当前手机未安装微信");
                    return;
                }

                PayReq request = new PayReq();
                request.appId = appId;
                request.partnerId = partnerId;
                request.prepayId = prepayid;
                request.packageValue = packageValue;
                request.nonceStr = nonceStr;
                request.timeStamp = timeStamp;
                request.sign = sign;
                mIWXAPI.sendReq(request);
            }
        });
    }

    private static class DialogConfig {

        private String title;
        private String msg;
        private List<DialogConfigButton> buttons;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<DialogConfigButton> getButtons() {
            return buttons;
        }

        public void setButtons(List<DialogConfigButton> buttons) {
            this.buttons = buttons;
        }
    }

    private static class DialogConfigButton {

        private String name;
        private String callback;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCallback() {
            return callback;
        }

        public void setCallback(String callback) {
            this.callback = callback;
        }
    }

}