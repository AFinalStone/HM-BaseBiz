package com.hm.iou.base.mvp

import android.content.Context
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

/**
 * Created by hjy on 19/10/17.<br></br>
 *
 * 构造函数包含2个参数，1：context；2：view，通常是HMBaseFragment的子类
 *
 * 默认情况下每个 Presenter 都实现了 CoroutineScope 接口，一般会与 view 实例对象共享同一个 CoroutineScope，
 * 如果 view 不是 HMBaseFragment 的子类，则由内部自己创建独立的 CoroutineScope。
 */
open class HMBaseFragmentPresenter<T : BaseContract.BaseView>(context: Context, view: T)
    : HMBasePresenter<T>(context, view) {

    open fun onViewCreated() {}

    open fun onDestroyView() {
        if (isActive) {
            cancel()
        }
    }

    override fun onDestroy() {
    }

}


