package cn.nineox.robot.appstrore.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.nineox.robot.appstrore.logic.bean.InstallEvent;
import cn.nineox.xframework.core.android.log.Log;

public class AppInstallReciver extends BroadcastReceiver {
    private Context mContext;
    PackageInfo packageInfo = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        EventBus.getDefault().post(new InstallEvent());
        String action = intent.getAction();
        String packageName = "";
        if (action.equals("cn.nineox.robot.appstrore.SilentInstaller")) {
            packageName = intent.getExtras().getString("packageName", "");
            String path = FileDownloadMgr.getInstance().getInstallingApp().get(packageName);
            Log.e("AppInstallReciver", packageName + ":" + path);
            if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                new File(path).delete();
            }
            return;
        }

        // 获取包名
        packageName = intent.getData().getSchemeSpecificPart();

        try {
            //获取应用名
            packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            String appName = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager())
                    .toString();

            String path = FileDownloadMgr.getInstance().getInstallingApp().get(packageName);
            Log.e("AppInstallReciver", packageName + ":" + path);
            if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                new File(path).delete();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}