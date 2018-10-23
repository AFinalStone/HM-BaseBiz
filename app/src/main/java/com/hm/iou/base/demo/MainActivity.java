package com.hm.iou.base.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hm.iou.base.file.FileApi;
import com.hm.iou.base.file.FileUploadResult;
import com.hm.iou.base.photo.CompressPictureUtil;
import com.hm.iou.base.photo.ImageCropper;
import com.hm.iou.base.photo.PhotoUtil;
import com.hm.iou.base.photo.SelectPicDialog;
import com.hm.iou.base.utils.InstallUtil;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.base.webview.BaseWebviewActivity;
import com.hm.iou.logger.Logger;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.tools.DensityUtil;
import com.hm.iou.tools.SystemUtil;
import com.hm.iou.tools.ToastUtil;
import com.sina.weibo.sdk.utils.MD5;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    ImageView mIvPhoto;
    ImageCropper mImageCropper;
    private EditText mEtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaseWebviewActivity.class);
                intent.putExtra("url", "https://www.54jietiao.com");
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_testStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestStatusActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_tes2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.openCamera(MainActivity.this, 100);
            }
        });

        findViewById(R.id.btn_tes3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.openAlbum(MainActivity.this, 101);
            }
        });

        mIvPhoto = findViewById(R.id.iv_photo);

        findViewById(R.id.btn_tes4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.showSelectDialog(MainActivity.this, 100, 101);
            }
        });
        findViewById(R.id.btn_tes5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPicDialog.createDialog(MainActivity.this, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1530164549688&di=1c59fb642db7d4279efd9eeaaea62765&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F8b82b9014a90f60314b73db13312b31bb151edc5.jpg", new SelectPicDialog.OnSelectListener() {
                    @Override
                    public void onDelete() {

                    }

                    @Override
                    public void onReSelect() {

                    }
                }).show();
            }
        });

        findViewById(R.id.check_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = SystemUtil.getDownloadFilePath("条管家.apk");
                InstallUtil.installNormal(MainActivity.this, path);
            }
        });
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.openAlbum(MainActivity.this, 102);
            }
        });

        mEtUrl = findViewById(R.id.et_h5Url);
        findViewById(R.id.btn_loadH5Url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtUrl.length() != 0) {
                    Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                            .withString("url", mEtUrl.getText().toString())
                            .withString("showtitlebar", "false")
                            .navigation(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Photo", "onActivityResult:" + requestCode + "," + resultCode);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Log.d("Photo", "拍照返回结果....");
                String path = PhotoUtil.getCameraPhotoPath();

/*                Log.d("Photo", "camera path: " + path);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Log.d("Photo", "bitmap is " + (bitmap == null ? "null" : " exists"));
                mIvPhoto.setImageBitmap(bitmap);*/

                initImageCropper();
                mImageCropper.crop(path, 150, 100, true, "crop");
            }
        } else if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Log.d("Photo", "相册图片返回结果....");
                String path = PhotoUtil.getPath(this, data.getData());
                Log.d("Photo", "album path: " + path);
/*                Bitmap bitmap = BitmapFactory.decodeFile(path);
                mIvPhoto.setImageBitmap(bitmap);*/

                initImageCropper();
                mImageCropper.crop(path, 150, 100, false, "crop");
            }
        } else if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                Log.d("Photo", "相册图片返回结果....");
                String path = PhotoUtil.getPath(this, data.getData());
                Log.d("Photo", "album path: " + path);
                compressPic(path);

            }
        }
    }

    /**
     * 压缩图片
     *
     * @param fileUrl
     */
    private void compressPic(final String fileUrl) {
        CompressPictureUtil.compressPic(this, fileUrl, new CompressPictureUtil.OnCompressListener() {
            @Override
            public void onCompressPicSuccess(File file) {
                getBitmapInfo(file.getPath());
                Logger.d("图片压缩成功....");
                Map<String, Object> map = new HashMap<>();
                map.put("bizType", 27);//记债本业务
                map.put("fileType", 1);
                FileApi.upload(file, map)
                        .map(RxUtil.<FileUploadResult>handleResponse())
                        .subscribe(new Consumer<FileUploadResult>() {
                            @Override
                            public void accept(FileUploadResult fileUploadResult) throws Exception {

                            }
                        })
                ;
            }
        });
    }

    /**
     * 获取图片宽和高
     *
     * @param path
     */
    private void getBitmapInfo(String path) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        // inJustDecodeBounds如果设置为true,仅仅返回图片实际的宽和高,宽和高是赋值给opts.outWidth,opts.outHeight;
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, op); //获取尺寸信息
        Logger.d("图片宽度===" + op.outWidth);
        Logger.d("图片高度===" + op.outHeight);

    }


    private void initImageCropper() {
        if (mImageCropper == null) {
            int displayDeviceHeight = getResources().getDisplayMetrics().heightPixels - DensityUtil.dip2px(this, 53);
            mImageCropper = ImageCropper.Helper.with(this).setTranslucentStatusHeight(displayDeviceHeight).create();
            mImageCropper.setCallback(new ImageCropper.Callback() {
                @Override
                public void onPictureCropOut(Bitmap bitmap, String tag) {
                    Log.d("Photo", "图片裁减成功...." + tag);

                    mIvPhoto.setImageBitmap(bitmap);
                }
            });
        }
    }

    private void login() {
        String pwd = MD5.hexdigest("123456".getBytes());
//        String pwd = MD5.hexdigest("qqqqqq".getBytes());
        MobileLoginReqBean reqBean = new MobileLoginReqBean();
//        reqBean.setMobile("15267163669");
        reqBean.setMobile("17681832816");
//        reqBean.setMobile("18867142516");
//        reqBean.setMobile("15967132742");
        reqBean.setQueryPswd(pwd);
        HttpReqManager.getInstance().getService(LoginService.class)
                .mobileLogin(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<UserInfo>>() {
                    @Override
                    public void accept(BaseResponse<UserInfo> userInfoBaseResponse) throws Exception {
                        ToastUtil.showMessage(MainActivity.this, "登录成功");
                        UserInfo userInfo = userInfoBaseResponse.getData();
                        UserManager.getInstance(MainActivity.this).updateOrSaveUserInfo(userInfo);
                        HttpReqManager.getInstance().setUserId(userInfo.getUserId());
                        HttpReqManager.getInstance().setToken(userInfo.getToken());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        t.printStackTrace();
                    }
                });
    }

//    /**
//     * 初始化新的网络框架
//     */
//    private void initNetwork() {
//        String deviceId = SPUtil.getString(this, "sysconfig", "deviceId");
//        if (TextUtils.isEmpty(deviceId)) {
//            //采用自己生产的UUID来当做设备唯一ID，存储在SharedPreferenes里，应用卸载重装会重新生成
//            deviceId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
//            SPUtil.put(this, "sysconfig", "deviceId", deviceId);
//        }
//
//        String channel = "official";
//        try {
//            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//            channel = appInfo.metaData.getString("UMENG_CHANNEL");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        UserManager userManager = UserManager.getInstance(this);
//        HttpRequestConfig config = new HttpRequestConfig.Builder(this)
//                .setDebug(BuildConfig.DEBUG)
//                .setAppChannel(channel)
//                .setAppVersion(SystemUtil.getCurrentAppVersionName(this))
//                .setDeviceId(deviceId)
////                .setBaseUrl("https://testapi.54jietiao.com")
//                .setBaseUrl("http://192.168.1.217")
//                .setUserId(userManager.getUserInfo().getUserId())
//                .setToken(userManager.getUserInfo().getToken())
//                .build();
//        HttpReqManager.init(config);
//    }

}
