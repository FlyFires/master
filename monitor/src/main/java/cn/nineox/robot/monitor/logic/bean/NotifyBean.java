package cn.nineox.robot.monitor.logic.bean;

public class NotifyBean extends TLReqBaseBean {


    private NotifyStaus status;


    public NotifyStaus getStatus() {
        return status;
    }

    public void setStatus(NotifyStaus status) {
        this.status = status;
    }

    public class NotifyStaus {
        private String title;

        private String mediaId;

        private String play;


        /// 当type=0时,设备状态请求数据示例：

        private int vol;

        private int battery;

        private int sfree;

        private int shake;

        private int power;

        private int bln;

        private int charging;

        private int lbi;

        private int tcard;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getPlay() {
            return play;
        }

        public void setPlay(String play) {
            this.play = play;
        }

        public int getVol() {
            return vol;
        }

        public void setVol(int vol) {
            this.vol = vol;
        }

        public int getBattery() {
            return battery;
        }

        public void setBattery(int battery) {
            this.battery = battery;
        }

        public int getSfree() {
            return sfree;
        }

        public void setSfree(int sfree) {
            this.sfree = sfree;
        }

        public int getShake() {
            return shake;
        }

        public void setShake(int shake) {
            this.shake = shake;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public int getBln() {
            return bln;
        }

        public void setBln(int bln) {
            this.bln = bln;
        }

        public int getCharging() {
            return charging;
        }

        public void setCharging(int charging) {
            this.charging = charging;
        }

        public int getLbi() {
            return lbi;
        }

        public void setLbi(int lbi) {
            this.lbi = lbi;
        }

        public int getTcard() {
            return tcard;
        }

        public void setTcard(int tcard) {
            this.tcard = tcard;
        }
    }
}
