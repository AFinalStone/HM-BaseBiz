package com.hm.iou.base.photo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.hm.iou.base.R;
import com.hm.iou.base.file.FileUtil;
import com.hm.iou.base.utils.PermissionUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.dialog.HMActionSheetDialog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class PhotoUtil {

    public interface OnPhotoCancelListener {
        //取消选择
        void onCancel();
    }

    private static String FILE_PROVIDER_SUFFIX = ".fileprovider";

    /**
     * 临时存储拍照后的图片路径
     */
    public static File CACHE_FILE_BY_CAMERA;

    private static boolean HAS_PERMISSION_CAMERA;
    private static boolean HAS_PERMISSION_STORAGE;

    private static boolean TMP_SELECT_PHOTO_FLAG;

    public static void showSelectDialog(final Activity activity, final int cameraReqCode, final int albumReqCode) {
        showSelectDialog(activity, cameraReqCode, albumReqCode, null);
    }

    /**
     * 显示选择相机、相册选择对话框
     *
     * @param activity
     * @param cameraReqCode 打开相机的请求码
     * @param albumReqCode  打开相册的请求码
     */
    public static void showSelectDialog(final Activity activity, final int cameraReqCode, final int albumReqCode, final OnPhotoCancelListener listener) {
        TMP_SELECT_PHOTO_FLAG = false;
        List<String> actionList = new ArrayList<>();
        actionList.add("拍照");
        actionList.add("从相册中选择");
        Dialog dialog = new HMActionSheetDialog.Builder(activity)
                .setTitle("")
                .setActionSheetList(actionList)
                .setCanSelected(false)
                .setOnItemClickListener(new HMActionSheetDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i, String s) {
                        if (i == 0) {
                            TMP_SELECT_PHOTO_FLAG = true;
                            if (PermissionUtil.isPermissionGranted(activity, Manifest.permission.CAMERA)) {
                                openCamera(activity, cameraReqCode, listener);
                            } else {
                                PermissionUtil.showCameraRemindDialog(activity, new PermissionUtil.OnPermissionDialogClick() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        openCamera(activity, cameraReqCode, listener);
                                    }

                                    @Override
                                    public void onNegativeBtnClick() {
                                        if (listener != null) {
                                            listener.onCancel();
                                        }
                                    }
                                });
                            }
                        } else if (i == 1) {
                            TMP_SELECT_PHOTO_FLAG = true;
                            if (PermissionUtil.isPermissionGranted(activity, READ_EXTERNAL_STORAGE)) {
                                openAlbum(activity, albumReqCode, listener);
                            } else {
                                PermissionUtil.showStorageRemindDialog(activity, new PermissionUtil.OnPermissionDialogClick() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        openAlbum(activity, albumReqCode, listener);
                                    }

                                    @Override
                                    public void onNegativeBtnClick() {
                                        if (listener != null) {
                                            listener.onCancel();
                                        }
                                    }
                                });
                            }
                        }
                    }
                })
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!TMP_SELECT_PHOTO_FLAG && listener != null) {
                    listener.onCancel();
                }
            }
        });
        dialog.show();
    }

    public static void showSelectDialog(final Fragment fragment, final int cameraReqCode, final int albumReqCode) {
        showSelectDialog(fragment, cameraReqCode, albumReqCode, null);
    }

    /**
     * 显示选择相机、相册对话框
     *
     * @param fragment
     * @param cameraReqCode 打开相机的请求码
     * @param albumReqCode  打开相册的请求码
     */
    public static void showSelectDialog(final Fragment fragment, final int cameraReqCode, final int albumReqCode, final OnPhotoCancelListener listener) {
        TMP_SELECT_PHOTO_FLAG = false;
        List<String> actionList = new ArrayList<>();
        actionList.add("拍照");
        actionList.add("从相册中选择");
        Dialog dialog = new HMActionSheetDialog.Builder(fragment.getActivity())
                .setTitle("")
                .setActionSheetList(actionList)
                .setCanSelected(false)
                .setOnItemClickListener(new HMActionSheetDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i, String s) {
                        if (i == 0) {
                            TMP_SELECT_PHOTO_FLAG = true;
                            if (PermissionUtil.isPermissionGranted(fragment.getActivity(), Manifest.permission.CAMERA)) {
                                openCamera(fragment, cameraReqCode, listener);
                            } else {
                                PermissionUtil.showCameraRemindDialog(fragment.getActivity(), new PermissionUtil.OnPermissionDialogClick() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        openCamera(fragment, cameraReqCode, listener);
                                    }

                                    @Override
                                    public void onNegativeBtnClick() {
                                        if (listener != null) {
                                            listener.onCancel();
                                        }
                                    }
                                });
                            }
                        } else if (i == 1) {
                            TMP_SELECT_PHOTO_FLAG = true;
                            if (PermissionUtil.isPermissionGranted(fragment.getActivity(), READ_EXTERNAL_STORAGE)) {
                                openAlbum(fragment, albumReqCode, listener);
                            } else {
                                PermissionUtil.showStorageRemindDialog(fragment.getActivity(), new PermissionUtil.OnPermissionDialogClick() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        openAlbum(fragment, albumReqCode, listener);
                                    }

                                    @Override
                                    public void onNegativeBtnClick() {
                                        if (listener != null) {
                                            listener.onCancel();
                                        }
                                    }
                                });
                            }
                        }
                    }
                })
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!TMP_SELECT_PHOTO_FLAG && listener != null) {
                    listener.onCancel();
                }
            }
        });
        dialog.show();
    }

    public static void openCamera(final Activity activity, final int requestCode) {
        openCamera(activity, requestCode, null);
    }

    /**
     * 打开系统相机进行拍照
     *
     * @param activity    当前activity
     * @param requestCode 调用系统相机请求码
     */
    public static void openCamera(final Activity activity, final int requestCode, final OnPhotoCancelListener listener) {
        HAS_PERMISSION_CAMERA = false;
        HAS_PERMISSION_STORAGE = false;
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.requestEach(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            if (Manifest.permission.CAMERA.equals(permission.name)) {
                                HAS_PERMISSION_CAMERA = true;
                            } else {
                                HAS_PERMISSION_STORAGE = true;
                            }
                            if (HAS_PERMISSION_CAMERA && HAS_PERMISSION_STORAGE) {
                                //拍照后照片存储路径
                                CACHE_FILE_BY_CAMERA = new File(FileUtil.getExternalCacheDirPath(activity) + "/photo.jpg");
                                Uri imageUri = Uri.fromFile(CACHE_FILE_BY_CAMERA);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    String fileProvider = activity.getPackageName() + FILE_PROVIDER_SUFFIX;
                                    imageUri = FileProvider.getUriForFile(activity, fileProvider, CACHE_FILE_BY_CAMERA);//通过FileProvider创建一个content类型的Uri
                                }
                                //调用系统相机
                                Intent intentCamera = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                                    intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }
                                intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                //将拍照结果保存至photo_file的Uri中，不保留在相册中
                                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                try {
                                    activity.startActivityForResult(intentCamera, requestCode);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (listener != null) {
                                        listener.onCancel();
                                    }
                                    ToastUtil.showMessage(activity, activity.getString(R.string.base_open_camera_fail));
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.onCancel();
                            }
                            if (Manifest.permission.CAMERA.equals(permission.name)) {
                                PermissionUtil.showCameraPermissionDialog(activity);
                            } else {
                                PermissionUtil.showStoragePermissionDialog(activity);
                            }
                        }
                    }
                });
    }

    public static void openCamera(final Fragment fragment, final int requestCode) {
        openCamera(fragment, requestCode, null);
    }

    /**
     * 打开系统相机进行拍照
     *
     * @param fragment    当前Fragment
     * @param requestCode 调用系统相机请求码
     */
    public static void openCamera(final Fragment fragment, final int requestCode, final OnPhotoCancelListener listener) {
        final Activity activity = fragment.getActivity();
        RxPermissions rxPermissions = new RxPermissions(fragment.getActivity());
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //拍照后照片存储路径
                    CACHE_FILE_BY_CAMERA = new File(FileUtil.getExternalCacheDirPath(fragment.getContext()) + "/photo.jpg");
                    Uri imageUri = Uri.fromFile(CACHE_FILE_BY_CAMERA);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        String fileProvider = activity.getPackageName() + FILE_PROVIDER_SUFFIX;
                        imageUri = FileProvider.getUriForFile(fragment.getActivity(), fileProvider, CACHE_FILE_BY_CAMERA);//通过FileProvider创建一个content类型的Uri
                    }
                    //调用系统相机
                    Intent intentCamera = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //添加这一句表示对目标应用临时授权该Uri所代表的文件
                        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    //将拍照结果保存至photo_file的Uri中，不保留在相册中
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    try {
                        fragment.startActivityForResult(intentCamera, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (listener != null) {
                            listener.onCancel();
                        }
                        ToastUtil.showMessage(activity, activity.getString(R.string.base_open_camera_fail));
                    }
                } else {
                    if (listener != null) {
                        listener.onCancel();
                    }
                    PermissionUtil.showCameraPermissionDialog(activity);
                }
            }
        });
    }


    /**
     * 获取当前拍照的图片路径
     *
     * @return 没有获取到则返回null
     */
    public static String getCameraPhotoPath() {
        if (CACHE_FILE_BY_CAMERA != null) {
            return CACHE_FILE_BY_CAMERA.getAbsolutePath();
        }
        return null;
    }

    public static void openAlbum(final Activity activity, final int requestCode) {
        openAlbum(activity, requestCode, null);
    }


    /**
     * 打开系统相册获取图片
     *
     * @param activity    当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openAlbum(final Activity activity, final int requestCode, final OnPhotoCancelListener listener) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   if (aBoolean) {
                                       Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                       photoPickerIntent.setType("image/*");
                                       activity.startActivityForResult(photoPickerIntent, requestCode);
                                   } else {
                                       if (listener != null) {
                                           listener.onCancel();
                                       }
                                       PermissionUtil.showStoragePermissionDialog(activity);
                                   }
                               }
                           }
                );
    }

    public static void openAlbum(final Fragment fragment, final int requestCode) {
        openAlbum(fragment, requestCode, null);
    }

    /**
     * 打开系统相册获取图片
     *
     * @param fragment    当前fragment
     * @param requestCode 打开相册的请求码
     */
    public static void openAlbum(final Fragment fragment, final int requestCode, final OnPhotoCancelListener listener) {
        final Activity activity = fragment.getActivity();
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(
                READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   if (aBoolean) {
                                       Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                       photoPickerIntent.setType("image/*");
                                       fragment.startActivityForResult(photoPickerIntent, requestCode);
                                   } else {
                                       if (listener != null) {
                                           listener.onCancel();
                                       }
                                       PermissionUtil.showStoragePermissionDialog(activity);
                                   }
                               }
                           }
                );
    }

    /**
     * 调用系统裁减工具
     *
     * @param activity    当前activity
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Activity activity, Uri orgUri, Uri desUri
            , int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 调用系统裁减图片工具
     *
     * @param fragment    当前activity
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Fragment fragment, Uri orgUri, Uri desUri
            , int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 读取Uri所在的图片文件路径
     *
     * @param context 上下文对象
     * @param uri     图片Uri地址
     * @return 图片文件路径
     */
    public static String getPath(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
