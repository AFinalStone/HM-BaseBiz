package com.hm.iou.base.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * Created by hjy on 2018/8/8.
 *
 * 埋点统计工具
 */

public class TraceUtil {

    public static void onEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    public static void onEvent(Context context, String eventId, Map<String, String> map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

}