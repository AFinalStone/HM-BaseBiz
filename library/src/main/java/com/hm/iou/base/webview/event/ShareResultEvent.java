package com.hm.iou.base.webview.event;

/**
 * Created by hjy on 2018/12/20.
 */

public class ShareResultEvent {

    private String pageTag;
    private String channel;
    private boolean succ;

    public ShareResultEvent(String pageTag, String channel, boolean succ) {
        this.pageTag = pageTag;
        this.channel = channel;
        this.succ = succ;
    }

    public String getPageTag() {
        return pageTag;
    }

    public void setPageTag(String pageTag) {
        this.pageTag = pageTag;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
