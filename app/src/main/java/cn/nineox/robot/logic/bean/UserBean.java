package cn.nineox.robot.logic.bean;

import android.databinding.Bindable;
import android.text.TextUtils;

import java.io.Serializable;

import cn.nineox.robot.BR;
import cn.nineox.xframework.base.BaseBean;

/**
 * Created by me on 17/11/1.
 */

public class
UserBean extends BaseBean implements Serializable{

    private String uid;

    @Bindable
    private String name;
    @Bindable
    private String mobile;
    @Bindable
    private String headPic;

    private long posttime;

    private UserType userType;

    private DeviceBean device;

    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUid(){
        return this.uid;
    }
    public void setName(String name){
        notifyPropertyChanged(BR.name);
        this.name = name;
    }
    public String getName(){
        if(TextUtils.isEmpty(name)){
            //name = "用户"+uid;
            name = "";
        }
        return this.name;
    }
    public void setMobile(String mobile){
        notifyPropertyChanged(BR.mobile);
        this.mobile = mobile;
    }
    public String getMobile(){
        return this.mobile;
    }
    public void setHeadPic(String headPic){
        notifyPropertyChanged(BR.mobile);
        this.headPic = headPic;
    }
    public String getHeadPic(){
        return this.headPic;
    }
    public void setPosttime(long posttime){
        this.posttime = posttime;
    }
    public long getPosttime(){
        return this.posttime;
    }


    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", headPic='" + headPic + '\'' +
                ", posttime=" + posttime +
                '}';
    }
}
