package com.hm.iou.base.webview.event;

/**
 * Created by hjy on 2018/8/23.
 */

public class JsNotifyEvent {

    private String eventName;
    private String params;
    private String pageTag;

    public JsNotifyEvent(String eventName, String params, String pageTag) {
        this.eventName = eventName;
        this.params = params;
        this.pageTag = pageTag;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getPageTag() {
        return pageTag;
    }

    public void setPageTag(String pageTag) {
        this.pageTag = pageTag;
    }
}
