package com.hm.iou.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hjy on 18/4/26.<br>
 */

public abstract class BaseFragment<T extends MvpFragmentPresenter> extends RxFragment implements BaseContract.BaseView {

    protected View mContentView;
    protected T mPresenter;

    private Unbinder mUnbinder;

    /**
     * 获取当前页面的layout id
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 创建Presenter
     *
     * @return
     */
    protected abstract T initPresenter();

    /**
     * 初始化事件和数据
     *
     * @param savedInstanceState
     */
    protected abstract void initEventAndData(Bundle savedInstanceState);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(getLayoutId(), container, false);
        mPresenter = initPresenter();
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        initEventAndData(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
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
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void hideSoftKeyboard() {

    }

    @Override
    public void showSoftKeyboard() {

    }
}
