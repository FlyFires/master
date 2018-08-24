package cn.nineox.robot.logic.bean;

/**
 * Created by me on 17/11/25.
 */

public class EquipmentGet {

    private String mid;

    private String id;

    private String midName;

    private String productionDate;

    private UserType userType;


    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMid() {
        return mid;
    }

    public String getId() {
        return id;
    }

    public String getMidName() {
        return midName;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
