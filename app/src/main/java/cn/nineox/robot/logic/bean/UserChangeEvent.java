package cn.nineox.robot.logic.bean;

/**
 * Created by me on 18/1/24.
 */

public class UserChangeEvent {

    private String name;

    private String headPic;

    private String eventList;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getEventList() {
        return eventList;
    }

    public void setEventList(String eventList) {
        this.eventList = eventList;
    }
}
