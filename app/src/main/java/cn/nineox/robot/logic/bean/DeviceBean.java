package cn.nineox.robot.logic.bean;

import android.databinding.Bindable;
import android.text.TextUtils;

import java.io.Serializable;

import cn.nineox.xframework.base.BaseBean;

/**
 * Created by me on 17/10/29.
 */

public class DeviceBean extends BaseBean implements Serializable{


    private String id;

    private String mid;

    @Bindable
    private String midName;

    private String ipAddress;

    private long posttime;


    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setMid(String mid){
        this.mid = mid;
    }
    public String getMid(){
        if(TextUtils.isEmpty(mid)){
            mid = "";
        }
        return this.mid;
    }
    public void setMidName(String midName){
        this.midName = midName;
    }
    public String getMidName(){
        return this.midName;
    }
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }
    public String getIpAddress(){
        return this.ipAddress;
    }
    public void setPosttime(long posttime){
        this.posttime = posttime;
    }
    public long getPosttime(){
        return this.posttime;
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "id='" + id + '\'' +
                ", mid='" + mid + '\'' +
                ", midName='" + midName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", posttime=" + posttime +
                '}';
    }
}
