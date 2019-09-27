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
    //加解密秘钥以及版本号需要更新
    public static final int ERR_CODE_ENCRYPT_NEED_UPDATE = 2201002;

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

    /**
     * 系统配置 SharedPreferences 文件名
     */
    public static final String SP_SYS_CONFIG = "sysconfig";
    public static final String SP_KEY_RSA_KEY = "rsa_key";
    public static final String SP_KEY_RSA_VERSION = "rsa_version";
    public static final String SP_KEY_REALNAME_CHANNEL = "realname_channel";    //保存实名认证渠道
    public static final String SP_KEY_REALNAME_OCR_WAY = "realname_ocr_way";    //OCR是自动还是手动


    public static final String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCq6i/S/uZA4Yy83XqrNzI00HaSW7U9W8W9hevRNAihAbsxy8kzBMDzadJ1Kyj+r2vEeA0P0mSRnMIgyNg3lRwxJw9T0PyrzQmag9w23u9zTViPYdwQ9F16QFhGI0g0Xx4G1jr5IWn+qrmA9AsVvKmq1A/aGyjPmBwsu5/DryovuwIDAQAB";
    public static final String RSA_PUBLIC_VERSION = "20190923";

}
