package com.hm.iou.base.comm;

/**
 * Created by hjy on 2018/12/24.
 */

public class ReportShareReqBean {

    private Integer scene;
    private Integer biz;
    private Integer channel;
    private Integer rule;
    private Integer serial;
    private String memo;

    public Integer getScene() {
        return scene;
    }

    public void setScene(Integer scene) {
        this.scene = scene;
    }

    public Integer getBiz() {
        return biz;
    }

    public void setBiz(Integer biz) {
        this.biz = biz;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getRule() {
        return rule;
    }

    public void setRule(Integer rule) {
        this.rule = rule;
    }

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
