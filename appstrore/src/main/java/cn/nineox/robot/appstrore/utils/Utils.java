package cn.nineox.robot.appstrore.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;

import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.common.weiget.TFTipsToast;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.SilentInstaller;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.utils.PackageUtil;
import cn.nineox.xframework.core.common.utils.SdCardUtil;

/**
 * Created by me on 17/11/19.
 */

public class Utils {

    private static String TAG = "Utils";

    public static String getCacheDir() {
        return Const.FILE_DOWNLOAD_DIR;
    }

    public static String getAPKCachePath(String name) {
        return getCacheDir() + "/" + name + ".apk";
    }


    public static void install(Context context, File file) {
        SilentInstaller silentInstaller = new SilentInstaller();
        Log.e("silentInstaller", "isSystemApplication:" + silentInstaller.isSystemApplication(context));
        if (silentInstaller.isSystemApplication(context)) {
            silentInstaller.installSilent(context, file.getAbsolutePath());
        } else {
            PackageUtil.install(context, file);
        }
    }


    public static boolean hasSDCard(Context context) {
        boolean hasSDCard = false;
        File file = new File("/mnt/extsd");
        try {
            File[] files = file.listFiles();
            if (files.length > 0) {
                hasSDCard = true;
                Log.e(TAG, "SD卡已安装---------------");
            }
        } catch (Exception e) {
            hasSDCard = false;
            Log.e(TAG, "SD卡未安装---------------");
        }

        String tempSize = getSDTotalSize(context);
        if (TextUtils.isEmpty(tempSize)) {
            hasSDCard = true;
            Log.e(TAG, "SD卡是空的-----------");
        } else {
            Log.e(TAG, "SD卡的空间是-----------" + tempSize);
            if (tempSize.equals("0.00 B")) {
                hasSDCard = false;
                Log.e(TAG, "SD卡没有安装-----------");
            }
        }

        return hasSDCard;

    }

    public static String getSDTotalSize(Context context) {
        File path = new File("/mnt/extsd");
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }


    /* 卸载apk */
    public static void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    public static boolean hasUpdate(Context context, String packageName, int version) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo.versionCode > version) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return true;
        }

    }

}
