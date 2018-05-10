package com.hm.iou.base.webview.event;

public class WebViewTitleTextEvent {

    private String tag;
    private String title;

    public WebViewTitleTextEvent(String tag, String title) {
        this.tag = tag;
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}