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
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.hm.iou.tools.KeyboardUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.dialog.IOSAlertDialog;
import com.hm.iou.uikit.loading.LoadingDialogUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hjy on 18/4/26.<br>
 */

public abstract class BaseFragment<T extends MvpFragmentPresenter> extends RxFragment implements BaseContract.BaseView {

    protected View mContentView;
    protected T mPresenter;
    protected Activity mActivity;

    private Unbinder mUnbinder;
    private Dialog mLoadingDialog;
    private boolean mRemindKickOff;
    private boolean mShowTokenOverdue;
    private boolean mRemindAccountFreeze;

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
        mActivity = getActivity();
        //如果是在ViewPager里，或者Fragment恢复现场，不需要重新创建View
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutId(), container, false);
        }
        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        //如果已经创建过Presenter，则不重新创建了
        if (mPresenter == null) {
            mPresenter = initPresenter();
        }
        initEventAndData(savedInstanceState);
        if (mPresenter != null)
            mPresenter.onViewCreated();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.onDestroyView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
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
    public void showKickOfflineDialog(String title, String errMsg) {
        if (getActivity() == null) {
            return;
        }
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        clearUserData();
        mRemindKickOff = true;
        new IOSAlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitAndToLoginPage();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void showAccountFreezeDialog(String title, String errMsg) {
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        mRemindAccountFreeze = true;
        clearUserData();
        new IOSAlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(errMsg)
                .setNegativeButton("退出账号", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitAndToLoginPage();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void showTokenOverdue() {
        if (getActivity() == null) {
            return;
        }
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        clearUserData();
        mShowTokenOverdue = true;
        exitAndToLoginPage();
    }

    private void clearUserData() {
        UserManager.getInstance(getActivity()).logout();
        EventBus.getDefault().post(new LogoutEvent());
        HttpReqManager.getInstance().setUserId("");
        HttpReqManager.getInstance().setToken("");
    }

    private void exitAndToLoginPage() {
        ActivityManager.getInstance().exitAllActivities();
        Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/login/selecttype")
                .navigation(getActivity());
    }

}
