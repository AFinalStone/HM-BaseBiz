package com.hm.iou.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.hm.iou.base.utils.TraceUtil;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.hm.iou.tools.KeyboardUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.dialog.HMAlertDialog;
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
            mContentView.setClickable(true);
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
        Activity activity = getActivity();
        if (activity == null)
            return;
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showLoadingView();
            return;
        }
        //保留旧代码
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(activity);
        }
        mLoadingDialog.show();

    }

    @Override
    public void showLoadingView(String msg) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showLoadingView(msg);
            return;
        }
        //保留旧代码
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(activity, msg, false);
        }
        TextView tvMsg = mLoadingDialog.findViewById(R.id.tv_loadingMsg);
        if (tvMsg != null && !TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);
        }
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingView() {
        Activity activity = getActivity();
        if (activity == null)
            return;
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).dismissLoadingView();
            return;
        }
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
    public void toastErrorMessage(String msg) {
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
    public void toastErrorMessage(int resId) {
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
        KeyboardUtil.toggleKeyboard(getActivity());
    }

    @Override
    public void showSoftKeyboard(View view) {
        KeyboardUtil.showKeyboard(view);
    }

    @Override
    public void showKickOfflineDialog(String title, String errMsg) {
        if (getActivity() == null) {
            return;
        }
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showKickOfflineDialog(title, errMsg);
            return;
        }
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        clearUserData();
        mRemindKickOff = true;
        new HMAlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("重新登录")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        TraceUtil.onEvent(mActivity, "err_login_other_place");
                        exitAndToLoginPage();
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create()
                .show();
    }

    @Override
    public void showAccountFreezeDialog(String title, String errMsg) {
        if (getActivity() == null) {
            return;
        }
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showAccountFreezeDialog(title, errMsg);
            return;
        }
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        mRemindAccountFreeze = true;
        clearUserData();
        new HMAlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("退出账号")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        TraceUtil.onEvent(mActivity, "err_black_name");
                        exitAndToLoginPage();
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create()
                .show();
    }

    @Override
    public void showTokenOverdue() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showTokenOverdue();
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
