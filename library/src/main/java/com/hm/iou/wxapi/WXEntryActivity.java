package com.hm.iou.wxapi;


import android.content.Context;
import android.os.Bundle;

import com.hm.iou.base.event.OpenWxResultEvent;
import com.hm.iou.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.SocialRouter;
import com.umeng.socialize.handler.UMSSOHandler;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.Map;

public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UMShareAPI api = UMShareAPI.get(this.getApplicationContext());

        //由于使用了友盟分享SDK，在微信登录里，它会自动调用微信接口，导致 code 失效，采用反射去掉该逻辑
        try {
            Field field = UMShareAPI.class.getDeclaredField("router");
            field.setAccessible(true);
            SocialRouter router = (SocialRouter) field.get(api);

            Field field2 = SocialRouter.class.getDeclaredField("platformHandlers");
            field2.setAccessible(true);
            Map<SHARE_MEDIA, UMSSOHandler> map = (Map<SHARE_MEDIA, UMSSOHandler>) field2.get(router);
            map.put(SHARE_MEDIA.WEIXIN, new UMHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            //微信登录
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
                    //获取微信传回的code
                    String code = newResp.code;
                    String key = newResp.state;
                    Logger.d("onRespPro code = " + code);
                    Logger.d("onRespPro state = " + key);
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

        super.onResp(baseResp);
    }

    private static String getAppId() {
        PlatformConfig.APPIDPlatform weixin = (PlatformConfig.APPIDPlatform) PlatformConfig.configs.get(SHARE_MEDIA.WEIXIN);
        String appId = weixin.appId;
        return appId;
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



