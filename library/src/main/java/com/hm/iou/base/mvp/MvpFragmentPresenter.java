package com.hm.iou.base.mvp;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * Created by hjy on 18/4/26.<br>
 */

public abstract class MvpFragmentPresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter {

    protected LifecycleProvider<FragmentEvent> mLifecycleProvider;
    protected T mView;

    public MvpFragmentPresenter(@NonNull T view) {
        mView = view;
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

}