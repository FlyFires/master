package cn.nineox.robot.monitor.logic;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_SInfo;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.inner.OnP2PClientListener;
import com.tutk.p2p.inner.OnP2PDeviceListener;
import com.yihengke.robotspeech.utils.WriteDataUtils;

import org.greenrobot.eventbus.EventBus;

import cn.nineox.robot.monitor.CallActivity;
import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.RobotPreviewActivity;
import cn.nineox.robot.monitor.WaitCallActivity;
import cn.nineox.robot.monitor.WaitCallMonitorView;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.ConnectInfo;
import cn.nineox.robot.monitor.common.tutk.CustomCommand;
import cn.nineox.robot.monitor.common.tutk.LiveViewReceiver;
import cn.nineox.robot.monitor.common.tutk.TUTKManager;
import cn.nineox.robot.monitor.databinding.ActivityMainBinding;
import cn.nineox.robot.monitor.logic.bean.DeviceLogin;
import cn.nineox.robot.monitor.logic.bean.EventInit;
import cn.nineox.robot.monitor.logic.bean.EventMain;
import cn.nineox.robot.monitor.logic.bean.Peer;
import cn.nineox.robot.monitor.logic.service.ForegroundService;
import cn.nineox.robot.monitor.utils.DesUtils;
import cn.nineox.robot.monitor.utils.SharePrefUtil;
import cn.nineox.robot.monitor.utils.SignUtil;
import cn.nineox.robot.monitor.utils.StringUtils;
import cn.nineox.robot.monitor.utils.WindowUtils;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.AndroidUtil;
import cn.nineox.xframework.core.common.utils.AppUtil;
import cn.nineox.xframework.core.common.utils.PackageUtil;

/**
 * Created by me on 18/5/19.
 */

public class MainActivityLogic extends BaseLogic<ActivityMainBinding> {


    private LiveViewReceiver mLiveViewBroadcast;

    private Handler mHandler = new Handler();

    private boolean mInit = false;

    public MainActivityLogic(Activity activity, ActivityMainBinding dataBinding) {
        super(activity, dataBinding);

        //deviceLogin();

        /* 6 注册广播 监听来电提醒 */
        mLiveViewBroadcast = new LiveViewReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.ACTION_CALL_CLICK);
        intentFilter.addAction(App.ACTION_CALL_REMOVE);
        mActivity.registerReceiver(mLiveViewBroadcast, intentFilter);
        //verifyCheck();
    }

    public void unRegisterceiver(){
        mActivity.unregisterReceiver(mLiveViewBroadcast);
    }
    private void verifyCheck() {
        Log.e("licence", "verifyCheck");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String licence = sharedPreferences.getString("licence", "");
        Log.e("licence", licence);
        boolean permission = true;
        if (TextUtils.isEmpty(licence)) {
            permission = true;
        } else {
            try {
                Log.e("licence", "decode permission:" + permission + "  " + DesUtils.decode(licence));
                permission = Boolean.valueOf(DesUtils.decode(licence));
                if (!permission) {
                    showKillTips();
                }
                return;
            } catch (Exception e) {
                permission = false;
            }
        }

        EntityRequest request = new EntityRequest<Boolean>(Const.VERIFY_CHECK, Boolean.class);
        request.add("sn", AndroidUtil.getAndroidId(mActivity));
        request.add("ver", PackageUtil.getAppVersionCode(mActivity));
        execute(request, new ResponseListener<Boolean>() {
            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
                if (result.getResult()) {
                    try {
                        String encode = DesUtils.encode("true");
                        Log.e("licence", "encode permission save:" + encode);
                        sharedPreferences.edit().putString("licence", encode).commit();
                    } catch (Exception e) {

                    }
                } else {
                    try {
                        String encode = DesUtils.encode("true");
                        Log.e("licence", "encode permission save:" + encode);
                        sharedPreferences.edit().putString("licence", encode).commit();
                    } catch (Exception e) {

                    }
                    showKillTips();
                }
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
            }
        });
    }


    private void showKillTips() {

        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("非法程序")
                .create();
        tipDialog.show();
//        tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                mActivity.finish();
//                System.exit(0);
//            }
//        });
//        tipDialog.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                tipDialog.dismiss();
//            }
//        }, 2000);
    }

    public void deviceLogin() {
        TUTKManager.getIntance(mActivity).deviceLogin();
    }


}
