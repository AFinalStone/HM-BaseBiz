package com.hm.iou.base.demo

import android.os.Bundle
import com.hm.iou.base.mvp.HMBaseFragment
import com.hm.iou.tools.kt.clickWithDuration
import kotlinx.android.synthetic.main.activity_test_login.*

class TestLoginFragment : HMBaseFragment<TestLoginFragmentPresenter>(), TestLoginContract.View {

    override fun getLayoutId() = R.layout.activity_test_login

    override fun initPresenter(): TestLoginFragmentPresenter = TestLoginFragmentPresenter(activity!!, this)

    override fun initEventAndData(savedInstanceState: Bundle?) {
        btn_test.clickWithDuration {
            mPresenter.login("15967132742", "123456")
        }
    }


}