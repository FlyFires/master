package cn.nineox.robot.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public static String HEABEAT = "INTENT_HEABEAT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == HEABEAT) {
            Log.e("AlarmReceiver", "log log log");
            TUTKManager.getIntance(context).heabeat();

        }

    }

}
