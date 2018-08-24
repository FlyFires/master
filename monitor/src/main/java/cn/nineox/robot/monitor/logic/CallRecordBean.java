package cn.nineox.robot.monitor.logic;

import cn.nineox.xframework.base.BaseBean;

/**
 * Created by me on 17/12/17.
 */

public class CallRecordBean extends BaseBean{


    private String name;

    private long time;

    private int type;

    public CallRecordBean(String name, long time, int type) {
        this.name = name;
        this.time = time;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
