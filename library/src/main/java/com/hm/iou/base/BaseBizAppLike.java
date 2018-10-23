package com.hm.iou.base;

import android.content.Context;

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
        mContext = context;
        mApp = this;
    }

    public void initServer(String apiServer, String fileServer, String h5Server) {
        mApiServer = apiServer;
        mFileServer = fileServer;
        mH5Server = h5Server;
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
}
