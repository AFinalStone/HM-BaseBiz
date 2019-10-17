package com.hm.iou.base.demo;

import android.app.Application;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.logger.Logger;
import com.hm.iou.router.Router;


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
        baseBizAppLike.setDebug(true);
//        baseBizAppLike.initServer("http://dev.54jietiao.com", "http://dev.54jietiao.com", "http://dev.54jietiao.com");
        baseBizAppLike.initServer("http://192.168.1.82:8071", "http://192.168.1.82", "http://192.168.1.82");
   }

}
