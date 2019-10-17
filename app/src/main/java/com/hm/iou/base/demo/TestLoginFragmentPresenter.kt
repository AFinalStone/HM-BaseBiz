package com.hm.iou.base.demo

import android.content.Context
import com.hm.iou.base.mvp.HMBaseFragmentPresenter
import kotlinx.coroutines.launch

class TestLoginFragmentPresenter(context: Context, view: TestLoginContract.View) : HMBaseFragmentPresenter<TestLoginContract.View>(context, view),
        TestLoginContract.Presenter {

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