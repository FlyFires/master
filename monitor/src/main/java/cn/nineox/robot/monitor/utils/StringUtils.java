package cn.nineox.robot.monitor.utils;

import java.util.Calendar;

public class StringUtils {


    public static String getFileNameWithTime() {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSec = c.get(Calendar.SECOND);

        StringBuffer sb = new StringBuffer();
        sb.append(mYear);
        if (mMonth < 10) {
            sb.append('0');
        }
        sb.append(mMonth);
        if (mDay < 10) {
            sb.append('0');
        }
        sb.append(mDay);
        sb.append('_');
        if (mHour < 10) {
            sb.append('0');
        }
        sb.append(mHour);
        if (mMinute < 10) {
            sb.append('0');
        }
        sb.append(mMinute);
        if (mSec < 10) {
            sb.append('0');
        }
        sb.append(mSec);

        return sb.toString();
    }

    public static String getNickNameByID(String id){
        return id;
    }
}
