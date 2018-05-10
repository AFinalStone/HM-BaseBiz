package com.hm.iou.base.webview.event;


public class WebViewRightButtonEvent {

    private String tag;
    private String message;
    private String rightButtonCallBackName;//右侧按钮被点击回调方法名

    public WebViewRightButtonEvent(String tag, String message, String rightButtonCallBackName) {
        this.tag = tag;
        this.message = message;
        this.rightButtonCallBackName = rightButtonCallBackName;
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

    public String getRightButtonCallBackName() {
        return rightButtonCallBackName;
    }

    public void setRightButtonCallBackName(String rightButtonCallBackName) {
        this.rightButtonCallBackName = rightButtonCallBackName;
    }
}