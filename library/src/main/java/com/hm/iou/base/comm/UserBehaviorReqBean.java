package com.hm.iou.base.comm;

import java.io.Serializable;

public class UserBehaviorReqBean implements Serializable {

    public String behaviorCode;
    public String userInput;

    public String getBehaviorCode() {
        return behaviorCode;
    }

    public void setBehaviorCode(String behaviorCode) {
        this.behaviorCode = behaviorCode;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
