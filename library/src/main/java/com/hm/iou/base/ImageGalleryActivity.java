package com.hm.iou.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.iou.base.file.FileUtil;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.tools.ToastUtil;
import com.hm.iou.uikit.CircleIndicator;
import com.hm.iou.uikit.dialog.HMAlertDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hjy on 2018/6/5.
 */

public class ImageGalleryActivity extends BaseActivity {

    public static final String EXTRA_KEY_IMAGES = "images";
    public static final String EXTRA_KEY_INDEX = "index";
    //导航栏右上角是否显示"删除"按钮，值为0时显示下载， 值为"1"时显示，其他情况都不显示
    public static final String EXTRA_KEY_SHOW_DELETE = "show_delete";

    public static final String EXTRA_KEY_DELETE_URLS = "delete_urls";

    protected ViewPager mViewPager;
    protected LinearLayout mLLBototmAction;
    protected ImageView mIvBottomAction;
    protected TextView mTvBottomAction;
    protected CircleIndicator mCircleIndicator;

    protected String[] mUrlArr;
    protected int mIndex;
    protected ImageAdapter mAdapter;
    protected int mShowDelete;

    protected ArrayList<String> mDelList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.base_activity_image_gallery;
    }

    @Override
    protected MvpActivityPresenter initPresenter() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mViewPager = findViewById(R.id.vp_image_gallery);
        mLLBototmAction = findViewById(R.id.ll_gallery_action);
        mIvBottomAction = findViewById(R.id.iv_gallery_action);
        mTvBottomAction = findViewById(R.id.tv_gallery_action);
        mCircleIndicator = findViewById(R.id.indicator_gallery);

        Intent data = getIntent();
        mUrlArr = data.getStringArrayExtra(EXTRA_KEY_IMAGES);
        mIndex = data.getIntExtra(EXTRA_KEY_INDEX, 0);
        mShowDelete = data.getIntExtra(EXTRA_KEY_SHOW_DELETE, 0);
        if (mUrlArr == null && bundle != null) {
            mUrlArr = bundle.getStringArray(EXTRA_KEY_IMAGES);
            mIndex = bundle.getInt(EXTRA_KEY_INDEX);
            mShowDelete = bundle.getInt(EXTRA_KEY_SHOW_DELETE);
        }
        List<String> list = new ArrayList<>();
        if (mUrlArr != null) {
            for (String url : mUrlArr) {
                list.add(url);
            }
        }
        mAdapter = new ImageAdapter(this, list);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mIndex);
        mCircleIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (mShowDelete == 0) {
            showDownloadAction();
        } else if (mShowDelete == 1) {
            showDeleteAction();
        }

        if (mUrlArr == null || mUrlArr.length <= 1) {
            mCircleIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDelList.isEmpty()) {
            finish();
        } else {
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_KEY_DELETE_URLS, mDelList);
            setResult(RESULT_OK, data);
            finish();
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(EXTRA_KEY_IMAGES, mUrlArr);
        outState.putInt(EXTRA_KEY_INDEX, mIndex);
        outState.putInt(EXTRA_KEY_SHOW_DELETE, mShowDelete);
    }

    /**
     * 右下角显示"删除"操作
     */
    protected void showDeleteAction() {
        mLLBototmAction.setVisibility(View.VISIBLE);
        mIvBottomAction.setImageResource(R.mipmap.uikit_ic_img_delete);
        mTvBottomAction.setText("删除");
        mLLBototmAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmDialog();
            }
        });
    }

    protected void showDownloadAction() {
        mLLBototmAction.setVisibility(View.VISIBLE);
        mIvBottomAction.setImageResource(R.mipmap.uikit_ic_img_download);
        mTvBottomAction.setText("下载");
        mLLBototmAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUrlArr != null && mIndex < mUrlArr.length) {
                    FileUtil.savePicture(ImageGalleryActivity.this, mUrlArr[mIndex]);
                }
            }
        });
    }

    private void showSavePhotoDialog(final String url) {
        new HMAlertDialog.Builder(this)
                .setTitle("保存图片")
                .setMessage("是否保存当前图片到本地？")
                .setPositiveButton("保存")
                .setNegativeButton("取消")
                .setMessageGravity(Gravity.CENTER)
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        FileUtil.savePicture(ImageGalleryActivity.this, url);
                    }

                    @Override
                    public void onNegClick() {
                    }
                })
                .create()
                .show();
    }

    private void showDeleteConfirmDialog() {
        new HMAlertDialog.Builder(this)
                .setMessage("要删除这张照片吗？")
                .setPositiveButton("删除")
                .setNegativeButton("取消")
                .setMessageGravity(Gravity.CENTER)
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        String deleteUrl = mAdapter.delete(mIndex);
                        if (!TextUtils.isEmpty(deleteUrl)) {
                            mDelList.add(deleteUrl);
                        }
                        ToastUtil.showStatusView(ImageGalleryActivity.this, "删除成功");
                        mIndex = mIndex > 0 ? mIndex - 1 : 0;
                        mViewPager.setCurrentItem(mIndex);
                        mCircleIndicator.setViewPager(mViewPager);
                        if (mAdapter.getCount() == 0) {
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .create()
                .show();
    }

    public class ImageAdapter extends PagerAdapter {

        private Context mContext;
        private LinkedList<View> mViewCache;
        private List<String> mDatas;
        private int mChildCount;

        public ImageAdapter(Context context, List<String> list) {
            this.mContext = context;
            this.mDatas = list;
            mViewCache = new LinkedList<>();
        }

        public String delete(int pos) {
            String url = null;
            if (mDatas != null && pos < mDatas.size() && pos >= 0) {
                url = mDatas.remove(pos);
            }
            notifyDataSetChanged();
            return url;
        }

        @Override
        public int getCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View contentView = (View) object;
            container.removeView(contentView);
            this.mViewCache.add(contentView);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View convertView;
            if (mViewCache.size() == 0) {
                DragPhotoView photoView = new DragPhotoView(mContext);
                convertView = photoView;
            } else {
                convertView = mViewCache.removeFirst();
            }
            final DragPhotoView view = (DragPhotoView) convertView;
            String url = mDatas.get(position);
            ImageLoader.getInstance(mContext).displayImage(url, view);
            container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            view.setTag(url);

            view.setOnExitListener(new DragPhotoView.OnExitListener() {
                @Override
                public void onExit(DragPhotoView var1, float var2, float var3, float var4, float var5) {
                    onBackPressed();
                }
            });

            view.setOnTapListener(new DragPhotoView.OnTapListener() {
                @Override
                public void onTap(DragPhotoView var1) {
                    onBackPressed();
                }
            });

            return convertView;
        }

    }

}