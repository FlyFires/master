package cn.nineox.robot.monitor.logic.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by me on 18/4/25.
 */

public class Contact implements Serializable{

//
//    "mid":"8e710daca997fff5",
//            "midName":null,
//            "uid":3515657358303,
//            "userType":
//
//    {
//        "key":"dad",
//            "desc":"爸爸"
//    }


    private String mid;

    private String midName;

    private String uid;

    private UserType userType;

    private String icon;

    private String mobile;

    public String getMobile() {
        if(TextUtils.isEmpty(mobile)){
            return uid;
        }
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public class UserType {
        private String key;

        private String desc;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    @Override
    public String toString() {
        return "Contact{" +
                "mid='" + mid + '\'' +
                ", midName='" + midName + '\'' +
                ", uid='" + uid + '\'' +
                ", userType=" + userType +
                ", icon='" + icon + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
