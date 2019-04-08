package com.hm.iou.wxapi;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.umeng.socialize.handler.UMWXHandler;

/**
 * Created by hjy on 2019/4/8.
 */

public class UMHandler extends UMWXHandler {

    protected void onAuthCallback(SendAuth.Resp resp) {
        if (resp.errCode == 0) {

        } else {
            super.onAuthCallback(resp);
        }
    }

}
