package cn.nineox.robot.logic.bean;

public class Caller {

    private String client;

    private CallerUser user;

    private CallerDevice device;


    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public CallerUser getUser() {
        return user;
    }

    public void setUser(CallerUser user) {
        this.user = user;
    }

    public CallerDevice getDevice() {
        return device;
    }

    public void setDevice(CallerDevice device) {
        this.device = device;
    }

    public class CallerUser {


        private String userId;

        private String userName;

        private String headPicture;

        private String mobile;
        private String userType;


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getHeadPicture() {
            return headPicture;
        }

        public void setHeadPicture(String headPicture) {
            this.headPicture = headPicture;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }
    }


    public class CallerDevice{
        private String deviceId;

        private String deviceName;

        private String headPicture;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getHeadPicture() {
            return headPicture;
        }

        public void setHeadPicture(String headPicture) {
            this.headPicture = headPicture;
        }
    }
}
