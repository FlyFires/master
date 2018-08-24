/**
 * Copyright 2017 bejson.com
 */
package cn.nineox.robot.appstrore.logic.bean;

import android.databinding.Bindable;
import android.text.TextUtils;

import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.xframework.base.BaseBean;

public class AppInfo extends BaseBean{

    private String createTime;
    private int createUserId;
    private int lastUpdateTime;
    private int lastUpdateUserId;
    private String id;
    private String name;
    private String description;
    private String remark;
    private String size;
    private int install;
    private String icon;
    private String issue;
    private int deleted;

    private String url;


    @Bindable
    private BaseDownloadTask task;

    private List<FileList> fileList;

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setLastUpdateTime(int lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateUserId(int lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public int getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setInstall(int install) {
        this.install = install;
    }

    public int getInstall() {
        return install;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getIssue() {
        return issue;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setFileList(List<FileList> fileList) {
         this.fileList = fileList;
     }
     public List<FileList> getFileList() {
         if(fileList == null ||fileList.size() == 0){
             fileList = new ArrayList<>();
             FileList fileList1 = new FileList();
             fileList1.setPhysicalPath("http://g.hiphotos.bdimg.com/wisegame/pic/item/10dfa9ec8a136327e4d96d6b9a8fa0ec08fac7b6.jpg");
             FileList fileList2 = new FileList();
             fileList2.setPhysicalPath("http://a.hiphotos.bdimg.com/wisegame/pic/item/d439b6003af33a875170af42cd5c10385343b54d.jpg");
             fileList.add(fileList1);
             fileList.add(fileList2);
         }
         return fileList;
     }

    public String getUrl() {
        if(TextUtils.isEmpty(url)){
            return "http://gyxz.exmmw.cn/az/rj_hyb1/xueqiu.apk";
        }
        return url;
    }

    public BaseDownloadTask getTask() {
        return task;
    }

    public void setTask(BaseDownloadTask task) {
        this.task = task;
    }
}