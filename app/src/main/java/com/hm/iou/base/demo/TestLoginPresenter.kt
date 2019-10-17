package com.hm.iou.base.demo

import android.content.Context
import com.hm.iou.base.mvp.HMBasePresenter
import kotlinx.coroutines.launch

class TestLoginPresenter(context: Context, view: TestLoginContract.View) :
        HMBasePresenter<TestLoginContract.View>(context, view), TestLoginContract.Presenter {

    override fun login(moible: String, pwd: String) {
        launch {
            try {
                val response = mobileLogin(moible, pwd)
                val userInfo = handleResponse(response)
                mView.toastMessage(userInfo?.mobile)
            } catch (e: Exception) {
                e.printStackTrace()
                handleException(e)
            }
        }
    }
}