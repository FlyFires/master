package cn.nineox.robot.logic.bean;

import java.util.List;

/**
 * Created by Eval on 2017/11/16.
 */
public class ChatListBean extends PageBean {

    private List<ChatBean> list ;


    public List<ChatBean> getList() {
        return list;
    }

    public void setList(List<ChatBean> list) {
        this.list = list;
    }
}
