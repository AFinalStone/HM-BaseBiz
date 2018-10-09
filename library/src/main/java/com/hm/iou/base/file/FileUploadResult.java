package com.hm.iou.base.file;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by hjy on 2018/5/24.
 */

public class FileUploadResult implements Serializable {

    private String fileId;
    private String fileUrl;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
