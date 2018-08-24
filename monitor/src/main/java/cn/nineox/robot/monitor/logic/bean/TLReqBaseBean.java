package cn.nineox.robot.monitor.logic.bean;

import cn.nineox.xframework.base.BaseBean;

public class TLReqBaseBean extends BaseBean{

    private String apiKey;

    private String deviceId;

    private int type;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
