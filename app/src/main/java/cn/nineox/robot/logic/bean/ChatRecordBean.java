package cn.nineox.robot.logic.bean;


import java.io.Serializable;

import cn.nineox.xframework.base.BaseBean;

/**
 * Created by me on 17/10/29.
 */

public class ChatRecordBean extends BaseBean implements Serializable{


    private String mid;

    private String chatDate;

    private String content;



    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
