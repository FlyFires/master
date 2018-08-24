package cn.nineox.robot.logic.bean;

/**
 * Created by me on 17/11/1.
 */

public class FeedbackBean {

    private int feedbackId;

    private String content;

    private String mobile;

    private int uid;

    private String reply;

    private int replyTime;

    public void setFeedbackId(int feedbackId){
        this.feedbackId = feedbackId;
    }
    public int getFeedbackId(){
        return this.feedbackId;
    }
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return this.content;
    }
    public void setMobile(String mobile){
        this.mobile = mobile;
    }
    public String getMobile(){
        return this.mobile;
    }
    public void setUid(int uid){
        this.uid = uid;
    }
    public int getUid(){
        return this.uid;
    }
    public void setReply(String reply){
        this.reply = reply;
    }
    public String getReply(){
        return this.reply;
    }
    public void setReplyTime(int replyTime){
        this.replyTime = replyTime;
    }
    public int getReplyTime(){
        return this.replyTime;
    }
}
