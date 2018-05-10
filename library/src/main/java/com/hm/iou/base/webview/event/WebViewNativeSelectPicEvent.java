package com.hm.iou.base.webview.event;


public class WebViewNativeSelectPicEvent {

    private String tag;
    private String selectType;

    public WebViewNativeSelectPicEvent(String tag, String selectType) {
        this.tag = tag;
        this.selectType = selectType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }
}