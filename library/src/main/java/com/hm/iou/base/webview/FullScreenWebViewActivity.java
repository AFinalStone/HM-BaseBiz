package com.hm.iou.base.webview;

import android.os.Bundle;

import com.hm.iou.base.R;

/**
 * Created by hjy on 2018/10/22.
 */

public class FullScreenWebViewActivity extends BaseWebviewActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.base_activity_fullscreen_webview;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        super.initEventAndData(savedInstanceState);
    }
}
