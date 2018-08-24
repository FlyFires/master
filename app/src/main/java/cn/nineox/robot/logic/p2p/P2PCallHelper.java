package cn.nineox.robot.logic.p2p;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import cn.nineox.robot.common.Const;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.activity.CallActivity;

/**
 * Created by me on 17/11/12.
 */

public class P2PCallHelper {//implements RTP2PCallHelper

    private Activity activity;


    private AlertDialog dialog;

    public P2PCallHelper(Activity activity) {
        this.activity = activity;

    }

    public void onConnected() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "连接成功！", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onDisconnect(int nErrCode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "连接断开！", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onRTCMakeCall(final String strPeerUserId, final int nCallMode, String strUserData) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity.getApplicationContext(), CallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //监看模式和视频Pro模式下直接跳转至P2PActivity
                Bundle bundle = new Bundle();
                bundle.putString("userid", APPDataPersistent.getInstance().getLoginInfoBean().getUid());
                bundle.putString("callid", strPeerUserId);
                bundle.putBoolean("p2p_push", false);
                bundle.putBoolean("is_host", false);
                bundle.putInt("p2p_mode", nCallMode);
                DeviceBean bean = new DeviceBean();
                bean.setMidName(strPeerUserId);
                bean.setMid(strPeerUserId);
                bean.setId(strPeerUserId);
                bundle.putSerializable(Const.EXTRA_DEVICE, bean);
                intent.putExtras(bundle);
                activity.startActivity(intent);
//                ShowDialog(activity, strPeerUserId, nCallMode);
            }
        });
    }

    public void onRTCAcceptCall(String strPeerUserId) {

    }

    public void onRTCRejectCall(String strPeerUserId, int errCode) {

    }

    public void onRTCEndCall(String strPeerUserId, int errCode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "对方已挂断！", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onRTCSwithToAudioMode() {

    }

    public void onRTCUserMessage(String strPeerUserId, String strMessage) {

    }

    public void onRTCOpenVideoRender(String strDevId) {

    }

    public void onRTCCloseVideoRender(String strDevId) {

    }


    /**
     * P2P呼叫
     *
     * @param context
     * @param strPeerUserId
     */
    private void ShowDialog(final Context context, final String strPeerUserId, final int callMode) {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
//        AlertDialog.Builder build = new AlertDialog.Builder(context);
//        build.setTitle("P2P 呼叫");
//        build.setCancelable(false);
//        build.setPositiveButton("同意", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                Bundle bundle = new Bundle();
//                bundle.putString("userid", APPDataPersistent.getInstance().getLoginInfoBean().getP2PUid());
//                bundle.putString("callid", strPeerUserId);
//                bundle.putBoolean("p2p_push", false);
//                bundle.putBoolean("is_host", false);
//                bundle.putInt("p2p_mode", callMode);
//                DeviceBean bean = new DeviceBean();
//                bean.setMidName(strPeerUserId);
//                bean.setId(strPeerUserId);
//                bundle.putSerializable(Const.EXTRA_DEVICE,bean);
//                Intent intent = new Intent();
//                intent.putExtras(bundle);
//                intent.setClass(context, CallActivity.class);
//                context.startActivity(intent);
//            }
//        });
//        build.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                /**
//                 * 拒绝呼叫
//                 */
//                PluginAPP.getInstance().getP2pKit().RejectCall(strPeerUserId);
//            }
//        });
//
//        dialog = build.show();
    }
}
