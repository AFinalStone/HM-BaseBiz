package com.hm.iou.base.webview.event;

/**
 * Created by hjy on 2019/9/2
 */
public class ShowBackIconEvent {

    public String pageTag;
    public boolean showBackIcon;

    public ShowBackIconEvent(String pageTag, boolean showBackIcon) {
        this.pageTag = pageTag;
        this.showBackIcon = showBackIcon;
    }

}