package com.hm.iou.base.demo;

import android.app.Application;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.logger.Logger;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;


/**
 * @author syl
 * @time 2018/5/14 下午3:23
 */
public class BaseBizDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.init(this);
        Logger.init(this, true);
        BaseBizAppLike baseBizAppLike = new BaseBizAppLike();
        baseBizAppLike.onCreate(this);
        baseBizAppLike.initServer("http://dev.54jietiao.com", "http://dev.54jietiao.com",
                "http://dev.54jietiao.com");
//        baseBizAppLike.initServer("http://192.168.1.82:8071", "http://192.168.1.82",
//        "http://192.168.1.82");
        initNetwork();
    }


    private void initNetwork() {
        System.out.println("init-----------");
        HttpRequestConfig config = new HttpRequestConfig.Builder(this)
                .setDebug(true)
                .setAppChannel("yyb")
                .setAppVersion("1.2.0.1")
                .setDeviceId("123abc123")
                .setBaseUrl(BaseBizAppLike.getInstance().getApiServer())
                .setUserId(UserManager.getInstance(this).getUserId())
                .setToken(UserManager.getInstance(this).getToken())
                .build();
        HttpReqManager.init(config);
    }

}
