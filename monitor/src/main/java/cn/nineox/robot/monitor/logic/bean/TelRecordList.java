package cn.nineox.robot.monitor.logic.bean;

import java.util.List;

/**
 * Created by me on 17/12/23.
 */

public class TelRecordList extends PageBean{

    private List<TelRecordBean> list;

    public List<TelRecordBean> getList() {
        return list;
    }

    public void setList(List<TelRecordBean> list) {
        this.list = list;
    }
}
