package com.hm.iou.base.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.R;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.photo.CompressPictureUtil;
import com.hm.iou.base.photo.ImageCropper;
import com.hm.iou.base.photo.PhotoUtil;
import com.hm.iou.base.webview.event.WebViewNativeSelectPicEvent;
import com.hm.iou.base.webview.event.WebViewRightButtonEvent;
import com.hm.iou.base.webview.event.WebViewTitleBgColorEvent;
import com.hm.iou.base.webview.event.WebViewTitleTextEvent;
import com.hm.iou.logger.Logger;
import com.hm.iou.tools.DensityUtil;
import com.hm.iou.tools.FileUtil;
import com.hm.iou.tools.NetStateUtil;
import com.hm.iou.tools.StringUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.HMTopBarView;
import com.hm.iou.uikit.dialog.IOSAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;


/**
 * Created by hasee on 2018/5/2.
 */

public class BaseWebviewActivity<T extends MvpActivityPresenter> extends BaseActivity<T> {

    public static final String EXTRA_KEY_WEB_TITLE = "title";
    public static final String EXTRA_KEY_WEB_URL = "url";
    public static final String EXTRA_KEY_SHOW_TITLE = "showtitle";      //是否显示中间标题
    public static final String EXTRA_KEY_SHOW_DIVIDER = "showdivider";  //是否显示分割线

