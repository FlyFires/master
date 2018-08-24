package cn.nineox.robot.logic.bean;

import java.util.List;

/**
 * Created by me on 17/11/1.
 */

public class ChatRecordListBean extends PageBean{

    private List<ChatRecordBean> list ;


    public void setList(List<ChatRecordBean> list){
        this.list = list;
    }
    public List<ChatRecordBean> getList(){
        return this.list;
    }



}
