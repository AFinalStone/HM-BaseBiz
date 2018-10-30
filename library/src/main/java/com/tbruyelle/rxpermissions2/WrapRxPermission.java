package com.tbruyelle.rxpermissions2;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.hm.iou.base.utils.PermissionUtil;


/**
 * Created by hjy on 2018/10/30.
 *
 * 主要是为了解决部分手机权限判断不准的问题，发现过小米手机上有该问题
 */
public class WrapRxPermission extends RxPermissions {

    private Context mContext;

    public WrapRxPermission(@NonNull Activity activity) {
        super(activity);
        mContext = activity.getApplicationContext();
    }

    @Override
    public boolean isGranted(String permission) {
        boolean isGranted = !isMarshmallow() || mRxPermissionsFragment.isGranted(permission);
        if (isGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return PermissionUtil.hasSelfPermissionForXiaomi(mContext, permission);
            } else {
                //6.0以下，部分手机自己也会加上权限判断 TODO 6.0以下系统，小米手机之类的权限并没有合适的方法来判断
            }
        }
        return isGranted;
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRevoked(String permission) {
        return isMarshmallow() && mRxPermissionsFragment.isRevoked(permission);
    }

}
