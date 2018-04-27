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
         * 显示Toast信息
         *
         * @param resId 资源id
         */
        void toastMessage(int resId);

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
         * 显示用户未登录
         *
         * @param errMsg
         */
        void showUserNotLogin(String errMsg);
    }

    interface BasePresenter {

        void onDestroy();
    }

}