package com.hm.iou.base.comm;

public class SendMessageReqBean {

    /**
     * 1:短信注册码，2:短信重置密码 + 微信注册时短信发送，3:修改手机号，4:绑定邮箱，5:重置邮箱,6:邮件重置密码，10：合同短信确认，11：短信注销，12：短信登录,13:申请仲裁
     */
    private int purpose;
    /**
     * to (string): 手机号码或者邮箱
     */
    private String to;

    /**
     * 发送邮箱验证码的时候，需要用到这个参数
     */
    private String mobile;

    public int getPurpose() {
        return purpose;
    }

    public void setPurpose(int purpose) {
        this.purpose = purpose;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
