package cn.nineox.robot.appstrore.logic.bean;

import com.liulishuo.filedownloader.BaseDownloadTask;

/**
 * Created by me on 17/11/19.
 */

public class DownloadEvent {


    public BaseDownloadTask task;

    public int soFarBytes;

    public int totalBytes;


    public DownloadEvent(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        this.task = task;
        this.soFarBytes = soFarBytes;
        this.totalBytes = totalBytes;

    }
}
