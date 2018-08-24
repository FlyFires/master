package cn.nineox.robot.monitor.logic.bean;

import android.text.TextUtils;

import java.io.Serializable;

import cn.nineox.robot.monitor.R;

/**
 * Created by me on 17/12/20.
 */

public class ContactBean implements Serializable{

    private String id;

    private String name;

    private String mobile;

    private String headpic;

    private int position;

    private String mid;

    private boolean contact;

    private boolean family;

    //默认的背景
    private int resId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String  getSimpleName(){
        if(!TextUtils.isEmpty(name)){
            return name.substring(name.length() - 1,name.length());
        } else {
            return mobile.substring(mobile.length() - 1,mobile.length());
        }
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public boolean isContact() {
        return contact;
    }

    public void setContact(boolean contact) {
        this.contact = contact;
    }

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }

    public int getResId() {

        switch (position){
            case 1:
                return R.drawable.circle_bg_green;
            case 2:
                return R.drawable.circle_bg_1;
            case 3:
                return R.drawable.circle_bg_2;
            case 4:
                return R.drawable.circle_bg_3;
            case 5:
                return R.drawable.circle_bg_4;
            case 6:
                return R.drawable.circle_bg_5;
            case 7:
                return R.drawable.circle_bg_3;
            case 8:
                return R.drawable.circle_bg_5;
        }
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
