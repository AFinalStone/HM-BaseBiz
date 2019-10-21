package com.hm.iou.base.mvp

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.hm.iou.base.ActivityManager
import com.hm.iou.base.BaseActivity
import com.hm.iou.base.R
import com.hm.iou.base.utils.TraceUtil
import com.hm.iou.network.HttpReqManager
import com.hm.iou.router.Router
import com.hm.iou.sharedata.UserManager
import com.hm.iou.sharedata.event.LogoutEvent
import com.hm.iou.tools.KeyboardUtil
import com.hm.iou.tools.ToastUtil
import com.hm.iou.uikit.dialog.HMAlertDialog
import com.hm.iou.uikit.loading.LoadingDialogUtil
import com.trello.rxlifecycle2.components.support.RxFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.greenrobot.eventbus.EventBus

/**
 * Created by hjy on 18/4/26.<br></br>
 */

abstract class HMBaseFragment<T : HMBaseFragmentPresenter<*>> : RxFragment(), BaseContract.BaseView, CoroutineScope by MainScope() {

    protected val mPresenter: T by lazy { initPresenter() }

    protected var mActivity: Activity? = null
    protected var mContentView: View? = null

    private var mUnbinder: Unbinder? = null
    private var mLoadingDialog: Dialog? = null
    private var mRemindKickOff: Boolean = false
    private var mShowTokenOverdue: Boolean = false
    private var mRemindAccountFreeze: Boolean = false

    protected abstract fun getLayoutId(): Int

    /**
     * 创建Presenter
     *
     * @return
     */
    protected abstract fun initPresenter(): T

    /**
     * 初始化事件和数据
     *
     * @param savedInstanceState
     */
    protected abstract fun initEventAndData(savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mActivity = activity
        //如果是在ViewPager里，或者Fragment恢复现场，不需要重新创建View
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutId(), container, false)
            mContentView?.isClickable = true
        }
        val parent = mContentView?.parent as? ViewGroup
        parent?.removeView(mContentView)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUnbinder = ButterKnife.bind(this, view)
        initEventAndData(savedInstanceState)
        mPresenter.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //在 View 被销毁的时候，取消协程里的所有 Job
        cancel()
        mPresenter.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
        mUnbinder?.unbind()
    }

    override fun showLoadingView() {
        val activity = activity ?: return
        if (activity is BaseActivity<*>) {
            activity.showLoadingView()
            return
        }
        if (activity is HMBaseActivity<*>) {
            activity.showLoadingView()
            return
        }
        //保留旧代码
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(activity)
        }
        mLoadingDialog!!.show()

    }

    override fun showLoadingView(msg: String?) {
        val activity = activity ?: return
        if (activity is BaseActivity<*>) {
            activity.showLoadingView(msg)
            return
        }
        if (activity is HMBaseActivity<*>) {
            activity.showLoadingView(msg)
            return
        }
        //保留旧代码
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogUtil.showLoading(activity, msg, false)
        }
        val tvMsg = mLoadingDialog!!.findViewById<TextView>(R.id.tv_loadingMsg)
        if (tvMsg != null && !TextUtils.isEmpty(msg)) {
            tvMsg.text = msg
        }
        mLoadingDialog!!.show()
    }

    override fun dismissLoadingView() {
        val activity = activity ?: return
        if (activity is BaseActivity<*>) {
            activity.dismissLoadingView()
            return
        }
        if (activity is HMBaseActivity<*>) {
            activity.dismissLoadingView()
            return
        }
        LoadingDialogUtil.dismissLoading(mLoadingDialog)
        mLoadingDialog = null
    }

    override fun toastMessage(msg: String?) {
        activity ?: return
        ToastUtil.showMessage(activity, msg)
    }

    override fun toastErrorMessage(msg: String?) {
        activity ?: return
        ToastUtil.showMessage(activity, msg)
    }

    override fun toastMessage(resId: Int) {
        activity ?: return
        ToastUtil.showMessage(activity, resId)
    }

    override fun toastErrorMessage(resId: Int) {
        activity ?: return
        ToastUtil.showMessage(activity, resId)
    }

    override fun closeCurrPage() {
        val activity = activity
        activity?.finish()
    }

    override fun hideSoftKeyboard() {
        activity ?: return
        KeyboardUtil.hideKeyboard(activity)
    }

    override fun showSoftKeyboard() {
        activity ?: return
        KeyboardUtil.toggleKeyboard(activity)
    }

    override fun showSoftKeyboard(view: View) {
        KeyboardUtil.showKeyboard(view)
    }

    override fun showKickOfflineDialog(title: String?, errMsg: String?) {
        val activity = activity ?: return
        if (activity is BaseActivity<*>) {
            activity.showKickOfflineDialog(title, errMsg)
            return
        }
        if (activity is HMBaseActivity<*>) {
            activity.showKickOfflineDialog(title, errMsg)
            return
        }
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return
        }
        clearUserData()
        mRemindKickOff = true
        HMAlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("重新登录")
                .setOnClickListener(object : HMAlertDialog.OnClickListener {
                    override fun onPosClick() {
                        TraceUtil.onEvent(mActivity, "err_login_other_place")
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

    override fun showAccountFreezeDialog(title: String?, errMsg: String?) {
        val activity = activity ?: return
        if (activity is BaseActivity<*>) {
            activity.showAccountFreezeDialog(title, errMsg)
            return
        }
        if (activity is HMBaseActivity<*>) {
            activity.showAccountFreezeDialog(title, errMsg)
            return
        }
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return
        }
        mRemindAccountFreeze = true
        clearUserData()
        HMAlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(errMsg)
                .setPositiveButton("退出账号")
                .setOnClickListener(object : HMAlertDialog.OnClickListener {
                    override fun onPosClick() {
                        TraceUtil.onEvent(mActivity, "err_black_name")
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
        val activity = activity ?: return
        if (activity is BaseActivity<*>) {
            activity.showTokenOverdue()
            return
        }
        if (activity is HMBaseActivity<*>) {
            activity.showTokenOverdue()
            return
        }
        if (mRemindKickOff || mShowTokenOverdue || mRemindAccountFreeze) {
            return
        }
        clearUserData()
        mShowTokenOverdue = true
        exitAndToLoginPage()
    }

    private fun clearUserData() {
        UserManager.getInstance(activity).logout()
        EventBus.getDefault().post(LogoutEvent())
        HttpReqManager.getInstance().setUserId("")
        HttpReqManager.getInstance().setToken("")
    }

    private fun exitAndToLoginPage() {
        ActivityManager.getInstance().exitAllActivities()
        Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/login/selecttype")
                .navigation(activity)
    }

}
