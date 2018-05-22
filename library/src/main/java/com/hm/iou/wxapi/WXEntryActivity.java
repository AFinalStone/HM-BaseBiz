package com.hm.iou.wxapi;


import android.content.Context;
import android.util.Log;

import com.hm.iou.base.event.OpenWxResultEvent;
import com.hm.iou.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.greenrobot.eventbus.EventBus;

//public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

/**
 * @author AFinalStone
 * @time 2018/3/28 下午10:14
 * 绑定微信的回调页面
 * 1.用户未登录，直接使用微信登录的方式，会触发这个页面
 * 2.用户已经登录，在个人中心进行微信绑定的操作
 */
public class WXEntryActivity extends WXCallbackActivity {

    private static final String APP_ID = "wx54a8a6252c69ea7c";

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Log.e("WXTest", "onResp OK");
                if (baseResp instanceof SendAuth.Resp) {
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
                    EventBus.getDefault().post(new OpenWxResultEvent(key, code));
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Logger.d("WXTest", "onResp ERR_USER_CANCEL ");
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Logger.d("WXTest", "onResp ERR_AUTH_DENIED");
                //发送被拒绝
                break;
            default:
                Logger.d("WXTest", "onResp default errCode " + baseResp.errCode);
                //发送返回
                break;
        }
        super.onResp(baseResp);
    }


    public static void openWx(Context context, String key) {
        //微信原生SDK登录
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        iwxapi.registerApp(APP_ID);

        //发起登录请求
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = key;
        iwxapi.sendReq(req);
    }

}
