package com.hm.iou.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.tools.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hjy on 2018/6/5.
 */

public class ImageGalleryActivity extends BaseActivity {

    public static final String EXTRA_KEY_IMAGES = "images";
    public static final String EXTRA_KEY_INDEX = "index";

    private ViewPager mViewPager;
    private String[] mUrlArr;
    private int mIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.base_activity_image_gallery;
    }

    @Override
    protected MvpActivityPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mViewPager = findViewById(R.id.vp_image_gallery);
        Intent data = getIntent();
        mUrlArr = data.getStringArrayExtra(EXTRA_KEY_IMAGES);
        mIndex = data.getIntExtra(EXTRA_KEY_INDEX, 0);
        if (mUrlArr == null && bundle != null) {
            mUrlArr = bundle.getStringArray(EXTRA_KEY_IMAGES);
            mIndex = bundle.getInt(EXTRA_KEY_INDEX);
        }
        List<String> list = new ArrayList<>();
        if (mUrlArr != null) {
            for (String url : mUrlArr) {
                list.add(url);
            }
        }
        mViewPager.setAdapter(new ImageAdapter(this, list));
        mViewPager.setCurrentItem(mIndex);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(EXTRA_KEY_IMAGES, mUrlArr);
        outState.putInt(EXTRA_KEY_INDEX, mIndex);
    }

    class ImageAdapter extends PagerAdapter {

        private Context mContext;
        private LinkedList<View> mViewCache;
        private List<String> mDatas;
        private int mChildCount;

        public ImageAdapter(Context context, List<String> list) {
            this.mContext = context;
            this.mDatas = list;
            mViewCache = new LinkedList<>();
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
                PhotoView photoView = new PhotoView(mContext);
                convertView = photoView;
            } else {
                convertView = mViewCache.removeFirst();
            }
            final PhotoView view = (PhotoView) convertView;
            String url = mDatas.get(position);
            ImageLoader.getInstance(mContext).displayImage(url, view);
            container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return convertView;
        }
    }

}