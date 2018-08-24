package cn.nineox.robot.monitor.common.tutk;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ISteeringService;
import android.os.IStepmotorService;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.inner.OnP2PClientListener;
import com.tutk.p2p.inner.OnP2PDeviceListener;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.tools.NetUtils;
import com.yihengke.robotspeech.utils.WriteDataUtils;

import org.greenrobot.eventbus.EventBus;

import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.logic.bean.DeviceLogin;
import cn.nineox.robot.monitor.logic.bean.EventInit;
import cn.nineox.robot.monitor.logic.bean.EventMain;
import cn.nineox.robot.monitor.logic.bean.Peer;
import cn.nineox.robot.monitor.logic.service.ForegroundService;
import cn.nineox.robot.monitor.utils.LogUtil;
import cn.nineox.robot.monitor.utils.SignUtil;
import cn.nineox.robot.monitor.utils.SteeringMonitorUtil;
import cn.nineox.robot.monitor.utils.StepMonitorUtil;
import cn.nineox.robot.monitor.utils.StringUtils;
import cn.nineox.xframework.core.common.assist.Network;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.AbstractRequest;
import cn.nineox.xframework.core.common.http.DefaultResponseListener;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.AndroidUtil;
import cn.nineox.xframework.core.common.utils.PackageUtil;

public class TUTKManager {


    private static TUTKManager mInstance;

    private boolean mInit;

    private boolean mIsByDeviceDoubleCall = true;
    private int steerdegree = 50;
    private IStepmotorService iStepmotorService;
    private ISteeringService iSteeringService;
    private final int MONITOR_DELAY = 2;
    private final int MONITOR_DELAY_TIME= 1000;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    login();
                    break;
            }
        }
    };

    private Context mContext;

    public TUTKManager(Context context) {
        mContext = context;
    }

    public synchronized static TUTKManager getIntance(Context context) {
        if (mInstance == null) {
            mInstance = new TUTKManager(context);
        }
        return mInstance;
    }

    public void initAll() {

        /* 1 设置调试模式 */
        TUTKMedia.TK_getInstance().TK_setDebug(true);
        TUTKP2P.TK_getInstance().TK_setDebug(true);

        /* 2 设备登陆 */
        TUTKP2P.TK_getInstance().TK_init();
        Log.e("--", "uid TK_device_login11:" + App.sSelfUID);
        final int result = TUTKP2P.TK_getInstance().TK_device_login(App.sSelfUID, App.DEVICE_PASSWORD);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (result < 0) {
                    new Toastor(mContext).showSingletonToast("登录失败");
                }
            }
        });

        if (result < 0) {
            TUTKP2P.TK_getInstance().TK_device_checkOnLine(App.sSelfUID, 10 * 1000);
        }


        /* 3 作为device登陆自己的uid */
        //DevicePushUtils.register(App.sSelfUID);

        /* 4 mapping 自己的uid */
//        JPushUtils.JPush_TOKEN = JPushInterface.getRegistrationID(mContext);
//        if (JPushUtils.JPush_TOKEN != null) {
//            JPushUtils.register(mContext);
//            JPushUtils.syncMapping(mContext, App.sSelfUID);
//        }

        /* 5 开启service 监听滑动移除动作 */
