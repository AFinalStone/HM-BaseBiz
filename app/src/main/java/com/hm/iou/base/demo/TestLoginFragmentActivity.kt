package com.hm.iou.base.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class TestLoginFragmentActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        supportFragmentManager.beginTransaction().replace(R.id.container, TestLoginFragment()).commit()
    }

}