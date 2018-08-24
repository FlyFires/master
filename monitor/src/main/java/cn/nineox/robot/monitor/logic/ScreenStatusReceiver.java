package cn.nineox.robot.monitor.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.nineox.robot.monitor.logic.service.ForegroundService;
import cn.nineox.xframework.core.android.log.Log;

public class ScreenStatusReceiver extends BroadcastReceiver {
    public static String SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static String SCREEN_OFF = "android.intent.action.SCREEN_OFF";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("--","ScreenStatusReceiver:" + intent.getAction());
        Intent intent1 = new Intent(context, ForegroundService.class);
        //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent1);
        if (SCREEN_ON.equals(intent.getAction())) {

        }
        else if (SCREEN_OFF.equals(intent.getAction())) {
        }
    }
}
