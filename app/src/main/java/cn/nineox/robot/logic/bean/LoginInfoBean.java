package cn.nineox.robot.logic.bean;

import android.text.TextUtils;

/**
 * Created by me on 17/11/1.
 */

public class LoginInfoBean {

    private String uid;

    private String name;

    private String password;

    private String headpic;

    private String sessionId;

    private String token;

    private String mobile;

    private DeviceBean device;

    private UserType userType;

    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUid(){
        return this.uid;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setHeadpic(String headpic){
        this.headpic = headpic;
    }
    public String getHeadpic(){
        return this.headpic;
    }
    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }
    public String getSessionId(){
        return this.sessionId;
    }
    public void setToken(String token){
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }

    public String getMobile() {
        return mobile;
    }

    public String getP2PUid(){
        if(TextUtils.isEmpty(mobile)){
            return uid;
        }
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean hasLogin(){
        return !TextUtils.isEmpty(token);
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginInfoBean{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", headpic='" + headpic + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
