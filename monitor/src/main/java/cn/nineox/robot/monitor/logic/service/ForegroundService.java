package cn.nineox.robot.monitor.logic.service;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.libTUTKMedia.utils.MediaCodecUtils;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.inner.OnP2PDeviceListener;
import com.tutk.p2p.utils.P2PUtils;
import com.yanzhenjie.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.nineox.robot.monitor.APP;
import cn.nineox.robot.monitor.CallActivity;
import cn.nineox.robot.monitor.MainActivity;
import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.RobotPreviewActivity;
import cn.nineox.robot.monitor.WaitCallActivity;
import cn.nineox.robot.monitor.WaitCallMonitorView;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.ConnectInfo;
import cn.nineox.robot.monitor.common.tutk.CustomCommand;
import cn.nineox.robot.monitor.logic.MonitorRefreshManager;
import cn.nineox.robot.monitor.logic.bean.EventInit;
import cn.nineox.robot.monitor.logic.bean.EventMain;
import cn.nineox.robot.monitor.logic.bean.EventMainActivity;
import cn.nineox.robot.monitor.utils.LogUtil;
import cn.nineox.robot.monitor.utils.SignUtil;
import cn.nineox.robot.monitor.utils.StringUtils;
import cn.nineox.robot.monitor.utils.WindowUtils;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.DefaultResponseListener;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.utils.AndroidUtil;

/**
 * Created by me on 18/5/5.
 */

public class ForegroundService extends Service implements NetEvent {
    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    //private P2PManager mP2PManager;

    /**
     * 监控网络的广播
     */
    private NetBroadcastReceiver netBroadcastReceiver;

    private BroadcastReceiver mTokenReceiver;


    private String mAccountID;

    private WaitCallMonitorView mWaitCallMonitorView;
    public  boolean mIsSendVideo  = true;//是否传视频
    private long mBitrateDownTime;//降码流时间 避免降低过快
    private static final long BITRATE_DOWN_TIME = 10 * 1000;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    EventBus.getDefault().post(new EventMainActivity());
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        //mP2PManager = P2PManager.getInstance();
        Log.e("ForegroundService", "onCreate");
//        Notification notification = new Notification(R.mipmap.ic_launcher, getText(R.string.app_name),
//                System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(this, getText(R.string.notification_title),
//                getText(R.string.notification_message), pendingIntent);

        Notification notification = new NotificationCompat.Builder(this)
                /**设置通知左边的大图标**/
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                /**设置通知右边的小图标**/
                .setSmallIcon(R.mipmap.ic_launcher)
                /**通知首次出现在通知栏，带上升动画效果的**/
                .setTicker("通知来了")
                /**设置通知的标题**/
                .setContentTitle(getResources().getString(R.string.app_name))
                /**设置通知的内容**/
                .setContentText(getResources().getString(R.string.app_name) + "正在运行中")
                /**通知产生的时间，会在通知信息里显示**/
                .setWhen(System.currentTimeMillis())
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                /**设置该通知优先级**/
                .setPriority(Notification.PRIORITY_DEFAULT).build();
        startForeground(10086, notification);

