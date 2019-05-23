package com.hm.iou.base.comm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hjy on 2019/3/2.
 */
public class ClipBoardBean implements Parcelable {

    private String shearCode;
    private String shearPicUrl;     //像弹窗那样展示的图片地址
    private String shearUrl;         //跳转的连接
    private int shouldShowPic;       //是否需要展示图片（弹窗）【YesNoEnum】:0-否，1-是

    public String getShearCode() {
        return shearCode;
    }

    public void setShearCode(String shearCode) {
        this.shearCode = shearCode;
    }

    public String getShearPicUrl() {
        return shearPicUrl;
    }

    public void setShearPicUrl(String shearPicUrl) {
        this.shearPicUrl = shearPicUrl;
    }

    public String getShearUrl() {
        return shearUrl;
    }

    public void setShearUrl(String shearUrl) {
        this.shearUrl = shearUrl;
    }

    public int getShouldShowPic() {
        return shouldShowPic;
    }

    public void setShouldShowPic(int shouldShowPic) {
        this.shouldShowPic = shouldShowPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.shearCode);
        dest.writeString(this.shearPicUrl);
        dest.writeString(this.shearUrl);
        dest.writeInt(this.shouldShowPic);
    }

    protected ClipBoardBean(Parcel in) {
        this.shearCode = in.readString();
        this.shearPicUrl = in.readString();
        this.shearUrl = in.readString();
        this.shouldShowPic = in.readInt();
    }

    public static final Creator<ClipBoardBean> CREATOR = new Creator<ClipBoardBean>() {
        @Override
        public ClipBoardBean createFromParcel(Parcel source) {
            return new ClipBoardBean(source);
        }

        @Override
        public ClipBoardBean[] newArray(int size) {
            return new ClipBoardBean[size];
        }
    };
}
