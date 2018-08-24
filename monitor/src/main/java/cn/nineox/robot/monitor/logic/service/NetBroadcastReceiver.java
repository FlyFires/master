package cn.nineox.robot.monitor.logic.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.yanzhenjie.nohttp.tools.NetUtils;

public class NetBroadcastReceiver extends BroadcastReceiver {

    private NetEvent netEvent;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //检查网络状态的类型
            if (NetUtils.isNetworkAvailable()){
                // 接口回传网络状态的类型
                netEvent.onNetChange(1);
            }else{
                netEvent.onNetChange(0);
            }

        }
    }

    public void setNetEvent(NetEvent netEvent) {
        this.netEvent = netEvent;
    }

}