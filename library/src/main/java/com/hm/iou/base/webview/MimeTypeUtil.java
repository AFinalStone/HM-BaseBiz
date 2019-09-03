package com.hm.iou.base.webview;

import android.webkit.MimeTypeMap;
/**
 * Created by hjy on 2018/8/30.
 *
 * MIME工具类
 */
public class MimeTypeUtil {

    /**
     * 根据后缀获取对应的 mime 类型
     *
     * @param extension
     * @return
     */
    public static String getMimeTypeFromExtension(String extension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

}