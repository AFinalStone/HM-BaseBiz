package com.hm.iou.base.webview.event;

/**
 * Created by hjy on 2018/8/17.
 */

public class SelectCityEvent {

    private String tag;

    public SelectCityEvent(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
