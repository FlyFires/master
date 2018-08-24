package cn.nineox.robot.monitor.logic;

/**
 * Created by me on 18/5/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import cn.nineox.xframework.core.android.log.Log;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("--","NetworkReceiver");
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        //如果无网络连接activeInfo为null
        //也可获取网络的类型
        if (activeInfo != null) { //网络连接
            //P2PManager.getInstance().turnOn();
        } else { //网络断开

        }
    }
}