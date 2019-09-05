package com.hm.iou.base.webview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;

import com.hm.iou.base.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hjy on 2018/8/30.
 * <p>
 * 对 WebView 的资源请求做拦截
 */
public class WebViewInterceptor {

    private static WebViewInterceptor INSTANCE;

    public static WebViewInterceptor getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WebViewInterceptor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WebViewInterceptor(context);
                }
            }
        }
        return INSTANCE;
    }

    private Context mContext;
    private OkHttpClient mOkHttpClient;

    public WebViewInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    private void ensureInitHttpClient() {
        if (mOkHttpClient != null)
            return;
        File cacheDir = new File(FileUtil.getExternalCacheDirPath(mContext), "WebCache");
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(new Cache(cacheDir, 100 * 1024 * 1024))
        /*        .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        return response.newBuilder()
                                .removeHeader("pragma")
                                .removeHeader("Cache-Control")
                                .header("Cache-Control", "max-age=604800")  //缓存有效时间 1 周
                                .build();
                    }
                })*/
                .build();
    }

    public WebResourceResponse interceptRequest(String url) {
        return interceptRequest(url, null);
    }

    public WebResourceResponse interceptRequest(String url, Map<String, String> headers) {
        String extension = ExtensionUtil.getFileExtensionFromUrl(url);
//        Log.d("WebView", "url : " + url);
        if (!checkUrl(url, extension)) {
            return null;
        }
//        Log.d("WebView", "需要缓存的url " + url);

        if (extension.equals("js")) {
            if (url.contains("jquery") && url.contains("54jietiao.com")) {
//                Log.d("WebView", "替换本地 jquery 库");

                try {
                    return new WebResourceResponse("text/*", "UTF-8", mContext.getAssets().open("static/js/jquery.js"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (headers == null)
            headers = buildDefaultHeaders();
        Request.Builder reqBuilder = new Request.Builder()
                .url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        if (ExtensionUtil.isHtml(extension)) {
            //Cache-Control: html缓存时间设置为 半小时
            reqBuilder.cacheControl(new CacheControl.Builder().maxAge(1800, TimeUnit.SECONDS).build());
        } else {
            //Cache-Control: 设置一周
            reqBuilder.cacheControl(new CacheControl.Builder().maxAge(604800, TimeUnit.SECONDS).build());
        }

        Request request = reqBuilder.build();
        ensureInitHttpClient();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Response cacheResponse = response.cacheResponse();
            if (cacheResponse != null) {
//                Log.d("WebView", "使用cache...");
            } else {
//                Log.d("WebView", "get from server...");
            }
            String mime = MimeTypeUtil.getMimeTypeFromExtension(extension);

            WebResourceResponse webResourceResponse = new WebResourceResponse(mime, null, response.body().byteStream());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    String message = response.message();
                    if (TextUtils.isEmpty(message))
                        message = "OK";
                    webResourceResponse.setStatusCodeAndReasonPhrase(response.code(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                webResourceResponse.setResponseHeaders(multimapToSingle(response.headers().toMultimap()));
            }
            return webResourceResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 检查url是否符合要求能够缓存
     *
     * @param url
     * @param extension 文件后缀
     * @return
     */
    private boolean checkUrl(String url, String extension) {
        if (TextUtils.isEmpty(url))
            return false;
        if (!url.startsWith("http"))
            return false;
        if (TextUtils.isEmpty(extension))
            return false;
        if (!ExtensionUtil.canCache(extension)) {
            return false;
        }
        return true;
    }

    private Map<String, String> buildDefaultHeaders() {
        Map<String, String> map = new HashMap();
        map.put("User-Agent", "HMAndroidWebView");
        return map;
    }

    private Map<String, String> multimapToSingle(Map<String, List<String>> maps) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : maps.entrySet()) {
            List<String> values = entry.getValue();
            sb.delete(0, sb.length());
            if (values != null && values.size() > 0) {
                for (String v : values) {
                    sb.append(v);
                    sb.append(";");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            map.put(entry.getKey(), sb.toString());
        }
        return map;
    }

}