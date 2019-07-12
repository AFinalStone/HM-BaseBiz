package com.hm.iou.base.comm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * Created by hjy on 2019/3/2.
 */
public class ClipBoardBean implements Parcelable {

    public ClipBoardBean() {
    }

    private String shearCode;
    private String shearPicUrl;     //像弹窗那样展示的图片地址
    private String shearUrl;         //跳转的连接
    private int shouldShowPic;       //是否需要展示图片（弹窗）【YesNoEnum】:0-否，1-是

    private ExtInfo extInfo;

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

    public ExtInfo getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(ExtInfo extInfo) {
        this.extInfo = extInfo;
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

    public static ExtInfo parseExtInfo(JsonObject ext) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(ext, ExtInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class ExtInfo implements Serializable, Parcelable {

        private int sex;
        private String avatarUrl;
        private String showId;
        private String nickName;

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getShowId() {
            return showId;
        }

        public void setShowId(String showId) {
            this.showId = showId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        @Override
        public String toString() {
            return "ExtInfo{" +
                    "sex=" + sex +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", showId='" + showId + '\'' +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.sex);
            dest.writeString(this.avatarUrl);
            dest.writeString(this.showId);
            dest.writeString(this.nickName);
        }

        public ExtInfo() {
        }

        protected ExtInfo(Parcel in) {
            this.sex = in.readInt();
            this.avatarUrl = in.readString();
            this.showId = in.readString();
            this.nickName = in.readString();
        }

        public static final Creator<ExtInfo> CREATOR = new Creator<ExtInfo>() {
            @Override
            public ExtInfo createFromParcel(Parcel source) {
                return new ExtInfo(source);
            }

            @Override
            public ExtInfo[] newArray(int size) {
                return new ExtInfo[size];
            }
        };
    }

}
