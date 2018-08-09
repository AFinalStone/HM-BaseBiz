package com.hm.iou.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.StatusBarUtil;
import com.hm.iou.base.utils.TraceUtil;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.hm.iou.tools.KeyboardUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.dialog.IOSAlertDialog;
import com.hm.iou.uikit.loading.LoadingDialogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hjy on 18/4/26.<br>
 */

public abstract class BaseActivity<T extends MvpActivityPresenter> extends RxAppCompatActivity implements BaseContract.BaseView {

    protected Activity mContext;

    private Unbinder mUnbinder;

    protected T mPresenter;

    private Dialog mLoadingDialog;
    private boolean mRemindKickOff;
    private boolean mRemindAccountFreeze;
    private boolean mShowTokenOverdue;

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
        mContext = this;
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(getLayoutId());
        }
        transparentStatusBar();
        initStatusBarDarkFont(true);

        ActivityManager.getInstance().addActivity(this);
        mUnbinder = ButterKnife.bind(this);
        mPresenter = initPresenter();
        initEventAndData(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    //点击键盘外侧把当前键盘隐藏
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        KeyboardUtil.hideKeyboard(mContext);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        mUnbinder.unbind();
        ActivityManager.getInstance().removeActivity(this);
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

    /**
     * 使状态栏透明
     */
    protected void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏字体为深色
     */
    protected void initStatusBarDarkFont(boolean isDarkFont) {
        //全屏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (StatusBarUtil.setXiaoMiStatusBarDarkFont(isDarkFont, this)) {

            } else if (StatusBarUtil.setMeiZuStatusBarDarkFont(isDarkFont, getWindow())) {

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isDarkFont) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }
        }

    }


    @Override
    public void showLoadingView() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(this);
        }
        mLoadingDialog.show();
    }

    @Override
    public void showLoadingView(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(this, msg, false);
        } else {
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoadingView() {
        LoadingDialogUtil.dismissLoading(mLoadingDialog);
        mLoadingDialog = null;
    }

    @Override
    public void toastMessage(String msg) {
        ToastUtil.showMessage(this, msg);
    }

    @Override
    public void toastMessage(int resId) {
        ToastUtil.showMessage(this, resId);
    }

    @Override
    public void closeCurrPage() {
        finish();
    }

    @Override
    public void hideSoftKeyboard() {
        KeyboardUtil.hideKeyboard(this);
    }

    @Override
    public void showSoftKeyboard() {
        KeyboardUtil.openKeyboard(this);
    }

    @Override
    public void showKickOfflineDialog(String title, String errMsg) {
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        mRemindKickOff = true;
        clearUserData();
        new IOSAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TraceUtil.onEvent(mContext, "err_login_other_place");
                        exitAndToLoginPage();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void showTokenOverdue() {
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        mShowTokenOverdue = true;
        clearUserData();
        exitAndToLoginPage();
    }

    @Override
    public void showAccountFreezeDialog(String title, String errMsg) {
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return;
        }
        mRemindAccountFreeze = true;
        clearUserData();
        new IOSAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(errMsg)
                .setNegativeButton("退出账号", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TraceUtil.onEvent(mContext, "err_black_name");
                        exitAndToLoginPage();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void clearUserData() {
        UserManager.getInstance(BaseActivity.this).logout();
        EventBus.getDefault().post(new LogoutEvent());
        HttpReqManager.getInstance().setUserId("");
        HttpReqManager.getInstance().setToken("");
    }

    private void exitAndToLoginPage() {
        ActivityManager.getInstance().exitAllActivities();
        Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/login/selecttype")
                .navigation(BaseActivity.this);
    }

}