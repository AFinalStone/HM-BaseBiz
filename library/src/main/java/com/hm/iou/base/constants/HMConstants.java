package com.hm.iou.base.constants;

import java.io.File;

/**
 * Created by hjy on 18/4/26.<br>
 */

public class HMConstants {

    //用户被踢下线
    public static final int ERR_CODE_KICK_OFFLINE = 202001;
    //token过期
    public static final int ERR_CODE_TOKEN_OVERDUE = 202007;
    //用户账号被冻结
    public static final int ERR_CODE_ACCOUNT_FREEZE = 202013;

    public static final String REG_MOBILE = "^[1][0-9]{10}$";

    public static final String REG_EMAIL_NUMBER = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$";

    public static final String EXTERNAL_CACHE_DIR = "54jietiao";
    public static final String IMAGE_CACHE_DIR = EXTERNAL_CACHE_DIR + File.separator + "image";

    public static String FILE_PROVIDER_SUFFIX = ".fileprovider";

    public static final String URL_BEGIN_BY_FILE = "file:///";

    /**
     * 专门用于WebView存储的SharedPreferences文件名
     */
    public static final String SP_WEBVIEW = "sp_webview";
}
