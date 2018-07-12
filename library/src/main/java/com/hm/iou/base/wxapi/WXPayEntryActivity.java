package com.hm.iou.base.wxapi;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.hm.iou.base.event.OpenWxResultEvent;
import com.hm.iou.logger.Logger;
import com.hm.iou.tools.ToastUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;


/**
 * @author AFinalStone
 * @time 2018/7/12 下午10:14
 * 微信支付的回调页面
 */
public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String APP_ID = "wx54a8a6252c69ea7c";

    @Override
    public void onReq(BaseReq baseReq) {
        Logger.d("微信请求openId" + baseReq.openId);
        Logger.d("微信请求transaction" + baseReq.transaction);
    }

    /**
     * 微信回调方法
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        Logger.d("微信回调ErrorCode" + baseResp.errCode);
        Logger.d("微信回调Type" + baseResp.getType());
        Logger.d("微信回调ErrStr" + baseResp.errStr);
        if (baseResp instanceof PayResp) {
            //微信支付
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    Logger.d("WXPay", "微信支付测试");
                    String key = ((PayResp) baseResp).extData;
                    OpenWxResultEvent event = new OpenWxResultEvent();
                    event.setKey(key);
                    event.setIfPaySuccess(true);
                    EventBus.getDefault().post(event);
                    break;
                default:
                    ToastUtil.showMessage(this, baseResp.errStr);
            }
        }
    }

    /**
     * 微信支付
     *
     * @param context
     * @param partnerId    商户号
     * @param packageValue
     * @param nonceStr
     * @param timeStamp
     * @param sign
     * @param key
     */
    public static void wxPay(Context context, String partnerId, String prepayid
            , String packageValue, String nonceStr, String timeStamp, String sign, String key) {
        //微信原生SDK登录
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        iwxapi.registerApp(APP_ID);

        PayReq request = new PayReq();
        request.appId = APP_ID;
        request.partnerId = partnerId;
        request.prepayId = prepayid;
        request.packageValue = packageValue;
        request.nonceStr = nonceStr;
        request.timeStamp = timeStamp;
        request.sign = sign;
        request.extData = key;
        iwxapi.sendReq(request);
    }

}
