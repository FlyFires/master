package cn.nineox.robot.monitor.logic.bean;

public class Peer {

    private String peerUid;

    public String getPeerUid() {
        return peerUid;
    }

    public void setPeerUid(String peerUid) {
        this.peerUid = peerUid;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "peerUid='" + peerUid + '\'' +
                '}';
    }
}
