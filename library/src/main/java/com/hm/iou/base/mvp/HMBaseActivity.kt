package com.hm.iou.base.mvp

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.hm.iou.base.ActivityManager
import com.hm.iou.base.R
import com.hm.iou.base.utils.StatusBarUtil
import com.hm.iou.base.utils.TraceUtil
import com.hm.iou.network.HttpReqManager
import com.hm.iou.router.Router
import com.hm.iou.sharedata.UserManager
import com.hm.iou.sharedata.event.LogoutEvent
import com.hm.iou.tools.KeyboardUtil
import com.hm.iou.tools.ToastUtil
import com.hm.iou.uikit.dialog.HMAlertDialog
import com.hm.iou.uikit.loading.LoadingDialogUtil
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import org.greenrobot.eventbus.EventBus

/**
 * Created by hjy on 19/10/215.<br></br>
 */
abstract class HMBaseActivity<T : HMBasePresenter<*>> : RxAppCompatActivity(), BaseContract.BaseView, CoroutineScope by MainScope() {

    protected lateinit var mContext: Activity
    protected val mPresenter: T by lazy { initPresenter() }

    private var mUnbinder: Unbinder? = null
    private var mLoadingDialog: Dialog? = null
    private var mRemindKickOff: Boolean = false
    private var mRemindAccountFreeze: Boolean = false
    private var mShowTokenOverdue: Boolean = false

    protected abstract fun getLayoutId(): Int

    protected abstract fun initPresenter(): T

    protected abstract fun initEventAndData(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        val layoutId = getLayoutId()
        if (layoutId != 0) {
            setContentView(layoutId)
        }
        transparentStatusBar()
        initStatusBarDarkFont(true)

        ActivityManager.getInstance().addActivity(this)
        mUnbinder = ButterKnife.bind(this)
        initEventAndData(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    //点击键盘外侧把当前键盘隐藏
    override fun onTouchEvent(event: MotionEvent): Boolean {
        KeyboardUtil.hideKeyboard(mContext)
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isActive) {
            cancel()
        }
        mPresenter?.onDestroy()
        mUnbinder?.unbind()
        ActivityManager.getInstance().removeActivity(this)
    }

    /**
     * 初始化底部导航栏的颜色
     */
    protected fun initNavigationBarColor(color: Int) {
        if (color == Color.TRANSPARENT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = color
        }
    }

    /**
     * 使状态栏透明
     */
    protected fun transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 设置状态栏字体为深色
     */
    protected fun initStatusBarDarkFont(isDarkFont: Boolean) {
        //全屏
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (StatusBarUtil.setMeiZuStatusBarDarkFont(isDarkFont, window)) {
                return
            }
            if (StatusBarUtil.setXiaoMiStatusBarDarkFont(isDarkFont, this)) {

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isDarkFont) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }
        }

    }

    override fun showLoadingView() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(this)
        }
        mLoadingDialog?.show()
    }

    override fun showLoadingView(msg: String?) {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(this, msg, false)
        }
        mLoadingDialog?.let {
            //动态变更msg
            val tvMsg = it.findViewById<TextView>(R.id.tv_loadingMsg)
            if (tvMsg != null && !TextUtils.isEmpty(msg)) {
                tvMsg.text = msg
            }
            it.show()
        }
    }

    override fun dismissLoadingView() {
        LoadingDialogUtil.dismissLoading(mLoadingDialog)
        mLoadingDialog = null
    }

    override fun toastMessage(msg: String?) {
        ToastUtil.showMessage(this, msg)
    }

    override fun toastErrorMessage(msg: String?) {
        ToastUtil.showMessage(this, msg)
    }

    override fun toastMessage(resId: Int) {
        ToastUtil.showMessage(this, resId)
    }

    override fun toastErrorMessage(resId: Int) {
        ToastUtil.showMessage(this, resId)
    }

    override fun closeCurrPage() {
        finish()
    }

    override fun hideSoftKeyboard() {
        KeyboardUtil.hideKeyboard(this)
    }

    override fun showSoftKeyboard() {
        KeyboardUtil.toggleKeyboard(this)
    }

    override fun showSoftKeyboard(view: View) {
        KeyboardUtil.showKeyboard(view)
    }

    override fun showKickOfflineDialog(title: String?, errMsg: String?) {
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return
        }
        mRemindKickOff = true
        clearUserData()
        HMAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("重新登录")
                .setOnClickListener(object : HMAlertDialog.OnClickListener {
                    override fun onPosClick() {
                        TraceUtil.onEvent(mContext, "err_login_other_place")
                        exitAndToLoginPage()
                    }

                    override fun onNegClick() {

                    }
                })
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create()
                .show()
    }

    override fun showTokenOverdue() {
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return
        }
        mShowTokenOverdue = true
        clearUserData()
        exitAndToLoginPage()
    }

    override fun showAccountFreezeDialog(title: String?, errMsg: String?) {
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return
        }
        mRemindAccountFreeze = true
        clearUserData()
        HMAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("退出账号")
                .setOnClickListener(object : HMAlertDialog.OnClickListener {
                    override fun onPosClick() {
                        TraceUtil.onEvent(mContext, "err_black_name")
                        exitAndToLoginPage()
                    }

                    override fun onNegClick() {

                    }
                })
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create()
                .show()
    }

    private fun clearUserData() {
        UserManager.getInstance(this@HMBaseActivity).logout()
        EventBus.getDefault().post(LogoutEvent())
        HttpReqManager.getInstance().setUserId("")
        HttpReqManager.getInstance().setToken("")
    }

    private fun exitAndToLoginPage() {
        ActivityManager.getInstance().exitAllActivities()
        Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/login/selecttype")
                .navigation(this@HMBaseActivity)
    }

}