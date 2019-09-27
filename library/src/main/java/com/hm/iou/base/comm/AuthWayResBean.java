package com.hm.iou.base.comm;

public class AuthWayResBean {

    //实名渠道：SENSETIME = 商汤，LINKFACE = 金石（老）
    String authChannel;
    //拍照方式：0=手动 ，1=自动
    int takePhotosWay;

    public String getAuthChannel() {
        return authChannel;
    }

    public void setAuthChannel(String authChannel) {
        this.authChannel = authChannel;
    }

    public int getTakePhotosWay() {
        return takePhotosWay;
    }

    public void setTakePhotosWay(int takePhotosWay) {
        this.takePhotosWay = takePhotosWay;
    }
}
