package com.hm.iou.base.demo

import android.content.Intent
import android.os.Bundle
import com.hm.iou.base.mvp.HMBaseActivity
import com.hm.iou.base.photo.PhotoUtil
import com.hm.iou.tools.kt.clickWithDuration
import kotlinx.android.synthetic.main.activity_test_login.*

class TestLoginActivity : HMBaseActivity<TestLoginPresenter>(), TestLoginContract.View {

    override fun getLayoutId() = R.layout.activity_test_login

    override fun initPresenter(): TestLoginPresenter = TestLoginPresenter(this, this)

    override fun initEventAndData(savedInstanceState: Bundle?) {
        btn_test.clickWithDuration {
            mPresenter.login("15967132742", "123456")
        }
        btn_test_file.clickWithDuration {
            PhotoUtil.openCamera(this@TestLoginActivity, 100)
        }
        btn_test_async.clickWithDuration {
            mPresenter.testAsycMethod()
        }
        btn_test_delay.clickWithDuration {
            mPresenter.testDelay()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val path = PhotoUtil.getCameraPhotoPath()
            println("TEST: $path")
            path?.let {
                mPresenter.testUploadImage(it)
            }
        }
    }

}