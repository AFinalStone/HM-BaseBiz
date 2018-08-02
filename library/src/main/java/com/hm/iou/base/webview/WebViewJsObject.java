package com.hm.iou.base.webview;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.hm.iou.base.ActivityManager;
import com.hm.iou.base.constants.HMConstants;
import com.hm.iou.base.file.FileUtil;
import com.hm.iou.base.webview.event.WebViewNativeSelectPicEvent;
import com.hm.iou.base.webview.event.WebViewRightButtonEvent;
import com.hm.iou.base.webview.event.WebViewTitleTextEvent;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.socialshare.bean.PlatFormBean;
import com.hm.iou.socialshare.business.view.SharePlatformDialog;
import com.hm.iou.socialshare.dict.PlatformEnum;
import com.hm.iou.tools.SPUtil;
import com.hm.iou.tools.SystemUtil;

import org.greenrobot.eventbus.EventBus;

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

    private Handler mHandler;
    private volatile Gson mGson;

    private String mPicCallbackName;    //拍照等返回回调H5的方法名
    private int mPicCropWidth;          //拍照要进行裁切的宽度
    private int mPicCropHeight;         //拍照要进行裁切的高度


    public WebViewJsObject(Activity activity) {
        mActivity = activity;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void setPageTag(String tag) {
        mPageTag = tag;
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

    @JavascriptInterface
    public void shareImage(final String imageUrl, final String channels) {
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
                SharePlatformDialog dialog = new SharePlatformDialog.Builder(mActivity)
                        .setPicUrl(imageUrl)
                        .setPlatforms(list)
                        .show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (dialog != null && dialog instanceof SharePlatformDialog) {
                            ((SharePlatformDialog) dialog).onDestroy();
                        }
                    }
                });
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
        }
        return null;
    }

    @JavascriptInterface
    public void shareLink(final String title, final String desc, final String url, final String channels) {
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
                SharePlatformDialog dialog = new SharePlatformDialog.Builder(mActivity)
                        .setWebUrlTitle(title)
                        .setWebUrlDesc(desc)
                        .setWebUrl(url)
                        .setPlatforms(list)
                        .show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (dialog != null && dialog instanceof SharePlatformDialog) {
                            ((SharePlatformDialog) dialog).onDestroy();
                        }
                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void shareText(final String text, final String channels) {
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
                SharePlatformDialog dialog = new SharePlatformDialog.Builder(mActivity)
                        .setText(text)
                        .setPlatforms(list)
                        .show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (dialog != null && dialog instanceof SharePlatformDialog) {
                            ((SharePlatformDialog) dialog).onDestroy();
                        }
                    }
                });
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

}