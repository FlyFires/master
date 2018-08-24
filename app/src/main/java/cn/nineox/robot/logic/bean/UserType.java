package cn.nineox.robot.logic.bean;

import java.io.Serializable;

/**
 * Created by me on 18/4/25.
 */

public class UserType implements Serializable{

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
