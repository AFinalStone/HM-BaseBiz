package com.hm.iou.base.mvp;

/**
 * Created by hjy on 18/4/26.<br>
 */

public interface BaseContract {

    interface BaseView {

        /**
         * 显示浮动加载进度条
         */
        void showLoadingView();

        /**
         * 显示浮动加载进度条
         *
         * @param msg
         */
        void showLoadingView(String msg);

        /**
         * 停止浮动加载进度条
         */
        void dismissLoadingView();

        /**
         * 显示Toast信息
         *
         * @param msg
         */
        void toastMessage(String msg);

        /**
         * 错误信息提示
         *
         * @param msg
         */
        void toastErrorMessage(String msg);

        /**
         * 显示Toast信息
         *
         * @param resId 资源id
         */
        void toastMessage(int resId);

        /**
         * 错误信息提示
         *
         * @param resId
         */
        void toastErrorMessage(int resId);

        /**
         * 关闭当前页面
         */
        void closeCurrPage();

        /**
         * 隐藏软键盘
         */
        void hideSoftKeyboard();

        /**
         * 显示软键盘
         */
        void showSoftKeyboard();

        /**
         * 显示被踢下线对话框
         *
         * @param title  标题
         * @param errMsg 错误信息
         */
        void showKickOfflineDialog(String title, String errMsg);

        /**
         * 显示账号被冻结
         *
         * @param title  标题
         * @param errMsg 错误信息
         */
        void showAccountFreezeDialog(String title, String errMsg);

        void showTokenOverdue();
    }

    interface BasePresenter {

    }

}