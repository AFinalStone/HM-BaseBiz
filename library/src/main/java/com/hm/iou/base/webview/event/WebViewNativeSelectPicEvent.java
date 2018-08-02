package com.hm.iou.base.webview.event;

/**
 * 调用拍照、相册选取照片通知事件
 */
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