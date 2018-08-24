package cn.nineox.robot.monitor.utils;

import java.security.MessageDigest;

import cn.nineox.xframework.core.android.log.Log;

public class SignUtil {

    public static  String getSign(String str) {
        String sign = "";
        //String str = "12&5e40b46f-a491-4ca9-a2d0-93fbf00ae0e2&1527168795756";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] sha = digest.digest(str.getBytes());
            //sign = HexUtil.encodeHexStr(android.util.Base64.encodeToString(sha, Base64.DEFAULT).getBytes());
            sign = Base64.getEncoder().encodeToString(sha);
            Log.e("--", "sign:" + sign);
        } catch (Exception e) {

        }

        return sign;
    }

}
