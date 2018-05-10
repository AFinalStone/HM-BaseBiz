package com.hm.iou.base.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hm.iou.base.photo.ImageCropper;
import com.hm.iou.base.photo.PhotoUtil;
import com.hm.iou.tools.DensityUtil;

public class MainActivity extends AppCompatActivity {

    ImageView mIvPhoto;
    ImageCropper mImageCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
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

                mImageCropper.crop(path, 150, 100, true, "crop");
            }
        } else if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Log.d("Photo", "相册图片返回结果....");
                String path = PhotoUtil.getPath(this, data.getData());
                Log.d("Photo", "album path: " + path);
/*                Bitmap bitmap = BitmapFactory.decodeFile(path);
                mIvPhoto.setImageBitmap(bitmap);*/

                mImageCropper.crop(path, 150, 100, false, "crop");

            }
        }
    }
}
