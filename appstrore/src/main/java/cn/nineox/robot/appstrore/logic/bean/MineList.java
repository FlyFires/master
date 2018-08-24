package cn.nineox.robot.appstrore.logic.bean;

import java.util.List;

/**
 * Created by me on 17/11/18.
 */

public class MineList extends PageBean{

    private List<MineBean> list;

    public List<MineBean> getList() {
        return list;
    }

    public void setList(List<MineBean> list) {
        this.list = list;
    }
}
