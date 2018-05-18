package com.hm.iou.base.mvp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * Created by hjy on 18/4/26.<br>
 */

public abstract class MvpFragmentPresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter {

    protected LifecycleProvider<FragmentEvent> mLifecycleProvider;
    protected Context mContext;
    protected T mView;

    public MvpFragmentPresenter(@NonNull Context context, @NonNull T view) {
        mView = view;
        mContext = context;
        if (view instanceof LifecycleProvider) {
            mLifecycleProvider = (LifecycleProvider<FragmentEvent>) view;
        } else {
            throw new IllegalArgumentException("The view must be a instance of LifecycleProvider.");
        }
    }

    public T getView() {
        return mView;
    }

    public LifecycleProvider<FragmentEvent> getProvider() {
        return mLifecycleProvider;
    }

    public void onViewCreated() {

    }

    public void onDestroyView() {

    }

    public void onDestroy() {

    }

}