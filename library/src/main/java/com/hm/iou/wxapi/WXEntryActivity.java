package com.hm.iou.wxapi;


import android.content.Context;

import com.hm.iou.base.event.OpenWxResultEvent;
import com.hm.iou.logger.Logger;
import com.hm.iou.tools.ToastUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.greenrobot.eventbus.EventBus;


/**
 * @author AFinalStone
 * @time 2018/3/28 下午10:14
 * 微信登录，绑定，友盟分享的回调页面，
 * 1.用户未登录，直接使用微信登录的方式，会触发这个页面
 * 2.用户已经登录，在个人中心进行微信绑定的操作
 */
//public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
public class WXEntryActivity extends WXCallbackActivity {

    private static final String APP_ID = "wx54a8a6252c69ea7c";

    @Override
    public void onReq(BaseReq baseReq) {
        super.onReq(baseReq);
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
        super.onResp(baseResp);
        Logger.d("微信回调ErrorCode" + baseResp.errCode);
        Logger.d("微信回调Type" + baseResp.getType());
        Logger.d("微信回调ErrStr" + baseResp.errStr);
        if (baseResp instanceof SendAuth.Resp) {
            //微信登录
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    Logger.d("WXLogin", "微信登录测试");
                    SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
                    //获取微信传回的code
                    String code = newResp.code;
                    String key = newResp.state;
                    String lang = newResp.lang;
                    String contry = newResp.country;
                    String url = newResp.url;
                    Logger.d("WXTest", "onResp code = " + code);
                    Logger.d("WXTest", "onResp state = " + key);
                    Logger.d("WXTest", "onResp lang = " + lang);
                    Logger.d("WXTest", "onResp contry = " + contry);
                    Logger.d("WXTest", "onResp url = " + url);
                    OpenWxResultEvent event = new OpenWxResultEvent();
                    event.setCode(code);
                    event.setKey(key);
                    EventBus.getDefault().post(event);
                    finish();
                    break;
                default:
                    ToastUtil.showMessage(this, baseResp.errStr);
                    break;
            }
        }
    }

    /**
     * 微信原生SDK登录
     *
     * @param context
     * @param key     额外辅助字段
     */
    public static void openWx(Context context, String key) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        iwxapi.registerApp(APP_ID);

        //发起登录请求
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = key;
        iwxapi.sendReq(req);
    }

}
