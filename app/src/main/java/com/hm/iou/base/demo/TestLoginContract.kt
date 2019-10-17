package com.hm.iou.base.demo

import com.hm.iou.base.mvp.BaseContract

interface TestLoginContract {

    interface View: BaseContract.BaseView {

    }

    interface Presenter: BaseContract.BasePresenter {

        fun login(moible: String, pwd: String)
    }


}