    private static final int REQ_CODE_FILE_CHOOSER = 100;
    private static final int REQ_CDOE_CAMERA = 101;
    private static final int REQ_CODE_ALBUM = 102;

    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
    );

    protected HMTopBarView mTopBar;
    protected FrameLayout mWebViewContainer;
    protected ProgressBar mPbWebview;
    protected HMLoadingView mLoadingView;
    protected WebView mWebView;

    protected String mTitle;
    protected String mUrl;
    protected String mShowTitle = "true";    //是否显示导航栏中间的标题，默认显示
    protected String mShowDivider = "true";  //是否显示导航栏底部的分割线，默认显示

    protected boolean mInited = false;
    protected boolean mNetworkError = false;// 是否网络错误的标记

    private ValueCallback mUploadMessage;
    private View mCustomView;
    private FrameLayout mFullScreenContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private String mPageTag;
    private WebViewJsObject mJsObj;
    private ImageCropper mImageCropper;

    @Override
    protected int getLayoutId() {
        return R.layout.base_activity_comm_webview;
    }

    @Override
    protected T initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        mTopBar = findViewById(R.id.topbar);
        mWebViewContainer = findViewById(R.id.fl_webview_container);
        mPbWebview = findViewById(R.id.pb_webview);
        mLoadingView = findViewById(R.id.lv_webview);

        Intent data = getIntent();
        mTitle = data.getStringExtra(EXTRA_KEY_WEB_TITLE);
        mUrl = data.getStringExtra(EXTRA_KEY_WEB_URL);
        String showTitle = data.getStringExtra(EXTRA_KEY_SHOW_TITLE);
        if (!TextUtils.isEmpty(showTitle))
            mShowTitle = showTitle;
        String showDivider = data.getStringExtra(EXTRA_KEY_SHOW_DIVIDER);
        if (!TextUtils.isEmpty(showDivider))
            mShowDivider = showDivider;

        if (savedInstanceState != null && mUrl == null) {
            mTitle = savedInstanceState.getString(EXTRA_KEY_WEB_TITLE);
            mUrl = savedInstanceState.getString(EXTRA_KEY_WEB_URL);
            mShowTitle = savedInstanceState.getString(EXTRA_KEY_SHOW_TITLE, "true");
            mShowDivider = savedInstanceState.getString(EXTRA_KEY_SHOW_DIVIDER, "true");
        }

        //如果网络连接，则直接加载
        initViews();
        EventBus.getDefault().register(this);

        loadUrl();
    }

    private void initViews() {
        if ("true".equals(mShowTitle)) {
            if (!TextUtils.isEmpty(mTitle)) {
                mTopBar.setTitle(mTitle);
            } else {
                mTopBar.setTitle("");
            }
        }
        mTopBar.setOnBackClickListener(new HMTopBarView.OnTopBarBackClickListener() {
            @Override
            public void onClickBack() {
                onBackPressed();
            }
        });
        //如果不显示中间标题，则下面分割线不显示
        if (!"true".equals(mShowTitle)) {
            mTopBar.getDividerView().setVisibility(View.INVISIBLE);
        }
        if (!"true".equals(mShowDivider)) {
            mTopBar.getDividerView().setVisibility(View.INVISIBLE);
        }
        mTopBar.setBackgroundColor(Color.WHITE);

        initWebview();
    }

    private void loadUrl() {
        if (NetStateUtil.isNetworkConnected(this)) {
            if (!TextUtils.isEmpty(mUrl)) {
                if (mUrl.startsWith("http://") || mUrl.startsWith("https://")) {
                    mWebView.loadUrl(mUrl);
                } else if (mUrl.startsWith("file://")) {
                    mWebView.loadUrl(mUrl);
                } else {
                    mWebView.loadUrl("http://" + mUrl);
                }
            }
            mWebView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);
        } else {
            showLoadingFail();
        }
    }

    private void showLoadingFail() {
        mWebView.setVisibility(View.GONE);
        mLoadingView.showDataFail(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_WEB_TITLE, mTitle);
        outState.putString(EXTRA_KEY_WEB_URL, mUrl);
        outState.putString(EXTRA_KEY_SHOW_TITLE, mShowTitle);
        outState.putString(EXTRA_KEY_SHOW_DIVIDER, mShowDivider);
    }

    @Override
    public void onBackPressed() {
        if (mCustomView != null) {
            hideCustomView();
        } else if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mWebViewContainer != null && mWebView != null)
            mWebViewContainer.removeView(mWebView);
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FILE_CHOOSER) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessage.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessage.onReceiveValue(result);
            }
            mUploadMessage = null;
        } else if (requestCode == REQ_CDOE_CAMERA) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.getCameraPhotoPath();
                if (TextUtils.isEmpty(path)) {
                    ToastUtil.showMessage(this, "获取图片失败");
                    return;
                }
                onSelectPhoto(path);
            }
        } else if (requestCode == REQ_CODE_ALBUM) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.getPath(this, data.getData());
                if (TextUtils.isEmpty(path)) {
                    ToastUtil.showMessage(this, "获取图片失败");
                    return;
                }
                onSelectPhoto(path);
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebview() {
        mWebView = new WebView(this);
        mWebViewContainer.removeAllViews();
        mWebViewContainer.addView(mWebView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.getSettings().setSavePassword(false);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mJsObj = getJsObject();
        mPageTag = System.currentTimeMillis() + "";
        mJsObj.setPageTag(mPageTag);
        mWebView.addJavascriptInterface(mJsObj, "HMApplication");
        mWebView.getSettings().setGeolocationEnabled(true);

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                launchBrowser(BaseWebviewActivity.this, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mPbWebview != null && newProgress > 0) {
                    if (newProgress == 100) {
                        mPbWebview.setVisibility(View.GONE);
                    } else {
                        if (View.VISIBLE != mPbWebview.getVisibility()) {
                            mPbWebview.setVisibility(View.VISIBLE);
                        }
                        mPbWebview.setProgress(newProgress);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if ("true".equals(mShowTitle) && TextUtils.isEmpty(mTitle)) {
                    mTopBar.setTitle(title);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, true);
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Logger.d(String.format("%s -- From line %s of %s", cm.message(), cm.lineNumber(), cm.sourceId()));
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new IOSAlertDialog.Builder(BaseWebviewActivity.this)
                        .setMessage(message)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                result.confirm();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new IOSAlertDialog.Builder(BaseWebviewActivity.this)
                        .setMessage(message)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                result.confirm();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                      final JsPromptResult result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BaseWebviewActivity.this);
                builder.setMessage(message);
                final EditText editText = new EditText(BaseWebviewActivity.this);
                editText.setText(defaultValue);
                builder.setView(editText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = editText.getText().toString().trim();
                        result.confirm(newValue);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                builder.show();
                return true;
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), REQ_CODE_FILE_CHOOSER);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
                i.setType(type);
                startActivityForResult(Intent.createChooser(i, "File Chooser"), REQ_CODE_FILE_CHOOSER);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
                i.setType(type);
                startActivityForResult(Intent.createChooser(i, "File Chooser"), REQ_CODE_FILE_CHOOSER);
            }

            //Android 5.0+
            @Override
            @SuppressLint("NewApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                        && fileChooserParams.getAcceptTypes().length > 0) {
                    i.setType(fileChooserParams.getAcceptTypes()[0]);
                } else {
                    i.setType("*/*");
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), REQ_CODE_FILE_CHOOSER);
                return true;
            }


            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(BaseWebviewActivity.this);
                frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);

                // if a view already exists then immediately terminate the new one
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                mFullScreenContainer = new FullscreenHolder(BaseWebviewActivity.this);
                mFullScreenContainer.addView(view, COVER_SCREEN_PARAMS);
                decor.addView(mFullScreenContainer, COVER_SCREEN_PARAMS);
                mCustomView = view;
                setStatusBarVisibility(false);
                mCustomViewCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                hideCustomView();
            }

        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                BaseWebviewActivity.this.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!mInited && !mNetworkError) {
                    mInited = true;
                }
                BaseWebviewActivity.this.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                if (!mInited) {

                } else {
                    handler.proceed();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Logger.d("webview load error : " + failingUrl);
                Logger.d("error code = " + errorCode);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return;
                }
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                    showLoadingFail();
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    int errorCode = error.getErrorCode();
                    if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                        showLoadingFail();
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) {
                    return true;
                }
                if (Uri.parse(url).getScheme().startsWith("file")) {
                    // 如果是本地加载的话，直接用当期浏览器加载
                    return false;
                }
                if (Uri.parse(url).getScheme().startsWith("http")) {
                    return false;
                }
                if ("about:blank".equals(url)) {
                    return false; // 不需要处理空白页
                }
                launchBrowser(BaseWebviewActivity.this, url);
                return true;
            }

        });
    }

    // 打开系统浏览器
    private static void launchBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse(url));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideCustomView() {
        if (mCustomView == null) {
            return;
        }

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(mFullScreenContainer);
        mFullScreenContainer = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        mWebView.setVisibility(View.VISIBLE);
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 监听网页加载进度，子类可根据需要监听进度
     *
     * @param view    WebView
     * @param url     地址
     * @param favicon icon
     */
    protected void onPageStarted(WebView view, String url, Bitmap favicon) {

    }

    /**
     * 监听网页加载结束，子类可根据需要监听进度
     *
     * @param view WebView
     * @param url  地址
     */
    protected void onPageFinished(WebView view, String url) {

    }

    /**
     * 获取js注入对象， 如果WebView需要处理特殊业务，子类Override该方法即可
     *
     * @return 默认返回基础的js注入对象
     */
    protected WebViewJsObject getJsObject() {
        return new WebViewJsObject(this);
    }

    /**
     * 拍照或者选择照片成功
     *
     * @param file
     */
    protected void onPictureSelectSuccess(File file) {
        Logger.d("图片选择成功：" + file.getAbsolutePath());

    }

    /**
     * 拍照或者选择图片成功
     *
     * @param path
     */
    private void onSelectPhoto(String path) {
        int width = mJsObj.getCameraCallCutWidth();
        int height = mJsObj.getCameraCallCutHeight();
        //不进行裁剪操作
        if (width == 0 || height == 0) {
            compressPicture(path);
            return;
        }
        initImageCropper();
        mImageCropper.crop(path, width, height, false, "webviewcropper");
    }

    private void initImageCropper() {
        if (mImageCropper == null) {
            int displayDeviceHeight = getResources().getDisplayMetrics().heightPixels - DensityUtil.dip2px(this, 53);
            mImageCropper = ImageCropper.Helper.with(this).setTranslucentStatusHeight(displayDeviceHeight).create();
            mImageCropper.setCallback(new ImageCropper.Callback() {
                @Override
                public void onPictureCropOut(Bitmap bitmap, String tag) {
                    File fileCrop = new File(FileUtil.getDiskCacheDir(BaseWebviewActivity.this) + "/photo_crop.png");
                    boolean flag = CompressPictureUtil.saveBitmapToTargetFile(fileCrop, bitmap);
                    if (flag) {
                        compressPicture(fileCrop.getAbsolutePath());
                    } else {
                        Logger.e("Webview crop picture fail.");
                    }
                }
            });
        }
    }

    private void compressPicture(String path) {
        CompressPictureUtil.compressPic(this, path, new CompressPictureUtil.OnCompressListener() {
            @Override
            public void onCompressPicSuccess(File file) {
                onPictureSelectSuccess(file);
            }
        });
    }

    //设置标题文字
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdateTitle(WebViewTitleTextEvent event) {
        if (StringUtil.getUnnullString(event.getTag()).equals(mPageTag)) {
            mTopBar.setTitle(event.getTitle());
        }
    }

    //打开相册或者相机
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSelectPhoto(WebViewNativeSelectPicEvent event) {
        if (!StringUtil.getUnnullString(event.getTag()).equals(mPageTag)) {
            return;
        }
        String selectType = event.getSelectType();
        if ("camera".equals(selectType)) {
            PhotoUtil.openCamera(this, REQ_CDOE_CAMERA);
            return;
        }

        if ("album".equals(selectType)) {
            PhotoUtil.openAlbum(this, REQ_CODE_ALBUM);
            return;
        }

        if ("cameraAndAlbum".equals(selectType)) {
            PhotoUtil.showSelectDialog(BaseWebviewActivity.this, REQ_CDOE_CAMERA, REQ_CODE_ALBUM);
        }
    }

    //设置右侧按钮的点击事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSetRightBtn(WebViewRightButtonEvent event) {
        if (StringUtil.getUnnullString(event.getTag()).equals(mPageTag)) {
            mTopBar.setRightText(event.getMessage());
            mTopBar.setOnMenuClickListener(new HMTopBarView.OnTopBarMenuClickListener() {
                @Override
                public void onClickTextMenu() {
                    String function = "javascript:" + event.getRightButtonCallBackName() + "()";
                    mWebView.loadUrl(function);
                }

                @Override
                public void onClickImageMenu() {

                }
            });
        }
    }

    //设置标题背景颜色
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdateTitleBg(WebViewTitleBgColorEvent event) {
        if (StringUtil.getUnnullString(event.getTag()).equals(mPageTag)) {
            try {
                int color = Color.parseColor(event.getColorRGB());
                mTopBar.setBackgroundColor(color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

}
