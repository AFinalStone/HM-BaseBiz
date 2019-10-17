package com.hm.iou.base.demo

import android.content.Context
import com.hm.iou.base.file.FileApi
import com.hm.iou.base.file.FileBizType
import com.hm.iou.base.mvp.HMBasePresenter
import com.hm.iou.network.HttpReqManager
import com.hm.iou.network.exception.ApiException
import com.hm.iou.sharedata.UserManager
import com.hm.iou.sharedata.model.BaseResponse
import com.hm.iou.sharedata.model.UserInfo
import com.hm.iou.tools.Md5Util
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TestLoginPresenter(context: Context, view: TestLoginContract.View) :
        HMBasePresenter<TestLoginContract.View>(context, view), TestLoginContract.Presenter {

    override fun login(moible: String, pwd: String) {
        launch {
            try {
                val response = mobileLogin(moible, pwd)
                val userInfo = handleResponse(response)
                userInfo?.let {
                    HttpReqManager.getInstance().setUserId(userInfo.userId)
                    HttpReqManager.getInstance().setToken(userInfo.token)
                    UserManager.getInstance(mContext).updateOrSaveUserInfo(userInfo)
                }
                mView.toastMessage(userInfo?.mobile)
            } catch (e: Exception) {
                e.printStackTrace()
                handleException(e)
            }
        }
    }

    override fun testUploadImage(file: String) {
        launch {
            mView.showLoadingView()
            try {
                println("TEST: 开始上传")
                val response = FileApi.uploadImageByCoroutine(File(file), FileBizType.Avatar)
                val result = handleResponse(response)
                println("TEST: 上传成功 ${result?.fileId}, ${result?.fileUrl}")
                mView.toastMessage("图片上传成功...")
            } catch (e: Exception) {
                e.printStackTrace()
                handleException(e)
            } finally {
                mView.dismissLoadingView()
            }
        }
    }

    override fun testAsycMethod() {
        launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    loginAsync()
                }
                val result = handleResponse(response)
                mView.toastMessage("测试登录成功: ${result?.userId}")
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    //将普通异步方法改造成支持协程的 suspend 函数
    suspend fun loginAsync(): BaseResponse<UserInfo> = suspendCancellableCoroutine { cancellableContinuation ->
        val reqBean = MobileLoginReqBean()
        reqBean.mobile = "15967132742"
        reqBean.queryPswd = Md5Util.getMd5ByString("123456")
        val call = HttpReqManager.getInstance().getService(LoginService::class.java).mobileLoginEx(reqBean)
        cancellableContinuation.invokeOnCancellation {
            //如果协程取消，同时要取消请求
            println("coroutine: async call is cancelled...")
            call.cancel()
        }
        call.enqueue(object : Callback<BaseResponse<UserInfo>> {
            override fun onFailure(call: Call<BaseResponse<UserInfo>>, t: Throwable) {
                cancellableContinuation.resumeWithException(ApiException("网络异常，请重试"))
            }

            override fun onResponse(call: Call<BaseResponse<UserInfo>>, response: Response<BaseResponse<UserInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    cancellableContinuation.resume(response.body()!!)
                } else {
                    cancellableContinuation.resumeWithException(ApiException("网络异常，请重试"))
                }
            }
        })
    }

    //测试间隔 4 秒 toast
    override fun testDelay() {
        launch {
            //在协程内需要用 isActivity 来作为退出循环的标记
            while (isActive) {
                delay(4000)
                mView.toastMessage("延迟4秒后显示")
            }
        }
    }
}