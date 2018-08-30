package com.hm.iou.base.file;

/**
 * Created by hjy on 2018/5/24.
 */

public class FileUploadResult {

    private String fileId;
    private String filePath;
    private String fileUrl;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.fileUrl = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        this.filePath = fileUrl;
    }
}
