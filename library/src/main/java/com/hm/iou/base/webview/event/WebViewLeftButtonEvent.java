package com.hm.iou.base.webview.event;

/**
 * Created by hjy on 2019/9/2
 *
 */
public class WebViewLeftButtonEvent {

    private String tag;
    private String message;
    private String callback;//右侧按钮被点击回调方法名
    private String params;

    public WebViewLeftButtonEvent(String tag, String message, String callback, String params) {
        this.tag = tag;
        this.message = message;
        this.callback = callback;
        this.params = params;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}