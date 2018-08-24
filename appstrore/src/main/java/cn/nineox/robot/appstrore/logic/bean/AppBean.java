package cn.nineox.robot.appstrore.logic.bean;

import android.databinding.Bindable;
import android.graphics.drawable.Drawable;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.io.Serializable;

import cn.nineox.robot.appstrore.BR;
import cn.nineox.xframework.base.BaseBean;

/**
 * Created by me on 17/11/18.
 */

public class AppBean extends BaseBean implements Serializable {


    private String createTime;
    private int createUserId;
    private String lastUpdateTime;
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

    private Drawable iconDrawable;

    private String packageName;

    private int versionCode;


    private BaseDownloadTask task;

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

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateTime() {
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


    public BaseDownloadTask getTask() {
        return task;
    }

    public void setTask(BaseDownloadTask task) {

        this.task = task;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Bindable
    public boolean isPause(){
        if(task != null && task.getStatus() == FileDownloadStatus.paused){
            return true;
        }
        return false;
    }

    public void pause(){
        notifyChange();

    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