        //P2PManager.getInstance().turnOn();
        registerNet();
        service.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Log.e("--", "heabeat");
                heabeat();

            }
        }, 120, 120, TimeUnit.SECONDS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("--", "ForegroundService onStartCommand");
        if (mTokenReceiver == null) {
            mTokenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String ation = intent.getAction();

                    android.util.Log.e("--", "Receiver:" + ation);
                    if (ation.equals(Const.ACTION_ERROR_CODE_INVALID_TOKEN)) {
                        mHandler.removeMessages(1);
                        mHandler.sendEmptyMessageDelayed(1, 5000);
                    } else {
                        if (intent.getExtras() == null) {
                            return;
                        }
                        String eventList = intent.getExtras().getString("eventList", "");
                        EventMain event = new EventMain();
                        event.setEventList(eventList);
                        EventBus.getDefault().post(event);
                    }

                }
            };
            IntentFilter filter = new IntentFilter(Const.ACTION_ERROR_CODE_INVALID_TOKEN);
            filter.addAction(Const.ACTION_USER_CHANGGE);
            this.registerReceiver(mTokenReceiver, filter);

        }

        return START_STICKY;

    }

    private void heabeat() {
//        if(TUTKP2P.TK_getInstance().TK_device_checkOnLine()){
//
//        }

        if (!TextUtils.isEmpty(App.sSelfUID)) {
            TUTKP2P.TK_getInstance().TK_device_checkOnLine(App.sSelfUID, 10 * 1000);
        }


        if (TextUtils.isEmpty(App.getToken())) {
            return;
        }

        StringReqeust request = new StringReqeust(cn.nineox.robot.monitor.common.Const.HEARBEAT);
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        request.addHeader("token", App.getToken());
        request.addHeader("deviceId", AndroidUtil.getAndroidId(this));
        android.util.Log.e("http", "device:" + AndroidUtil.getAndroidId(this));
        String str = AndroidUtil.getAndroidId(this) + "&" + App.getToken() + "&" + timestamp;
        request.addHeader("sign", SignUtil.getSign(str));
        NoHttp.getRequestQueueInstance().add(0, request, new DefaultResponseListener<String>(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                Log.e("--", "heabeat:" + result.getResult());
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                LogUtil.debug("heabeat fail "+error);
            }


        }));
    }


    private void registerNet() {
        //注册广播
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netBroadcastReceiver, filter);
            /**
             * 设置监听
             */
            netBroadcastReceiver.setNetEvent(this);
        }
    }


    @Override
    public void onNetChange(int status) {
//        if(status == 0){
//            P2PManager.getInstance().getP2PKit().TurnOff();
//        }else{
//            P2PManager.getInstance().setP2PCallback(callbacks);
//            P2PManager.getInstance().turnOn();
//        }
//        Log.e("--", "onNetChange");
        //P2PManager.getInstance().getP2PKit().Clear();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventInit event) {
        TUTKP2P.TK_getInstance().TK_registerDeviceListener(mOnP2PDeviceListener);
    }


    private OnP2PDeviceListener mOnP2PDeviceListener = new OnP2PDeviceListener() {
        @Override
        public void receiveOnLineInfo(String s, int result) {

            android.util.Log.e("--", "receiveOnLineInfo:" + s + "  " + result);
            if (result < 0 && App.sSelfUID.equals(s)) {
                android.util.Log.e("--", "receiveOnLineInfo:" + s + "  " + result + "  重新登录");
                //EventBus.getDefault().post(new EventMainActivity());

                mHandler.removeMessages(1);
                mHandler.sendEmptyMessageDelayed(1, 2000);
            }

        }

        @Override
        public void receiveIOTCListenInfo(final int sid) {
        }

        @Override
        public void receiveAvServerStart(final int sid, final int avIndex, final int state) {

        }

        @Override
        public void receiveSessionCheckInfo(int sid, St_SInfo info, int result) {

        }

        @Override
        public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {
            //changeDASA(i1);
        }

        @Override
        public void receiveIOCtrlDataInfo(final int sid, int avIOCtrlMsgType, byte[] data) {
            Log.e("--", "Service receiveIOCtrlDataInfo");
            switch (avIOCtrlMsgType) {
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_REQ: {
                    synchronized (this) {
                        final CustomCommand.SMsgAVIoctrlCallReq.Struct
                                struct = CustomCommand.SMsgAVIoctrlCallReq.parseToStruct(data);
//
                    /* 1 如果是主讲人 直接拒绝 */
                        if (App.sHasInvitePermission) {

                            //添加历史记录 -- 别人打过来 弹框 -- 自己拒接
//                        long startTime = System.currentTimeMillis();
//                        HashMap<String, String> map = new HashMap<>(1);
//                        map.put(struct.myID, struct.myUID);
//                        DatabaseManager.addHistoryToServer(startTime, 0, struct.callType, DatabaseManager.CALL_TYPE_INJECT, map);

                            //发送command
                            TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(sid,
                                    CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                                    CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));
                            SystemClock.sleep(100);
                            TUTKP2P.TK_getInstance().TK_device_disConnect(sid);

                            return;
                        }
                    /* 2 判断当前自己处于哪个页面 CallUser/LiveView */
                        Activity topActivity = WindowUtils.getTopActivity();
                        boolean foregroundMonitor = MonitorRefreshManager.isMonitor();//topActivity != null && topActivity instanceof RobotPreviewActivity;
                        boolean foregroundLandscape = topActivity != null && topActivity instanceof CallActivity;
                        boolean foregroundWaitCall = topActivity != null && topActivity instanceof WaitCallActivity;
                    /* 3 如果处于CallUser则需要弹框 不能直接连 */
                        if (foregroundWaitCall || WaitCallActivity.mWaiting) {

//                        showNotification(mActivity, sid, struct.myID,
//                                struct.myUID, struct.callType, struct.otherUID);

                            TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(sid,
                                    CustomCommand.IOTYPE_USER_IPCAM_CALL_ING,
                                    CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));

                            return;
                        }
//					/* 4 判断对面是主动邀请自己还是被动邀请自己 */
                        if (struct.invited == 1) {
                            //被动邀请 这些情况 属于 command数据异常
                            if (!foregroundLandscape || struct.otherUID.size() != 0) {
                                throw new UnknownError("be invited data error");
                            }
                        } else if (struct.invited == 0) {
                            //主动邀请 这些情况 需要 弹框

                            if (foregroundWaitCall || foregroundLandscape) {
//                            showNotification(mActivity, sid, struct.myID,
//                                    struct.myUID, struct.callType, struct.otherUID);
                                Log.e("--", "繁忙");
                                //正在通话中

                                TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(sid,
                                        CustomCommand.IOTYPE_USER_IPCAM_CALL_ING,
                                        CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));
                                return;
                            }
                        } else {
                            throw new UnknownError("command error invited maybe 1 or 0");
                        }

                        if (foregroundMonitor && struct.callType == 1) {
                            //正在通话中
//                        TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(sid,
//                                CustomCommand.IOTYPE_USER_IPCAM_CALL_ING,
//                                CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));
//                        return;
                            //如果正在监控时有电话来，则停止监控
                            MonitorRefreshManager.notifyRefreshListener();
                            SystemClock.sleep(500);
                        }

                        //如果已存在通话
//                    if (App.sConnectList.size() > 0) {
//                        return;
//                    }

                        //if(struct.callType == 1){
                        wakeUpAndUnlock();
                        //}

                        Log.e("--", "Service receiveIOCtrlDataInfo:" + APP.the().activityStack.empty());

                    /* 5 添加account信息 */
                        ConnectInfo accountInfo = new ConnectInfo();
                        accountInfo.mByClientConnect = true;
                        accountInfo.mClientSID = sid;
                        accountInfo.mAccountId = struct.myID;
                        String nickNameByID = StringUtils.getNickNameByID(struct.myID);
                        accountInfo.mNickName = nickNameByID == null ? struct.myID : nickNameByID;
                        accountInfo.mDeviceUID = struct.myUID;
                        App.sConnectList.add(accountInfo);
                        android.util.Log.e("--", "receiveIOCtrlDataInfo:" + accountInfo);
                        mAccountID = accountInfo.mAccountId;
                    /* 7 如果自己闲置状态 则跳转页面 并且连接其它uid */
                        Intent intent = new Intent(ForegroundService.this, WaitCallActivity.class);
                        final Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(App.BUNDLE_INFO_LIST, struct.otherUID);
                        bundle.putInt(App.BUNDLE_SID, sid);
                        bundle.putString(App.BUNDLE_ACCOUNT_ID, struct.myID);
                        android.util.Log.e("--", "  App.BUNDLE_ACCOUNT_ID:" + struct.myID + "   otheUid:" + struct.otherUID);
                        bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, struct.callType == 1);
                        bundle.putBoolean(App.BUNDLE_IS_CALL_TO, false);
                        intent.putExtras(bundle);

                        if (struct.callType == 1) {
                            WaitCallActivity.mWaiting = true;
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intent.putExtras(bundle);
                            //让intent result可以回到fragment处理
//                    MainActivity activity = (MainActivity) mActivity;
//                    BaseFragment fragment = FragmentFactory.createFragment(activity.mCurrentItem);
//                    fragment.startActivityForResult(intent, App.INTENT_REQUEST_CALL_BY);
                            ForegroundService.this.getApplication().startActivity(intent);
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    new WaitCallMonitorView(ForegroundService.this, bundle).show();
//                                if(mWaitCallMonitorView == null){
//                                    mWaitCallMonitorView = new WaitCallMonitorView(ForegroundService.this, bundle);
//                                    mWaitCallMonitorView.show();
//                                }else{
//                                    mWaitCallMonitorView.sLogic.answer(bundle);
//                                }
                                }
                            });

                        }

                    }


                }
                break;
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_END: {
//                    CustomCommand.SMsgAVIoctrlCallEnd.Struct struct = CustomCommand.SMsgAVIoctrlCallEnd.parseToStruct(data);
//
//                    Log.e("--","mWaitCallMonitorView:" + mWaitCallMonitorView  + "  " + struct.myID.equals(mAccountID));
//                    if (struct.myID.equals(mAccountID)) {
//                        //添加历史记录 -- 别人打过来 自己还未接 别人挂断
//                        if(mWaitCallMonitorView != null){
//                            mWaitCallMonitorView.destroy();
//                            mWaitCallMonitorView = null;
//                        }
//
//                    }
                }
                break;
                default: {

                }
            }
        }

        @Override
        public void sendIOCtrlDataInfo(int i, int i1, int i2, byte[] bytes) {

        }


        @Override
        public void receiveVideoInfo(int sid, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {

        }

        @Override
        public void receiveAudioInfo(int i, byte[] bytes, long l, int i1) {

        }

    };


    /**
     * 唤醒手机屏幕并解锁
     */
    public static void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) APP.the().getApplicationContext()
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) APP.the()
                .getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TUTKP2P.TK_getInstance().TK_unRegisterDeviceListener(mOnP2PDeviceListener);
        EventBus.getDefault().unregister(this);
    }


    public void changeDASA(int rttResult) {

		/* 动态码流 */
/*
        if (rttResult == P2PUtils.STATUS_CHECK_UNKNOWN) {
            return;
        }

        //最低10s钟才进行码流调整 避免多路rtt 导致降码流太快
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mBitrateDownTime < BITRATE_DOWN_TIME) {
            return;
        }
        mBitrateDownTime = currentTimeMillis;

        if (rttResult == P2PUtils.STATUS_CHECK_BLOCK) {
            //网络不好降码流 : 1 --> 0.5 --> 0.25 --> 不传视频
            float bitrate = TUTKMedia.TK_getInstance().TK_preview_getBitrate();
            float bitrateDown = -1;
            if (bitrate == MediaCodecUtils.PREVIEW_BITRATE_MIDDLE) {
                bitrateDown = MediaCodecUtils.PREVIEW_BITRATE_LOW;
            } else if (bitrate == MediaCodecUtils.PREVIEW_BITRATE_LOW) {
                bitrateDown = MediaCodecUtils.PREVIEW_BITRATE_LOWEST;
            } else if (bitrate == MediaCodecUtils.PREVIEW_BITRATE_LOWEST && mIsSendVideo) {
                new Toastor(this).showSingleLongToast(R.string.tips_network_block);
                mIsSendVideo = false;
            }

            if (bitrateDown != -1) {
                new Toastor(this).showSingleLongToast(R.string.tips_network_low);
                TUTKMedia.TK_getInstance().TK_preview_changeQuality(
                        TUTKMedia.TK_getInstance().TK_preview_getResolution(),
                        bitrateDown,
                        TUTKMedia.TK_getInstance().TK_preview_getFPS());
            }
        } else if (!mIsSendVideo && rttResult == P2PUtils.STATUS_CHECK_NORMAL) {
            //网络正常恢复视频
            mIsSendVideo = true;
            new Toastor(this).showSingleLongToast(R.string.tips_network_normal);
        }*/
    }
}
