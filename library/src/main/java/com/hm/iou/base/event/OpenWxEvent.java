package com.hm.iou.base.event;


/**
 * 打开绑
 *
 * @author syl
 * @time 2018/5/17 下午2:28
 */
public class OpenWxEvent {
    private Object object;

    public OpenWxEvent(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}