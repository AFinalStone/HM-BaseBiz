package com.hm.iou.base.demo;

import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;

/**
 * Created by hjy on 18/4/26.<br>
 */

public class TestPresenter extends MvpActivityPresenter<TestContract.View> implements TestContract.Presenter {

    public TestPresenter(@NonNull TestContract.View view) {
        super(view);
    }
}
