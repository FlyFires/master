package cn.nineox.robot.logic.bean;

import java.util.List;

/**
 * Created by Eval on 2017/11/16.
 */
public class ShareUsersBean extends PageBean {

    private List<UserBean> list ;


    public List<UserBean> getList() {
        return list;
    }

    public void setList(List<UserBean> list) {
        this.list = list;
    }
}
