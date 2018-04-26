package com.hm.iou.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hjy on 18/4/26.<br>
 */

public abstract class BaseActivity<T extends MvpActivityPresenter> extends RxAppCompatActivity implements BaseContract.BaseView {

    private Unbinder mUnbinder;

    protected T mPresenter;

    /**
     * 获取当前页面的layout id
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract T initPresenter();

    protected abstract void initEventAndData(Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        mPresenter = initPresenter();
        initEventAndData(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    /**
     * 初始化底部导航栏的颜色
     */
    protected void initNavigationBarColor(int color) {
        if (color == Color.TRANSPARENT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(color);
        }
    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void showLoadingView(String msg) {

    }

    @Override
    public void dismissLoadingView() {

    }

    @Override
    public void toastMessage(String msg) {

    }

    @Override
    public void toastMessage(int resId) {

    }

    @Override
    public void closeCurrPage() {
        finish();
    }

    @Override
    public void hideSoftKeyboard() {

    }

    @Override
    public void showSoftKeyboard() {

    }
}