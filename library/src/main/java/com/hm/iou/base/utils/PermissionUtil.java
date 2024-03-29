package com.hm.iou.base.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.hm.iou.base.R;
import com.hm.iou.uikit.dialog.HMAlertDialog;

/**
 * Created by hjy on 18/5/9.<br>
 */

public class PermissionUtil {

    public interface OnPermissionDialogClick {

        void onPositiveBtnClick();

        void onNegativeBtnClick();
    }

    /**
     * 显示需要设置相机权限的dialog
     *
     * @param activity
     */
    public static void showCameraPermissionDialog(Activity activity) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_camera_permission_req_msg), null);
    }

    /**
     * 显示需要设置相机权限的dialog，以及回调接口
     *
     * @param activity
     * @param onPermissionDialogClick
     */
    public static void showCameraPermissionDialog(Activity activity, OnPermissionDialogClick onPermissionDialogClick) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_camera_permission_req_msg), onPermissionDialogClick);
    }

    /**
     * 显示需要存储权限的dialog
     *
     * @param activity
     */
    public static void showStoragePermissionDialog(Activity activity) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_read_storage_permission_req_msg), null);
    }

    /**
     * 显示需要存储权限的dialog
     *
     * @param activity
     * @param onPermissionDialogClick
     */
    public static void showStoragePermissionDialog(Activity activity, OnPermissionDialogClick onPermissionDialogClick) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_read_storage_permission_req_msg), onPermissionDialogClick);
    }

    /**
     * 显示需要位置权限的dialog
     *
     * @param activity
     */
    public static void showLocationPermissionDialog(Activity activity) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_location_permission_req_msg), null);

    }

    /**
     * 显示需要位置权限的dialog
     *
     * @param activity
     * @param onPermissionDialogClick
     */
    public static void showLocationPermissionDialog(Activity activity, OnPermissionDialogClick onPermissionDialogClick) {
        showPermissionReqDialog(activity, activity.getString(R.string.base_location_permission_req_msg), onPermissionDialogClick);

    }

    /**
     * 显示权限申请设置对话框，点击去设置后跳转到应用的设置界面
     *
     * @param activity
     */
    public static void showPermissionReqDialog(final Activity activity, String msg, final OnPermissionDialogClick listener) {
        new HMAlertDialog.Builder(activity)
                .setTitle(R.string.base_permission_req)
                .setMessage(msg)
                .setPositiveButton(R.string.base_go_setting)
                .setNegativeButton(R.string.base_cancel)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        if (listener != null) {
                            listener.onPositiveBtnClick();
                        }
                        toPermissionSetting(activity);
                    }

                    @Override
                    public void onNegClick() {
                        if (listener != null) {
                            listener.onNegativeBtnClick();
                        }
                    }
                })
                .create()
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
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
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

    public static boolean isPermissionGranted(Context activity, String permission) {
        boolean granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        if (granted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PermissionUtil.hasSelfPermissionForXiaomi(activity, permission);
        }
        return granted;
    }

    /**
     * 在申请权限之前，显示权限提醒对话框
     *
     * @param context
     * @param title    标题
     * @param msg      描述内容
     * @param listener
     */
    public static void showPermissionRemindDialog(Context context, String title, String msg, final OnPermissionDialogClick listener) {
        new HMAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.base_permission_allowed)
                .setNegativeButton(R.string.base_permission_not_allowed)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        if (listener != null) {
                            listener.onPositiveBtnClick();
                        }
                    }

                    @Override
                    public void onNegClick() {
                        if (listener != null) {
                            listener.onNegativeBtnClick();
                        }
                    }
                })
                .create()
                .show();
    }

    public static void showLocationRemindDialog(Context context, OnPermissionDialogClick listener) {
        showPermissionRemindDialog(context, "开启位置权限",
                "我们需要获得该权限，才能为您提供省市头条信息及附近律师。", listener);
    }

    public static void showCalendarRemindDialog(Context context, OnPermissionDialogClick listener) {
        showPermissionRemindDialog(context, "开启日历权限",
                "我们需要获得该权限，才能为您提供智能日期提醒服务。", listener);
    }

    public static void showCameraRemindDialog(final Context context, final OnPermissionDialogClick listener) {
        showPermissionRemindDialog(context, "开启摄像权限",
                "我们需要获得该权限，才能为您提供拍摄照片服务及扫一扫功能。", new OnPermissionDialogClick() {
                    @Override
                    public void onPositiveBtnClick() {
                        TraceUtil.onEvent(context, "perm_camera_allow");
                        if (listener != null)
                            listener.onPositiveBtnClick();
                    }

                    @Override
                    public void onNegativeBtnClick() {
                        TraceUtil.onEvent(context, "perm_camera_disallow");
                        if (listener != null)
                            listener.onNegativeBtnClick();
                    }
                });
    }

    public static void showStorageRemindDialog(final Context context, final OnPermissionDialogClick listener) {
        showPermissionRemindDialog(context, "开启读写手机存储权限",
                "我们需要获得该权限，才能为您提供从相册选取照片、拍摄照片及下载分享等功能。", new OnPermissionDialogClick() {
                    @Override
                    public void onPositiveBtnClick() {
                        TraceUtil.onEvent(context, "perm_album_allow");
                        if (listener != null)
                            listener.onPositiveBtnClick();
                    }

                    @Override
                    public void onNegativeBtnClick() {
                        TraceUtil.onEvent(context, "perm_album_disallow");
                        if (listener != null)
                            listener.onNegativeBtnClick();
                    }
                });
    }


    /**
     * 在部分小米手机上发现它修改过底层权限请求系统，必须采用该方法再做一次判断
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasSelfPermissionForXiaomi(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                String op = AppOpsManager.permissionToOp(permission);
                if (!TextUtils.isEmpty(op)) {
                    int checkOp = appOpsManager.checkOp(op, Process.myUid(), context.getPackageName());
                    return checkOp == AppOpsManager.MODE_ALLOWED && ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}