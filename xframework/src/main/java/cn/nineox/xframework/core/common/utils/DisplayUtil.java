package cn.nineox.xframework.core.common.utils;

import android.content.Context;
import android.os.PowerManager;
import android.util.DisplayMetrics;

import cn.nineox.xframework.core.android.log.Log;

/**
 * Created by me on 17/9/27.
 */
public class DisplayUtil {
    private static final String TAG = DisplayUtil.class.getSimpleName();

    /**
     * 获取 显示信息
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    /**
     * 打印 显示信息
     */
    public static DisplayMetrics printDisplayInfo(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        if (Log.isPrint) {
            StringBuilder sb = new StringBuilder();
            sb.append("_______  显示信息:  ");
            sb.append("\ndensity         :").append(dm.density);
            sb.append("\ndensityDpi      :").append(dm.densityDpi);
            sb.append("\nheightPixels    :").append(dm.heightPixels);
            sb.append("\nwidthPixels     :").append(dm.widthPixels);
            sb.append("\nscaledDensity   :").append(dm.scaledDensity);
            sb.append("\nxdpi            :").append(dm.xdpi);
            sb.append("\nydpi            :").append(dm.ydpi);
            Log.i(TAG, sb.toString());
        }
        return dm;
    }


    public static PowerManager.WakeLock wakeLock(Context context) {
        PowerManager powerManager = ((PowerManager) context.getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
        return mWakeLock;
    }

    public static void releaseWakeLock(PowerManager.WakeLock wakeLock) {
        if (null != wakeLock) {
            wakeLock.release();
        }
    }
}
