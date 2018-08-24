package cn.nineox.robot.logic.bean;

import java.util.List;

/**
 * Created by me on 17/11/26.
 */

public class VideoDetailListBean extends PageBean{

    private List<VideoDetailBean> list;


    public void setList(List<VideoDetailBean> list) {
        this.list = list;
    }

    public List<VideoDetailBean> getList() {
        return this.list;
    }


}
