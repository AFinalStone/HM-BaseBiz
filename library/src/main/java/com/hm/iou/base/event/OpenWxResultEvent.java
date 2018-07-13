package com.hm.iou.base.event;


/**
 * 打开微信获取到的回调对象
 *
 * @author syl
 * @time 2018/5/17 下午2:40
 */
public class OpenWxResultEvent {
    private String code;        //微信登录获取到的微信code
    private String key;         //key，方便回调方法识别微信发起的来源地
    private boolean ifPaySuccess;//微信是否付款成功

    public String getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean getIfPaySuccess() {
        return ifPaySuccess;
    }

    public void setIfPaySuccess(boolean ifPaySuccess) {
        this.ifPaySuccess = ifPaySuccess;
    }
}