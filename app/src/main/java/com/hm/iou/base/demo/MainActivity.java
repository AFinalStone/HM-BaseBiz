package com.hm.iou.base.demo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.base.photo.ImageCropper;
import com.hm.iou.base.photo.PhotoUtil;
import com.hm.iou.base.photo.SelectPicDialog;
import com.hm.iou.base.version.CheckVersionResBean;
import com.hm.iou.base.version.VersionApi;
import com.hm.iou.base.webview.BaseWebviewActivity;
import com.hm.iou.logger.Logger;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.tools.DensityUtil;
import com.hm.iou.tools.SPUtil;
import com.hm.iou.tools.SystemUtil;

import java.util.UUID;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    ImageView mIvPhoto;
    ImageCropper mImageCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.init(this, true);
        BaseBizAppLike baseBizAppLike = new BaseBizAppLike();
        baseBizAppLike.onCreate(this);
        baseBizAppLike.initServer("https://testapi.54jietiao.com",
                "https://testapi.54jietiao.com",
                "https://testapi.54jietiao.com");
        initNetwork();


        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaseWebviewActivity.class);
                intent.putExtra("url", "http://192.168.1.217/appWebView/index.html");
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
                VersionApi.checkVersion()
                        .subscribe(new Consumer<BaseResponse<CheckVersionResBean>>() {
                            @Override
                            public void accept(BaseResponse<CheckVersionResBean> checkVersionResBeanBaseResponse) throws Exception {

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        });
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
        }
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

    /**
     * 初始化新的网络框架
     */
    private void initNetwork() {
        String deviceId = SPUtil.getString(this, "sysconfig", "deviceId");
        if (TextUtils.isEmpty(deviceId)) {
            //采用自己生产的UUID来当做设备唯一ID，存储在SharedPreferenes里，应用卸载重装会重新生成
            deviceId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            SPUtil.put(this, "sysconfig", "deviceId", deviceId);
        }

        String channel = "official";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserManager userManager = UserManager.getInstance(this);
        HttpRequestConfig config = new HttpRequestConfig.Builder(this)
                .setDebug(BuildConfig.DEBUG)
                .setAppChannel(channel)
                .setAppVersion(SystemUtil.getCurrentAppVersionName(this))
                .setDeviceId(deviceId)
                .setBaseUrl("https://testapi.54jietiao.com")
                .setUserId(userManager.getUserInfo().getUserId())
                .setToken(userManager.getUserInfo().getToken())
                .build();
        HttpReqManager.init(config);
    }

}
