package cn.nineox.robot.monitor.logic.bean;

public class IotAudioBean extends TLBaseBean {

    private PlayLoad playload;

    public PlayLoad getPlayload() {
        return playload;
    }

    public void setPlayload(PlayLoad playload) {
        this.playload = playload;
    }

    public class PlayLoad {

        private String id;

        private String name;

        private String tip;

        private String url;

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

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
