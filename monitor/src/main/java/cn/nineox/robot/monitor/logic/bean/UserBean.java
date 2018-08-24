package cn.nineox.robot.monitor.logic.bean;

import android.databinding.Bindable;
import android.text.TextUtils;

import java.io.Serializable;

import cn.nineox.xframework.base.BaseBean;

/**
 * Created by me on 17/11/1.
 */

public class UserBean extends BaseBean implements Serializable{

    private String uid;

    @Bindable
    private String name;
    @Bindable
    private String mobile;
    @Bindable
    private String headPic;

    private long posttime;

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
        if(TextUtils.isEmpty(name)){
            name = "用户"+uid;
        }
        return this.name;
    }
    public void setMobile(String mobile){
        this.mobile = mobile;
    }
    public String getMobile(){
        return this.mobile;
    }
    public void setHeadPic(String headPic){
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
