package com.hm.iou.wxapi;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hm.iou.base.event.OpenWxResultEvent;
import com.hm.iou.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.Map;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    private static String getAppId() {
        PlatformConfig.APPIDPlatform weixin = (PlatformConfig.APPIDPlatform) PlatformConfig.configs.get(SHARE_MEDIA.WEIXIN);
        String appId = weixin.appId;
        return appId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(getApplicationContext(), getAppId(), false);
        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Logger.d("微信请求openId" + baseReq.openId);
        Logger.d("微信请求transaction" + baseReq.transaction);
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Logger.d("微信回调: ErrorCode=" + baseResp.errCode);
        Logger.d("微信回调: Type=" + baseResp.getType());
        Logger.d("微信回调: ErrStr=" + baseResp.errStr);
        if (baseResp instanceof SendAuth.Resp) {
            //微信登录
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
                    //获取微信传回的code
                    String code = newResp.code;
                    String key = newResp.state;
                    String lang = newResp.lang;
                    String contry = newResp.country;
                    String url = newResp.url;
                    Logger.d("onRespPro code = " + code);
                    Logger.d("onRespPro state = " + key);
                    Logger.d("onRespPro lang = " + lang);
                    Logger.d("onRespPro contry = " + contry);
                    Logger.d("onRespPro url = " + url);
                    OpenWxResultEvent event = new OpenWxResultEvent();
                    event.setCode(code);
                    event.setKey(key);
                    EventBus.getDefault().post(event);
                    break;
                default:
//                    ToastUtil.showMessage(this, baseResp.errStr);
                    break;
            }
        }
        finish();
    }

    /**
     * 微信原生SDK登录
     *
     * @param context
     * @param key     额外辅助字段
     */
    @Deprecated
    public static void openWx(Context context, String key) {
        String appId = getAppId();
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context.getApplicationContext(), appId, true);
        iwxapi.registerApp(appId);

        //发起登录请求
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = key;
        iwxapi.sendReq(req);
    }

    /**
     * 掉起微信授权
     *
     * @param context Activity
     * @param key     业务发起方自定义key，用来标识业务
     * @return
     */
    public static IWXAPI openWxAuth(Context context, String key) {
        String appId = getAppId();
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context.getApplicationContext(), appId, true);
        iwxapi.registerApp(appId);

        //发起登录请求
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = key;
        iwxapi.sendReq(req);

        return iwxapi;
    }

    /**
     * 清除微信memory leak
     */
    public static void cleanWXLeak() {
        try {
            Class clazz = com.tencent.a.a.a.a.g.class;
            Field field = clazz.getDeclaredField("V");
            field.setAccessible(true);
            Object obj = field.get(clazz);
            if (obj != null) {
                com.tencent.a.a.a.a.g g = (com.tencent.a.a.a.a.g) obj;
                Field mapField = clazz.getDeclaredField("U");
                mapField.setAccessible(true);
                Map map = (Map) mapField.get(g);
                map.clear();
            }
            field.set(clazz, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



