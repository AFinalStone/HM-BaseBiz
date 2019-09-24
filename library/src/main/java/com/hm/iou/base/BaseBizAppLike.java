package com.hm.iou.base;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hm.iou.base.comm.CommApi;
import com.hm.iou.base.comm.CurrRsaKeyBean;
import com.hm.iou.base.constants.HMConstants;
import com.hm.iou.base.event.UpdateRsaKeyEvent;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.tools.SPUtil;
import com.hm.iou.tools.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

import static com.hm.iou.base.constants.HMConstants.SP_SYS_CONFIG;

/**
 * Created by hjy on 18/5/15.<br>
 */

public class BaseBizAppLike {

    public static BaseBizAppLike getInstance() {
        if (mApp == null) {
            throw new RuntimeException("BaseBizAppLike should init first.");
        }
        return mApp;
    }

    private static BaseBizAppLike mApp;

    private Context mContext;
    private String mApiServer;
    private String mFileServer;
    private String mH5Server;
    private boolean mDebug;

    public void onCreate(Context context) {
        mContext = context.getApplicationContext();
        mApp = this;
        EventBus.getDefault().register(this);
    }

    public void initServer(String apiServer, String fileServer, String h5Server) {
        mApiServer = apiServer;
        mFileServer = fileServer;
        mH5Server = h5Server;

        initNetwork();
    }

    public void setDebug(boolean isDebug) {
        mDebug = isDebug;
    }

    public String getApiServer() {
        return mApiServer;
    }

    public String getFileServer() {
        return mFileServer;
    }

    public String getH5Server() {
        return mH5Server;
    }

    public boolean isDebug() {
        return mDebug;
    }

    /**
     * 初始化新的网络框架
     */
    private void initNetwork() {
        String deviceId = SPUtil.getString(mContext, SP_SYS_CONFIG, "deviceId");
        if (TextUtils.isEmpty(deviceId)) {
            //采用自己生产的UUID来当做设备唯一ID，存储在SharedPreferenes里，应用卸载重装会重新生成
            deviceId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            SPUtil.put(mContext, SP_SYS_CONFIG, "deviceId", deviceId);
        }

        String channel = "official";
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String rsaKey = SPUtil.getString(mContext, SP_SYS_CONFIG, HMConstants.SP_KEY_RSA_KEY, HMConstants.RSA_PUBLIC_KEY);
        String rsaVersion = SPUtil.getString(mContext, SP_SYS_CONFIG, HMConstants.SP_KEY_RSA_VERSION, HMConstants.RSA_PUBLIC_VERSION);

        UserManager userManager = UserManager.getInstance(mContext);
        HttpRequestConfig config = new HttpRequestConfig.Builder(mContext)
                .setDebug(isDebug())
                .setConnectTimeout(30, TimeUnit.SECONDS)
                .setReadTimeout(30, TimeUnit.SECONDS)
                .setAppChannel(channel)
                .setAppVersion(SystemUtil.getCurrentAppVersionName(mContext))
                .setDeviceId(deviceId)
                .setBaseUrl(BaseBizAppLike.getInstance().getApiServer())
                .setUserId(userManager.getUserInfo().getUserId())
                .setToken(userManager.getUserInfo().getToken())
                .setRsaPubKey(rsaKey)
                .setRsaPubVersion(rsaVersion)
                .build();
        HttpReqManager.init(config);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRsaKeyUpdate(UpdateRsaKeyEvent event) {
        CommApi.getCurrentRsaKey()
                .map(RxUtil.<CurrRsaKeyBean>handleResponse())
                .subscribe(new Consumer<CurrRsaKeyBean>() {
                    @Override
                    public void accept(CurrRsaKeyBean data) throws Exception {
                        if (data != null) {
                            String key = data.getRsaPubKey();
                            String version = data.getPubVersion();
                            SPUtil.put(mContext, SP_SYS_CONFIG, HMConstants.SP_KEY_RSA_KEY, key);
                            SPUtil.put(mContext, SP_SYS_CONFIG, HMConstants.SP_KEY_RSA_VERSION, version);
                            HttpReqManager.getInstance().setRsaKey(version, key);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

}
