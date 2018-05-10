package com.hm.iou.base.webview.event;

public class WebViewTitleBgColorEvent {

    private String tag;
    private String colorRGB;

    public WebViewTitleBgColorEvent(String tag, String colorRGB) {
        this.tag = tag;
        this.colorRGB = colorRGB;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getColorRGB() {
        return colorRGB;
    }

    public void setColorRGB(String colorRGB) {
        this.colorRGB = colorRGB;
    }
}