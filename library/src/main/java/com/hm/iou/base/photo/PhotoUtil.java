package com.hm.iou.base.photo;

import android.Manifest;
import android.app.Activity;
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
import com.hm.iou.base.utils.PermissionUtil;
import com.hm.iou.tools.FileUtil;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.dialog.IOSActionSheetItem;
import com.hm.iou.uikit.dialog.IOSActionSheetTitleDialog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;

public class PhotoUtil {

    private static String FILE_PROVIDER_SUFFIX = ".fileprovider";

    /**
     * 临时存储拍照后的图片路径
     */
    public static File CACHE_FILE_BY_CAMERA;

    private static boolean HAS_PERMISSION_CAMERA;
    private static boolean HAS_PERMISSION_STORAGE;

    /**
     * 显示选择相机、相册选择对话框
     *
     * @param activity
     * @param cameraReqCode 打开相机的请求码
     * @param albumReqCode  打开相册的请求码
     */
    public static void showSelectDialog(final Activity activity, final int cameraReqCode, final int albumReqCode) {
        new IOSActionSheetTitleDialog.Builder(activity)
                .addSheetItem(IOSActionSheetItem.create("打开相机").setItemClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openCamera(activity, cameraReqCode);
                    }
                }))
                .addSheetItem(IOSActionSheetItem.create("打开相册").setItemClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openAlbum(activity, albumReqCode);
                    }
                }))
                .show();
    }

    /**
     * 显示选择相机、相册对话框
     *
     * @param fragment
     * @param cameraReqCode 打开相机的请求码
     * @param albumReqCode  打开相册的请求码
     */
    public static void showSelectDialog(final Fragment fragment, final int cameraReqCode, final int albumReqCode) {
        new IOSActionSheetTitleDialog.Builder(fragment.getActivity())
                .addSheetItem(IOSActionSheetItem.create("打开相机").setItemClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openCamera(fragment, cameraReqCode);
                    }
                }))
                .addSheetItem(IOSActionSheetItem.create("打开相册").setItemClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openAlbum(fragment, albumReqCode);
                    }
                }))
                .show();
    }

    /**
     * 打开系统相机进行拍照
     *
     * @param activity    当前activity
     * @param requestCode 调用系统相机请求码
     */
    public static void openCamera(final Activity activity, final int requestCode) {
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
                                CACHE_FILE_BY_CAMERA = new File(FileUtil.getExternalCacheDirPath(activity) + "/photo.png");
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
                                    ToastUtil.showMessage(activity, activity.getString(R.string.base_open_camera_fail));
                                }
                            }
                        } else {
                            if (Manifest.permission.CAMERA.equals(permission.name)) {
                                PermissionUtil.showCameraPermissionDialog(activity);
                            } else {
                                PermissionUtil.showStoragePermissionDialog(activity);
                            }
                        }
                    }
                });
    }

    /**
     * 打开系统相机进行拍照
     *
     * @param fragment    当前Fragment
     * @param requestCode 调用系统相机请求码
     */
    public static void openCamera(final Fragment fragment, final int requestCode) {
        final Activity activity = fragment.getActivity();
        RxPermissions rxPermissions = new RxPermissions(fragment.getActivity());
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //拍照后照片存储路径
                    CACHE_FILE_BY_CAMERA = new File(FileUtil.getExternalCacheDirPath(fragment.getContext()) + "/photo.png");
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
                        ToastUtil.showMessage(activity, activity.getString(R.string.base_open_camera_fail));
                    }
                } else {
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

    /**
     * 打开系统相册获取图片
     *
     * @param activity    当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openAlbum(final Activity activity, final int requestCode) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   if (aBoolean) {
                                       Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                       photoPickerIntent.setType("image/*");
                                       activity.startActivityForResult(photoPickerIntent, requestCode);
                                   } else {
                                       PermissionUtil.showStoragePermissionDialog(activity);
                                   }
                               }
                           }
                );
    }


    /**
     * 打开系统相册获取图片
     *
     * @param fragment    当前fragment
     * @param requestCode 打开相册的请求码
     */
    public static void openAlbum(final Fragment fragment, final int requestCode) {
        final Activity activity = fragment.getActivity();
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   if (aBoolean) {
                                       Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                       photoPickerIntent.setType("image/*");
                                       fragment.startActivityForResult(photoPickerIntent, requestCode);
                                   } else {
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
