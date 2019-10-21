package com.hm.iou.base.mvp

import android.content.Context
import com.hm.iou.base.constants.HMConstants
import com.hm.iou.base.event.UpdateRsaKeyEvent
import com.hm.iou.network.exception.ApiException
import com.hm.iou.sharedata.event.LogoutEvent
import com.hm.iou.sharedata.model.BaseResponse
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by hjy on 19/10/15.<br></br>
 *
 * 构造函数包含2个参数，1：context；2：view，通常是HMBaseActivity的子类
 *
 * 默认情况下每个 Presenter 都实现了 CoroutineScope 接口，一般会与 view 实例对象共享同一个 CoroutineScope，
 * 如果 view 不是 HMBaseActivity 的子类，则由内部自己创建独立的 CoroutineScope。
 */
open class HMBasePresenter<T : BaseContract.BaseView>(protected var mContext: Context, protected var mView: T)
    : BaseContract.BasePresenter, CoroutineScope by (mView as? CoroutineScope ?: MainScope()) {

    open fun onDestroy() {
        if (isActive) {
            cancel()
        }
    }

    /**
     * 处理http请求的返回结果，只有当正常返回 retCode = 0时，返回真实的数据结果，如果 retCode != 0，则会抛出 ApiException 异常
     */
    @Throws(Exception::class)
    fun <T> handleResponse(response: BaseResponse<T>): T? {
        if (response.errorCode == 0) {
            return response.data
        } else {
            when (response.errorCode) {
                HMConstants.ERR_CODE_TOKEN_OVERDUE -> {
                    mView.showTokenOverdue()
                    //发出事件通知
                    EventBus.getDefault().post(LogoutEvent())
                    throw AccountException(response.errorCode?.toString(), response.message)
                }
                HMConstants.ERR_CODE_KICK_OFFLINE -> {
                    mView.showKickOfflineDialog("账号登录异常", response.message)
                    EventBus.getDefault().post(LogoutEvent())
                    throw AccountException(response.errorCode?.toString(), response.message)
                }
                HMConstants.ERR_CODE_ACCOUNT_FREEZE -> {
                    mView.showAccountFreezeDialog("官方私信", response.message)
                    EventBus.getDefault().post(LogoutEvent())
                    throw AccountException(response.errorCode?.toString(), response.message)
                }
                HMConstants.ERR_CODE_ENCRYPT_NEED_UPDATE -> {
                    EventBus.getDefault().post(UpdateRsaKeyEvent())
                    throw ApiException(null, "网络异常，请稍后重试")
                }
            }
            throw ApiException(response.errorCode?.toString(), response.message)
        }
    }

    /**
     * 通用的异常业务处理方法
     *
     * @param e 异常
     * @param showCommError 是否toast通用的异常信息，例如网络异常等等
     * @param showBusinessError 是否toast业务异常信息，retCode !=0 时的业务异常
     */
    fun handleException(e: Exception, showCommError: Boolean = true, showBusinessError: Boolean = true) {
        if (e is AccountException) {
            //账号异常，不做任何处理，不 toast 错误信息
        } else if (e is ApiException) {
            val code = e.code
            val msg = e.message
            if (code.isNullOrEmpty()) {
                if (showCommError) {
                    mView.toastErrorMessage(msg)
                }
            } else {
                if (showBusinessError) {
                    mView.toastErrorMessage(msg)
                }
            }
        } else {
            if (e is CancellationException) {
                //由于协程取消时，可能会产生一个 kotlinx.coroutines.JobCancellationException 异常
                return
            }
            mView.toastErrorMessage("出现异常，请稍后重试")
        }
    }

    internal class AccountException constructor(code: String?, msg: String?) : ApiException(code, msg)
}


