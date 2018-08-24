package cn.nineox.robot.logic.bean;

import java.util.List;

/**
 * Created by me on 17/11/1.
 */

public class EmquipmentBean extends PageBean{

    private List<DeviceBean> list ;


    public void setList(List<DeviceBean> list){
        this.list = list;
    }
    public List<DeviceBean> getList(){
        return this.list;
    }



    @Override
    public String toString() {
        return "EmquipmentBean{" +
                "list=" + list +
                '}';
    }
}
