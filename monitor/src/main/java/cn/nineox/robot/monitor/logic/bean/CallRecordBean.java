package cn.nineox.robot.monitor.logic.bean;

/**
 * Created by me on 17/12/20.
 */

public class CallRecordBean {

    private String id;

    private String name;

    private String mobile;

    private String headpic;

    private int position;

    private boolean contact;

    private boolean family;

    private long posttime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isContact() {
        return contact;
    }

    public void setContact(boolean contact) {
        this.contact = contact;
    }

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }

    public long getPosttime() {
        return posttime;
    }

    public void setPosttime(long posttime) {
        this.posttime = posttime;
    }
}
