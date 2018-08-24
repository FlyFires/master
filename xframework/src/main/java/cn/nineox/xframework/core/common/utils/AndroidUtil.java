package cn.nineox.xframework.core.common.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.io.FileUtils;

/**
 * 手机信息 & MAC地址 & 开机时间
 *
 * Created by me on 17/9/27.
 */
public class AndroidUtil {
    private static final String TAG = AndroidUtil.class.getSimpleName();
    //Ethernet Mac Address
    private static final String ETH0_MAC_ADDRESS = "/sys/class/net/eth0/address";

    public static final String CHANNEL_ID = "1001-1001-1001";

    /**
     * 获取 Wifi MAC 地址
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     */
    @Deprecated
    public static String getMacAddress(Context context) {
        return getWifiMacAddress(context);
    }

    /**
     * 获取 Wifi MAC 地址
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     */
    public static String getWifiMacAddress(Context context) {
        //wifi mac地址
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        if (Log.isPrint) {
            Log.i(TAG, "WIFI MAC：" + mac);
        }
        return mac;
    }


    /**
     * 获取 以太网 MAC 地址
     */
    public static String getEthernetMacAddress() {
        try {
            String mac = FileUtils.readFileToString(new File(ETH0_MAC_ADDRESS));
            if (Log.isPrint) {
                Log.i(TAG, "Ethernet MAC：" + mac);
            }
            return mac;
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when getting eth0 mac address", e);
            e.printStackTrace();
            return "unknown";
        }
    }

    /**
     * 获取 ANDROID_ID
     */
    public static String getAndroidId(Context context) {

        String androidId = getSerialNumberCustom();
        if(TextUtils.isEmpty(androidId)){
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        if (Log.isPrint) {
            Log.i(TAG, "ANDROID_ID ：" + androidId);
        }
        androidId = androidId.trim();
        return androidId +"-"+ CHANNEL_ID;
    }

    //SN号custom
    public static String getSerialNumberCustom() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialnocustom");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取头部电机信息
     */
    public static String getHeadMotor() {
        String motorType = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            motorType = (String) get.invoke(c,"ro.head");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return motorType;
    }

    /**
     * 获取脚上电机信息
     */
    public static String getFeetMotor() {
        String motorType = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            motorType = (String) get.invoke(c,"ro.feet");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return motorType;
    }

    /**
     * 获取 开机时间
     */
    public static String getBootTimeString() {
        long ut = SystemClock.elapsedRealtime() / 1000;
        int h = (int) ((ut / 3600));
        int m = (int) ((ut / 60) % 60);
        if (Log.isPrint) {
            Log.i(TAG, h + ":" + m);
        }
        return h + ":" + m;
    }

    public static String printSystemInfo() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        StringBuilder sb = new StringBuilder();
        sb.append("_______  系统信息  ").append(time).append(" ______________");
        sb.append("\nID                 :").append(Build.ID);
        sb.append("\nBRAND              :").append(Build.BRAND);
        sb.append("\nMODEL              :").append(Build.MODEL);
        sb.append("\nRELEASE            :").append(Build.VERSION.RELEASE);
        sb.append("\nSDK                :").append(Build.VERSION.SDK);

        sb.append("\n_______ OTHER _______");
        sb.append("\nBOARD              :").append(Build.BOARD);
        sb.append("\nPRODUCT            :").append(Build.PRODUCT);
        sb.append("\nDEVICE             :").append(Build.DEVICE);
        sb.append("\nFINGERPRINT        :").append(Build.FINGERPRINT);
        sb.append("\nHOST               :").append(Build.HOST);
        sb.append("\nTAGS               :").append(Build.TAGS);
        sb.append("\nTYPE               :").append(Build.TYPE);
        sb.append("\nTIME               :").append(Build.TIME);
        sb.append("\nINCREMENTAL        :").append(Build.VERSION.INCREMENTAL);

        sb.append("\n_______ CUPCAKE-3 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sb.append("\nDISPLAY            :").append(Build.DISPLAY);
        }

        sb.append("\n_______ DONUT-4 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            sb.append("\nSDK_INT            :").append(Build.VERSION.SDK_INT);
            sb.append("\nMANUFACTURER       :").append(Build.MANUFACTURER);
            sb.append("\nBOOTLOADER         :").append(Build.BOOTLOADER);
            sb.append("\nCPU_ABI            :").append(Build.CPU_ABI);
            sb.append("\nCPU_ABI2           :").append(Build.CPU_ABI2);
            sb.append("\nHARDWARE           :").append(Build.HARDWARE);
            sb.append("\nUNKNOWN            :").append(Build.UNKNOWN);
            sb.append("\nCODENAME           :").append(Build.VERSION.CODENAME);
        }

        sb.append("\n_______ GINGERBREAD-9 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sb.append("\nSERIAL             :").append(Build.SERIAL);
        }
        Log.i(TAG, sb.toString());
        return sb.toString();
    }
}
