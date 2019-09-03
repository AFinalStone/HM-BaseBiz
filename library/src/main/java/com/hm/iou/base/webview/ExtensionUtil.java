package com.hm.iou.base.webview;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by hjy on 2018/8/30.
 *
 * 扩展名工具类
 */
public class ExtensionUtil {

    private static Set<String> CACHE_EXTENSIONS = new HashSet();

    static {
        CACHE_EXTENSIONS.add("js");
        CACHE_EXTENSIONS.add("css");
        CACHE_EXTENSIONS.add("png");
        CACHE_EXTENSIONS.add("jpg");
        CACHE_EXTENSIONS.add("jpeg");
        CACHE_EXTENSIONS.add("gif");
        CACHE_EXTENSIONS.add("webp");
        CACHE_EXTENSIONS.add("bmp");
        CACHE_EXTENSIONS.add("ico");
        CACHE_EXTENSIONS.add("ttf");
        CACHE_EXTENSIONS.add("woff");
        CACHE_EXTENSIONS.add("woff2");
        CACHE_EXTENSIONS.add("otf");
        CACHE_EXTENSIONS.add("eot");
        CACHE_EXTENSIONS.add("svg");
    }

    /**
     * 从 url 里获取文件的后缀，如果识别不出就返回空
     *
     * @param url 地址
     * @return 扩展名或者为空
     */
    public static String getFileExtensionFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            if (!filename.isEmpty() &&
                    Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }
        return "";
    }

    /**
     * 根据文件扩展名来判断是否需要进行缓存
     *
     * @param extension
     * @return
     */
    public static boolean canCache(String extension) {
        return CACHE_EXTENSIONS.contains(extension);
    }

}
