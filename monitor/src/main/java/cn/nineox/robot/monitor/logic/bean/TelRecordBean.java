package cn.nineox.robot.monitor.logic.bean;

import cn.nineox.robot.monitor.R;

/**
 * Created by me on 17/12/23.
 */

public class TelRecordBean {

    private String name;

    private String mobile;

    private int type;

    private long startTime;

    private long endTime;

    private long posttime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getType() {
        return type;
    }

    public int getTypeIcon(){
        if(type == 1){
            return  R.mipmap.ic_incoming_miss;
        }
        return R.mipmap.ic_outcoming_miss;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getPosttime() {
        return posttime;
    }

    public void setPosttime(long posttime) {
        this.posttime = posttime;
    }
}
