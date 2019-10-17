package com.hm.iou.base.mvp

import android.view.View

/**
 * Created by hjy on 18/4/26.<br></br>
 */

interface BaseContract {

    interface BaseView {

        /**
         * 显示浮动加载进度条
         */
        fun showLoadingView()

        /**
         * 显示浮动加载进度条
         *
         * @param msg
         */
        fun showLoadingView(msg: String?)

        /**
         * 停止浮动加载进度条
         */
        fun dismissLoadingView()

        /**
         * 显示Toast信息
         *
         * @param msg
         */
        fun toastMessage(msg: String?)

        /**
         * 错误信息提示
         *
         * @param msg
         */
        fun toastErrorMessage(msg: String?)

        /**
         * 显示Toast信息
         *
         * @param resId 资源id
         */
        fun toastMessage(resId: Int)

        /**
         * 错误信息提示
         *
         * @param resId
         */
        fun toastErrorMessage(resId: Int)

        /**
         * 关闭当前页面
         */
        fun closeCurrPage()

        /**
         * 隐藏软键盘
         */
        fun hideSoftKeyboard()

        /**
         * 切换软键盘的状态
         */
        fun showSoftKeyboard()

        /**
         * 显示软键盘
         *
         * @param view
         */
        fun showSoftKeyboard(view: View)

        /**
         * 显示被踢下线对话框
         *
         * @param title  标题
         * @param errMsg 错误信息
         */
        fun showKickOfflineDialog(title: String?, errMsg: String?)

        /**
         * 显示账号被冻结
         *
         * @param title  标题
         * @param errMsg 错误信息
         */
        fun showAccountFreezeDialog(title: String?, errMsg: String?)

        fun showTokenOverdue()
    }

    interface BasePresenter

}