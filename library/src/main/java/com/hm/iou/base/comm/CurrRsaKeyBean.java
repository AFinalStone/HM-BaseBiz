package com.hm.iou.base.comm;

import java.io.Serializable;

public class CurrRsaKeyBean implements Serializable {

    private String pubVersion;
    private String rsaPubKey;

    public String getPubVersion() {
        return pubVersion;
    }

    public void setPubVersion(String pubVersion) {
        this.pubVersion = pubVersion;
    }

    public String getRsaPubKey() {
        return rsaPubKey;
    }

    public void setRsaPubKey(String rsaPubKey) {
        this.rsaPubKey = rsaPubKey;
    }

}
