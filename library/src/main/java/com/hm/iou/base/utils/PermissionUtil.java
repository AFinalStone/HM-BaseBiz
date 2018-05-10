package com.hm.iou.base.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.hm.iou.base.R;
import com.hm.iou.uikit.dialog.IOSAlertDialog;

/**
 * Created by hjy on 18/5/9.<br>
 */

public class PermissionUtil {

    /**
     * 显示需要设置相机权限的dialog
     *
     * @param activity
     */
    public static void showCameraPermissionDialog(Activity activity) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_camera_permission_req_msg));
    }

    /**
     * 显示需要存储权限的dialog
     *
     * @param activity
     */
    public static void showStoragePermissionDialog(Activity activity) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_read_storage_permission_req_msg));
    }

    /**
     * 显示需要位置权限的dialog
     *
     * @param activity
     */
    public static void showLocationPermissionDialog(Activity activity) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_location_permission_req_msg));

    }

    /**
     * 显示权限申请设置对话框，点击去设置后跳转到应用的设置界面
     *
     * @param activity
     */
    public static void showPermissionReqDialog(Activity activity, String msg) {
        new IOSAlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.base_permission_req))
                .setMessage(msg)
                .setPositiveButton(activity.getString(R.string.base_go_setting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toPermissionSetting(activity);
                    }
                })
                .setNegativeButton(activity.getString(R.string.base_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 跳转到权限设置
     *
     * @param activity
     */
    public static void toPermissionSetting(Activity activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            toSystemConfig(activity);
        } else {
            try {
                toApplicationInfo(activity);
            } catch (Exception e) {
                e.printStackTrace();
                toSystemConfig(activity);
            }
        }
    }

    /**
     * 应用信息界面
     *
     * @param activity
     */
    public static void toApplicationInfo(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        activity.startActivity(localIntent);
    }

    /**
     * 系统设置界面
     *
     * @param activity
     */
    public static void toSystemConfig(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
