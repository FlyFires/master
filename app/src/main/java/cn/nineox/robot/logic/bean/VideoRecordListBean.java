package cn.nineox.robot.logic.bean;

import java.util.List;

/**
 * Created by me on 17/11/1.
 */

public class VideoRecordListBean extends PageBean{

    private List<VideoRecordBean> list;


    public void setList(List<VideoRecordBean> list) {
        this.list = list;
    }

    public List<VideoRecordBean> getList() {
        return this.list;
    }



}
