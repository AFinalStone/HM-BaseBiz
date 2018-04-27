package com.hm.iou.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.tools.KeyboardUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.dialog.IOSAlertDialog;
import com.hm.iou.uikit.loading.LoadingDialogUtil;
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
    private Dialog mLoadingDialog;
    private boolean mRemindUserNotLogin;        //是否已经弹出过提醒用户未登录的对话框，防止重复弹出

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
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void showLoadingView() {
        if (getActivity() == null)
            return;
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(getActivity());
        }
        mLoadingDialog.show();
    }

    @Override
    public void showLoadingView(String msg) {
        if (getActivity() == null)
            return;
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(getActivity(), msg, false);
        } else {
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoadingView() {
        if (getActivity() == null)
            return;
        LoadingDialogUtil.dismissLoading(mLoadingDialog);
        mLoadingDialog = null;
    }

    @Override
    public void toastMessage(String msg) {
        if (getActivity() == null)
            return;
        ToastUtil.showMessage(getActivity(), msg);
    }

    @Override
    public void toastMessage(int resId) {
        if (getActivity() == null)
            return;
        ToastUtil.showMessage(getActivity(), resId);
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
        if (getActivity() == null)
            return;
        KeyboardUtil.hideKeyboard(getActivity());
    }

    @Override
    public void showSoftKeyboard() {
        if (getActivity() == null)
            return;
        KeyboardUtil.openKeyboard(getActivity());
    }

    @Override
    public void showUserNotLogin(String errMsg) {
        if (getActivity() == null) {
            return;
        }
        if (mRemindUserNotLogin) {
            return;
        }
        UserManager.getInstance(getActivity()).logout();
        mRemindUserNotLogin = true;
        new IOSAlertDialog.Builder(getActivity())
                .setTitle("下线通知")
                .setMessage(errMsg)
                .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityManager.getInstance().exitAllActivities();
                        //TODO 需要用路由跳转到登录页
                        try {
                            startActivity(new Intent(getActivity(),
                                    Class.forName("com.hm.iou.hmreceipt.ui.activity.login.LoginSelectActivity")));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityManager.getInstance().exitAllActivities();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
