package com.hm.iou.base.demo;

import android.os.Bundle;
import android.widget.Button;

import com.hm.iou.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by hjy on 18/4/26.<br>
 */

public class TestActivity extends BaseActivity<TestPresenter> implements TestContract.View {

    @BindView(R.id.btn_test)
    Button mBtnTest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected TestPresenter initPresenter() {
        return new TestPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {

    }
}
