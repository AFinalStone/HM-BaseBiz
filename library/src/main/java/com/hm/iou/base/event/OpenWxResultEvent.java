package com.hm.iou.base.event;


/**
 * 打开微信获取到的code
 *
 * @author syl
 * @time 2018/5/17 下午2:40
 */
public class OpenWxResultEvent {
    private String code;
    private Object object;

    public OpenWxResultEvent(String code, Object object) {
        this.code = code;
        this.object = object;
    }

    public String getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }
}