package cn.nineox.robot.monitor.logic.bean;

import java.util.List;

/**
 * Created by me on 17/12/21.
 */

public class ContactList extends PageBean{


    private List<ContactBean> list;

    public List<ContactBean> getList() {
        return list;
    }

    public void setList(List<ContactBean> list) {
        this.list = list;
    }
}
