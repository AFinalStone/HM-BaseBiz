package com.hm.iou.base.utils;

import android.text.TextUtils;

import com.hm.iou.base.constants.HMConstants;
import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.exception.ApiException;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by hjy on 18/4/26.<br>
 */

public abstract class CommSubscriber<T> extends ResourceSubscriber<T> {

    private BaseContract.BaseView mView;

    public CommSubscriber(BaseContract.BaseView view) {
        mView = view;
    }

    /**
     * 是否显示通用的异常，例如：网络连接不上、无网络连接等等
     *
     * @return
     */
    public boolean isShowCommError() {
        return true;
    }

    /**
     * 是否显示业务异常，某些业务异常需要特殊处理时，需要重写该方法，返回false
     *
     * @return
     */
    public boolean isShowBusinessError() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable t) {
        if (mView == null)
            return;
        //RxJava2里map操作时不能return null值，这里做了个特殊处理
        if (t instanceof ResponseDataEmptyException) {
            handleResult(null);
            return;
        }
        //这里使用RXJava实现嵌套网络请求，请求结果不需要继续进行下去，这里只是为了中断整个嵌套链接
        if (t instanceof RxJavaStopException) {
            return;
        }

        String code = null;
        String errMsg;
        if (t instanceof ApiException) {
            ApiException apiException = (ApiException) t;
            code = apiException.getCode();
            if (("" + HMConstants.ERR_CODE_TOKEN_OVERDUE).equals(code)) {
                mView.showTokenOverdue();
                //发出事件通知
                EventBus.getDefault().post(new LogoutEvent());
                return;
            }
            if (("" + HMConstants.ERR_CODE_KICK_OFFLINE).equals(code)) {
                mView.showKickOfflineDialog("账号登录异常", apiException.getMessage());
                EventBus.getDefault().post(new LogoutEvent());
                return;
            }
            errMsg = t.getMessage();
        } else {
            errMsg = "出现异常，请稍后重试";
        }

        if (TextUtils.isEmpty(code)) {
            if (isShowCommError()) {
                mView.toastMessage(errMsg);
            }
        } else {
            //业务异常出现时
            if (isShowBusinessError()) {
                mView.toastMessage(errMsg);
            }
        }

        handleException(t, code, errMsg);
    }

    @Override
    public void onNext(T t) {
        handleResult(t);
    }

    /**
     * 请求成功返回
     *
     * @param t
     */
    public abstract void handleResult(T t);

    /**
     * 请求出现异常回调
     *
     * @param t    异常信息
     * @param code 错误码
     * @param msg  错误消息
     */
    public abstract void handleException(Throwable t, String code, String msg);
}