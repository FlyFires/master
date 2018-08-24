package cn.nineox.robot.appstrore.logic.bean;

import java.util.List;

/**
 * Created by me on 17/11/18.
 */

public class AppList extends PageBean{

    private List<AppBean> list;

    public List<AppBean> getList() {
        return list;
    }

    public void setList(List<AppBean> list) {
        this.list = list;
    }
}
