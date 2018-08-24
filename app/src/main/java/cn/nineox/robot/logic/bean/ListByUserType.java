package cn.nineox.robot.logic.bean;

/**
 * Created by me on 18/4/25.
 */

public class ListByUserType {


    private String icon;

    private UserType userType;

    private boolean select;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
