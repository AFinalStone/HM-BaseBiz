package com.hm.iou.base.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.hm.iou.base.R;
import com.hm.iou.router.Router;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.tools.SystemUtil;

/**
 * @author syl
 * @time 2019/3/19 5:01 PM
 */

public class ImageLoadUtil {

    /**
     * 获取图片的真正链接
     *
     * @param context
     * @param imageUrl 图片链接
     */
    public static String getImageRealUrl(Context context, String imageUrl) {
        if (imageUrl.startsWith("hmiou://")) {
            String placeString = String.format("android.resource://%s/", SystemUtil.getCurrentAppPackageName(context));
            imageUrl = imageUrl.replace("hmiou://", placeString);
        }
        return imageUrl;
    }
}
