package com.hm.iou.base.demo

import com.hm.iou.network.HttpReqManager
import com.hm.iou.sharedata.model.BaseResponse
import com.hm.iou.sharedata.model.UserInfo
import com.hm.iou.tools.Md5Util

suspend fun mobileLogin(mobile: String, pwd: String): BaseResponse<UserInfo> {
    val reqBean = MobileLoginReqBean()
    reqBean.mobile = mobile
    reqBean.queryPswd = Md5Util.getMd5ByString(pwd)
    return HttpReqManager.getInstance().getService(LoginService::class.java).testMobileLogin(reqBean)
}