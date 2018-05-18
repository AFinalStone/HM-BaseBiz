package com.hm.iou.base.event;


/**
 * 打开微信获取到的code
 *
 * @author syl
 * @time 2018/5/17 下午2:40
 */
public class OpenWxResultEvent {
    private String code;
    private String key;

    public OpenWxResultEvent(String key, String code) {
        this.key = key;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }
}