//        Intent intent = new Intent(mContext, MediaSDKService.class);
//        mContext.startService(intent);

        /* 7 注册p2p监听器 */
        TUTKP2P.TK_getInstance().TK_registerDeviceListener(mOnP2PDeviceListener);
        TUTKP2P.TK_getInstance().TK_registerClientListener(mOnP2PClientListener);

        EventBus.getDefault().post(new EventInit(result));
        mInit = true;
    }

    public void unInitAll() {
        if (!mInit) {
            return;
        }
        /* 2 清除p2p */
        TUTKP2P.TK_getInstance().TK_client_disConnectAll();
        TUTKP2P.TK_getInstance().TK_device_disConnectAll();
        TUTKP2P.TK_getInstance().TK_unRegisterDeviceListener(mOnP2PDeviceListener);
        TUTKP2P.TK_getInstance().TK_unRegisterClientListener(mOnP2PClientListener);
        TUTKP2P.TK_getInstance().TK_device_logout();
        TUTKP2P.TK_getInstance().TK_unInit();
        mInit = false;
    }

    private void initStepMotorService(){
        iStepmotorService = IStepmotorService.Stub.asInterface((IBinder) StepMonitorUtil.getService());
        if (iStepmotorService != null){
            try {
                iStepmotorService.openDev();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private void initSteerMotorService(){
        iSteeringService =ISteeringService.Stub.asInterface((IBinder) SteeringMonitorUtil.getService());
        if (iSteeringService != null){
            try {
                iSteeringService.openDev();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private void closeStepMotorService(){
        if (iStepmotorService!=null){
            try {
                iStepmotorService.closeDev();
                iStepmotorService=null;
                LogUtil.debug("iStepmotorService.closeDev()");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private void closeSteerMotorService(){
        if (iSteeringService!=null){
            try {
                iSteeringService.closeDev();
                iSteeringService=null;
                LogUtil.debug("iSteeringService.closeDev()");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private OnP2PDeviceListener mOnP2PDeviceListener = new OnP2PDeviceListener() {
        @Override
        public void receiveOnLineInfo(String s, int result) {
            //retryLogin();
        }

        @Override
        public void receiveIOTCListenInfo(final int sid) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(mContext, "收到client连线 sid = " + sid, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void receiveAvServerStart(final int sid, final int avIndex, final int state) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case TUTKP2P.CONNECTION_STATE_CONNECTED: {
                            //Toast.makeText(mContext, " 连线成功 sid = " + sid + " avIndex = " + avIndex, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case TUTKP2P.CONNECTION_STATE_CONNECT_FAILED: {
                            //Toast.makeText(mContext, " 连线失败 sid = " + sid + " avIndex = " + avIndex, Toast.LENGTH_SHORT).show();

                        }
                        break;
                        default: {

                        }
                    }

                }
            });
        }

        @Override
        public void receiveSessionCheckInfo(int sid, St_SInfo info, int result) {
            if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                    || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {
                LogUtil.debug("监控退出");
                if(AndroidUtil.getHeadMotor().equals("stepmotor")){
                    closeStepMotorService();
                }else if(AndroidUtil.getHeadMotor().equals("steering_pwm")){
                    closeSteerMotorService();
                }
                TUTKP2P.TK_getInstance().TK_device_disConnect(sid);

                for (ConnectInfo accountInfo : App.sConnectList) {
                    if (accountInfo.mByClientConnect && accountInfo.mClientSID == sid) {
                        App.sConnectList.remove(accountInfo);
                        break;
                    }
                }

                //如果是作为设备端监控的话，则不提示
                if (mIsByDeviceDoubleCall) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(mContext, "用户退出", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //当存在notification时 对方掉线了
                //NotificationUtils.stopScheduleTask(sid, true);
            }
        }

        @Override
        public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {
        }

        @Override
        public void receiveIOCtrlDataInfo(final int sid, final int avIOCtrlMsgType, byte[] data) {
            switch (avIOCtrlMsgType) {
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_REQ: {
//
                    final CustomCommand.SMsgAVIoctrlCallReq.Struct
                            struct = CustomCommand.SMsgAVIoctrlCallReq.parseToStruct(data);
                    mIsByDeviceDoubleCall = (struct.callType == 1);

                }
                break;
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_END: {

                    TUTKP2P.TK_getInstance().TK_device_disConnect(sid);
                    for (ConnectInfo accountInfo : App.sConnectList) {
                        if (accountInfo.mByClientConnect && accountInfo.mClientSID == sid) {
                            App.sConnectList.remove(accountInfo);
                            break;
                        }
                    }
                    //如果是作为设备端监控的话，则不提示
                    if (mIsByDeviceDoubleCall) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, mContext.getString(R.string.text_user_exit), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                    //当存在notification时 对方挂断了
                    //NotificationUtils.stopScheduleTask(sid, true);

                }
                break;
                case CustomCommand.HEAD_MOTOR_BACK:
                case CustomCommand.HEAD_MOTOR_FORWARD:
                case CustomCommand.HEAD_MOTOR_LEFT:
                case CustomCommand.HEAD_MOTOR_RIGHT:
                case CustomCommand.HEAD_MOTOR_STOP:

//                    Log.e("hhhhhhhhaaaaaa", "native_ear_light_control:" + avIOCtrlMsgType);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                Log.d("===========", "我接到命令是" + avIOCtrlMsgType);
                                if(AndroidUtil.getHeadMotor().equals("steering_pwm")){
                                    try {
                                        if (iSteeringService == null) {
                                            Log.e("=========", "steering_pwm服务为空，无法转动！");
                                            initSteerMotorService();
                                            return;
                                        } Log.e("=========", "iSteeringService.getPosition()"+iSteeringService.getPosition());
                                        if (avIOCtrlMsgType == CustomCommand.HEAD_MOTOR_RIGHT) {
                                            Log.e("=========", "HEAD_MOTOR_RIGHT!");
                                            steerdegree = iSteeringService.getPosition() +5;
                                            if(steerdegree>100){
                                                steerdegree = 100;
                                            }
                                            iSteeringService.rotate(steerdegree,200);
                                        }else if (avIOCtrlMsgType == CustomCommand.HEAD_MOTOR_LEFT) {
                                            Log.e("=========", "HEAD_MOTOR_LEFT！");
                                            steerdegree = iSteeringService.getPosition() -5;
                                            if(steerdegree<0){
                                                steerdegree = 0;
                                            }
                                            iSteeringService.rotate(steerdegree,200);
                                        }else if (avIOCtrlMsgType == CustomCommand.HEAD_MOTOR_STOP) {
                                            // iSteeringService.closeDev();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                               }

                                else if(AndroidUtil.getHeadMotor().equals("stepmotor")){
                                    try {
                                      //  WriteDataUtils.native_ear_light_control(0, CustomCommand.toCotrollCmd(avIOCtrlMsgType), 0);
                                        if (iStepmotorService == null) {
                                            initStepMotorService();
                                            Log.e("=========", "stepmotor服务为空，无法转动！");
                                            return;
                                        }
                                        if (avIOCtrlMsgType == CustomCommand.HEAD_MOTOR_RIGHT) {
                                            iStepmotorService.rotateNegative();
                                        }else if (avIOCtrlMsgType == CustomCommand.HEAD_MOTOR_LEFT) {
                                            iStepmotorService.rotatePositive();
                                        }else if (avIOCtrlMsgType == CustomCommand.HEAD_MOTOR_STOP) {
                                            iStepmotorService.stop();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                        });

                    break;
                case CustomCommand.FEET_MOTOR_BACK:
                case CustomCommand.FEET_MOTOR_FORWARD:
                case CustomCommand.FEET_MOTOR_LEFT:
                case CustomCommand.FEET_MOTOR_RIGHT:
                case CustomCommand.FEET_MOTOR_STOP:

                    LogUtil.debug("native_ear_light_control:" + avIOCtrlMsgType);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mHandler.removeCallbacks(stopMonitor);
                              WriteDataUtils.native_ear_light_control(0, CustomCommand.toCotrollCmd(avIOCtrlMsgType), 0);
                                mHandler.postDelayed(stopMonitor,MONITOR_DELAY_TIME);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    break;
                case CustomCommand.IOTYPE_USER_IPCAM_RESRESH_APP:
                    Log.e("--", "IOTYPE_USER_IPCAM_RESRESH_APP");
                    EventBus.getDefault().post(new EventMain());
                    break;
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_ING: {
                    new Toastor(mContext).showSingletonToast("正在通话中");
                }
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

    //OnP2PDeviceListener end ================//

    private OnP2PClientListener mOnP2PClientListener = new OnP2PClientListener() {
        @Override
        public void receiveConnectInfo(String uid, final int sid, final int state) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case TUTKP2P.CONNECTION_STATE_CONNECTED: {
                            //Toast.makeText(mContext, "连接成功 sid " + sid, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case TUTKP2P.CONNECTION_STATE_CONNECT_FAILED: {
                            //Toast.makeText(mContext, "连接失败 sid " + sid, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        default: {

                        }
                    }
                }
            });
        }

        @Override
        public void receiveClientStartInfo(final String uid, final int avIndex, final int state) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case TUTKP2P.CONNECTION_STATE_CONNECTED: {
                            //Toast.makeText(mContext, "连接成功 avIndex " + avIndex, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case TUTKP2P.CONNECTION_STATE_CONNECT_FAILED: {
                            //Toast.makeText(mContext, "连接失败 avIndex " + avIndex, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        default: {

                        }
                    }
                }
            });
        }

        @Override
        public void receiveSessionCheckInfo(String uid, St_SInfo info, int result) {
            if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                    || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {

                TUTKP2P.TK_getInstance().TK_client_disConnect(uid);
                for (ConnectInfo accountInfo : App.sConnectList) {
                    if (!accountInfo.mByClientConnect && accountInfo.mDeviceUID.equalsIgnoreCase(uid)) {
                        App.sConnectList.remove(accountInfo);
                        break;
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mContext, mContext.getString(R.string.text_user_exit), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

        @Override
        public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int i) {

        }

        @Override
        public void receiveIOCtrlDataInfo(final String uid, int avChannel, final int avIOCtrlMsgType, byte[] data) {
            switch (avIOCtrlMsgType) {
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP: {
                    CustomCommand.SMsgAVIoctrlCallResp.Struct struct = CustomCommand.SMsgAVIoctrlCallResp.parseToStruct(data);
                    int answer = struct.answer;
                    if (answer == 1) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(mContext, " uid " + uid + " device 已接听 ", Toast.LENGTH_SHORT).show();
                            }
                        });

                        ConnectInfo accountInfo = new ConnectInfo();
                        accountInfo.mByClientConnect = false;
                        accountInfo.mDeviceUID = uid;
                        accountInfo.mDeviceChannel = TUTKP2P.DEFAULT_CHANNEL;
                        accountInfo.mAccountId = struct.myID;
                        String nickNameByID = StringUtils.getNickNameByID(struct.myID);
                        accountInfo.mNickName = nickNameByID == null ? struct.myID : nickNameByID;
                        App.sConnectList.add(accountInfo);

                    } else {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, mContext.getString(R.string.text_user_reject), Toast.LENGTH_SHORT).show();
                            }
                        });

                        TUTKP2P.TK_getInstance().TK_client_disConnect(uid);
                    }
                }
                break;
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_END:

                    TUTKP2P.TK_getInstance().TK_client_disConnect(uid);

                    for (ConnectInfo accountInfo : App.sConnectList) {
                        if (!accountInfo.mByClientConnect && accountInfo.mDeviceUID.equalsIgnoreCase(uid)) {
                            App.sConnectList.remove(accountInfo);
                            break;
                        }
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(mContext, mContext.getString(R.string.text_user_exit), Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case CustomCommand.FEET_MOTOR_BACK:
                case CustomCommand.FEET_MOTOR_FORWARD:
                case CustomCommand.FEET_MOTOR_LEFT:
                case CustomCommand.FEET_MOTOR_RIGHT:
                case CustomCommand.FEET_MOTOR_STOP:

                    LogUtil.debug("native_ear_light_control:" + avIOCtrlMsgType);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                    mHandler.removeCallbacks(stopMonitor);
                                    WriteDataUtils.native_ear_light_control(0, CustomCommand.toCotrollCmd(avIOCtrlMsgType), 0);
                                    mHandler.postDelayed(stopMonitor,MONITOR_DELAY_TIME);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;
                case CustomCommand.IOTYPE_USER_IPCAM_RESRESH_APP:
                    Log.e("--", "IOTYPE_USER_IPCAM_RESRESH_APP");
                    EventBus.getDefault().post(new EventMain());
                    break;
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_ING: {
                    new Toastor(mContext).showSingletonToast("正在通话中");
                }
                default: {
                    break;
                }
            }
        }

        @Override
        public void sendIOCtrlDataInfo(String s, int i, int i1, int i2, byte[] bytes) {

        }


        @Override
        public void receiveVideoInfo(String uid, int avChannel, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {

        }

        @Override
        public void receiveAudioInfo(String s, int i, byte[] bytes, long l, int i1) {

        }

    };
    Runnable stopMonitor = new Runnable() {
        @Override
        public void run() {
            WriteDataUtils.native_ear_light_control(0, CustomCommand.toCotrollCmd(CustomCommand.FEET_MOTOR_STOP), 0);
            LogUtil.debug("native_ear_light_control 没有收到命令 停止电机");
        }
    };

    public void deviceLogin() {
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessage(1);
    }

    private void login() {
        LogUtil.debug("login "+Const.DEVICE_LOGIN);
        final EntityRequest request = new EntityRequest(Const.DEVICE_LOGIN, DeviceLogin.class);
        request.add("deviceId", AndroidUtil.getAndroidId(mContext));
        request.add("clientVersion", PackageUtil.getAppVersionName(mContext));
        request.add("osVersion", Build.VERSION.SDK_INT);
        request.add("channelId", App.CHANNEL_ID);
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        String str = AndroidUtil.getAndroidId(mContext) + "&" + timestamp +
                "&" + AndroidUtil.getAndroidId(mContext) + "&" + PackageUtil.getAppVersionName(mContext) +
                "&" + Build.VERSION.SDK_INT + "&" + App.CHANNEL_ID;
        Log.e("--", SignUtil.getSign(str) + "     " + str);
        request.addHeader("sign", SignUtil.getSign(str));
        execute(request, new ResponseListener<DeviceLogin>() {
            @Override
            public void onSucceed(int what, Result<DeviceLogin> result) {
                super.onSucceed(what, result);
                App.deviceLogin = result.getResult();
                getMyPeerUid(App.getToken());
                startForegroundService();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                if (error.contains("请联系商家注册")) {
                    new Toastor(mContext).showSingleLongToast(error);
                } else {
                    LogUtil.debug("error"+error);
                    retryLogin();
                }

            }
        });
    }


    private void getMyPeerUid(String token) {
        Log.e("http", "getMyPeer:" + token);
        EntityRequest request = new EntityRequest(Const.GET_MY_PEERUID, Peer.class);
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        request.addHeader("token", token);
        request.addHeader("deviceId", AndroidUtil.getAndroidId(mContext));
        Log.e("http", "device:" + AndroidUtil.getAndroidId(mContext));
        String str = AndroidUtil.getAndroidId(mContext) + "&" + token + "&" + timestamp;
        request.addHeader("sign", SignUtil.getSign(str));
        execute(request, new ResponseListener<Peer>() {
            @Override
            public void onSucceed(int what, Result<Peer> result) {
                super.onSucceed(what, result);
                if (result.getResult() != null && !TextUtils.isEmpty(result.getResult().getPeerUid())) {
                    //App.sSelfUID = "DZKU85J4U5B4BNPGY1CJ";//result.getResult().getPeerUid();
                    App.sSelfUID = result.getResult().getPeerUid();
                    if (mInit) {
                        unInitAll();
                    }
                    initAll();
                }
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mContext).showSingletonToast(error);
            }
        });
    }


    private void retryLogin() {
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }


    private void startForegroundService() {
        Intent innerIntent = new Intent(mContext, ForegroundService.class);
        mContext.startService(innerIntent);
    }

    /**
     * 异步请求
     *
     * @param request
     * @param listener
     * @param <T>
     */
    protected <T> void execute(AbstractRequest<T> request, ResponseListener<T> listener) {
        NoHttp.getRequestQueueInstance().add(0, request, new DefaultResponseListener<T>(request, listener));
    }

}
