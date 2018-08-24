package cn.nineox.robot.logic.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by me on 18/1/23.
 */

public class UsageBean {

    private Drawable icon;

    private String appName;

    private String packageName;

    private long totalTime;

    private int launchCount;

//    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }


    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public int getLaunchCount() {
        return launchCount;
    }

    public void setLaunchCount(int launchCount) {
        this.launchCount = launchCount;
    }
}
