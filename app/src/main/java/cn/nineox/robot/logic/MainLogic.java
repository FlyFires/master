package cn.nineox.robot.logic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.inner.OnP2PClientListener;
import com.tutk.p2p.inner.OnP2PDeviceListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.nineox.robot.BuildConfig;
import cn.nineox.robot.PluginAPP;
import cn.nineox.robot.R;
import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.tutk.ConnectInfo;
import cn.nineox.robot.common.tutk.CustomCommand;
import cn.nineox.robot.common.tutk.LiveViewReceiver;
import cn.nineox.robot.common.tutk.NotificationUtils;
import cn.nineox.robot.databinding.ActivityMainBinding;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.bean.Peer;
import cn.nineox.robot.logic.bean.UserChangeEvent;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.MainActivity;
import cn.nineox.robot.ui.activity.CallActivity;
import cn.nineox.robot.ui.activity.LoginActivity;
import cn.nineox.robot.ui.activity.RobotPreviewActivity;
import cn.nineox.robot.ui.activity.WaitCallActivity;
import cn.nineox.robot.utils.LogUtil;
import cn.nineox.robot.utils.WindowUtils;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.update.UpdateManager;
import cn.nineox.xframework.core.common.utils.DisplayUtil;
import cn.nineox.xframework.core.common.utils.PackageUtil;
import cn.nineox.xframework.core.common.utils.TelephoneUtil;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by me on 18/5/19.
 */

public class MainLogic extends BasicLogic<ActivityMainBinding> {

    private LiveViewReceiver mLiveViewBroadcast;

    private BroadcastReceiver mTokenReceiver;

    private boolean mInit = false;

    private ScheduledExecutorService mExecutorService = Executors.newScheduledThreadPool(1);

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    login();
                    break;
                case 2:
                    heabeat();
                    break;
            }
        }
    };

    public MainLogic(Activity activity, ActivityMainBinding dataBinding) {
        super(activity, dataBinding);

        getMyPeerUid();

        mExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Log.e("--", "heabeat");
                //heabeat();
                mHandler.removeMessages(2);
                mHandler.sendEmptyMessageDelayed(2, 2000);

            }
        }, 120, 120, TimeUnit.SECONDS);


        mTokenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String ation = intent.getAction();

                Log.e("--", "Receiver:" + ation);
                if (ation.equals(Const.ACTION_ERROR_CODE_INVALID_TOKEN)) {
                    mHandler.removeMessages(1);
                    mHandler.sendEmptyMessageDelayed(1, 2000);
                } else {
                    if (intent.getExtras() == null) {
                        return;
                    }
                    String mEventList = intent.getExtras().getString("eventList", "");
                    UserChangeEvent event = new UserChangeEvent();
                    event.setEventList(mEventList);
                    EventBus.getDefault().post(event);
                }

            }
        };
        IntentFilter filter = new IntentFilter(Const.ACTION_ERROR_CODE_INVALID_TOKEN);
        filter.addAction(Const.ACTION_USER_CHANGGE);
        mActivity.registerReceiver(mTokenReceiver, filter);

    }


    public void initAll() {
        /* 1 设置调试模式 */
        TUTKMedia.TK_getInstance().TK_setDebug(BuildConfig.DEBUG);
        TUTKP2P.TK_getInstance().TK_setDebug(BuildConfig.DEBUG);

        /* 2 设备登陆 */
        TUTKP2P.TK_getInstance().TK_init();
        Log.e("--", "init:" + App.sSelfUID + "   " + App.DEVICE_PASSWORD);
        if (TextUtils.isEmpty(App.sSelfUID)) {
            return;
        }
        final int result = TUTKP2P.TK_getInstance().TK_device_login(App.sSelfUID, App.DEVICE_PASSWORD);

        ((Activity) mActivity).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (result < 0) {
                    new Toastor(mActivity).showSingletonToast("登录失败");
                }
            }
        });
        if (result < 0) {
            TUTKP2P.TK_getInstance().TK_device_checkOnLine(App.sSelfUID, 10 * 1000);
        }

        /* 6 注册广播 监听来电提醒 */
        mLiveViewBroadcast = new LiveViewReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.ACTION_CALL_CLICK);
        intentFilter.addAction(App.ACTION_CALL_REMOVE);
        mActivity.registerReceiver(mLiveViewBroadcast, intentFilter);

        /* 3 作为device登陆自己的uid */
        //DevicePushUtils.register(App.sSelfUID);

        /* 4 mapping 自己的uid */
//        JPushUtils.JPush_TOKEN = JPushInterface.getRegistrationID(mActivity);
//        if (JPushUtils.JPush_TOKEN != null) {
//            JPushUtils.register(mActivity);
//            JPushUtils.syncMapping(mActivity, App.sSelfUID);
//        }

        /* 5 开启service 监听滑动移除动作 */
//        Intent intent = new Intent(mActivity, MediaSDKService.class);
//        mActivity.startService(intent);

        /* 7 注册p2p监听器 */
        TUTKP2P.TK_getInstance().TK_registerDeviceListener(mOnP2PDeviceListener);
        TUTKP2P.TK_getInstance().TK_registerClientListener(mOnP2PClientListener);
        mInit = true;

    }


    public void destory() {
        unInitAll();
        if (mTokenReceiver != null) {
            try {
                mActivity.unregisterReceiver(mTokenReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void unInitAll() {
        if (!mInit) {
            return;
        }
        /* 1 清除链表 */
        App.sConnectList.clear();
        /* 2 清除p2p */
        TUTKP2P.TK_getInstance().TK_client_disConnectAll();
        TUTKP2P.TK_getInstance().TK_device_disConnectAll();
        TUTKP2P.TK_getInstance().TK_unRegisterDeviceListener(mOnP2PDeviceListener);
        TUTKP2P.TK_getInstance().TK_unRegisterClientListener(mOnP2PClientListener);
        TUTKP2P.TK_getInstance().TK_device_logout();
        TUTKP2P.TK_getInstance().TK_unInit();
        /* 3 反注册广播 */
        if (mLiveViewBroadcast != null) {
            try {
                mActivity.unregisterReceiver(mLiveViewBroadcast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mInit = false;

    }

    private OnP2PDeviceListener mOnP2PDeviceListener = new OnP2PDeviceListener() {
        @Override
        public void receiveOnLineInfo(String s, int i) {
            Log.e("--", "receiveOnLineInfo:" + s + "  " + i);
            if (i < 0 && App.sSelfUID.equals(s)) {
                unInitAll();
                getMyPeerUid();
            }
        }

        @Override
        public void receiveIOTCListenInfo(final int sid) {
            ((Activity) mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(mActivity, "收到client连线 sid = " + sid, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void receiveAvServerStart(final int sid, final int avIndex, final int state) {
            ((Activity) mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case TUTKP2P.CONNECTION_STATE_CONNECTED: {
                            //Toast.makeText(mActivity, " 连线成功 sid = " + sid + " avIndex = " + avIndex, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case TUTKP2P.CONNECTION_STATE_CONNECT_FAILED: {
                            //Toast.makeText(mActivity, " 连线失败 sid = " + sid + " avIndex = " + avIndex, Toast.LENGTH_SHORT).show();

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

                TUTKP2P.TK_getInstance().TK_device_disConnect(sid);

                for (ConnectInfo accountInfo : App.sConnectList) {
                    if (accountInfo.mByClientConnect && accountInfo.mClientSID == sid) {
                        App.sConnectList.remove(accountInfo);
                        break;
                    }
                }

                ((Activity) mActivity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, mActivity.getString(R.string.text_user_exit), Toast.LENGTH_SHORT).show();
                    }
                });

                //当存在notification时 对方掉线了
                NotificationUtils.stopScheduleTask(sid, true);
            }
        }

        @Override
        public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {

        }

        @Override
        public void receiveIOCtrlDataInfo(final int sid, int avIOCtrlMsgType, byte[] data) {
            switch (avIOCtrlMsgType) {
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_REQ: {

                    final CustomCommand.SMsgAVIoctrlCallReq.Struct
                            struct = CustomCommand.SMsgAVIoctrlCallReq.parseToStruct(data);

                /* 1 如果自己是主讲人 直接拒绝 */
                    if (App.sSelfAccountID.equals(App.sInviteAccountID)) {

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
                    boolean foregroundPortrait = topActivity != null && topActivity instanceof RobotPreviewActivity;
                    boolean foregroundLandscape = topActivity != null && topActivity instanceof CallActivity;
                    boolean foregroundWaitCall = topActivity != null && topActivity instanceof WaitCallActivity;
                    /* 3 如果处于CallUser则需要弹框 不能直接连 */
                    if (foregroundWaitCall) {

//                        showNotification(mActivity, sid, struct.myID,
//                                struct.myUID, struct.callType, struct.otherUID);
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

                        if (foregroundPortrait || foregroundLandscape) {
//                            showNotification(mActivity, sid, struct.myID,
//                                    struct.myUID, struct.callType, struct.otherUID);

                            //正在通话中
                            TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(sid,
                                    CustomCommand.IOTYPE_USER_IPCAM_CALL_ING,
                                    CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));

                            return;
                        }
                    } else {
                        throw new UnknownError("command error invited maybe 1 or 0");
                    }

                    wakeUpAndUnlock();
                    /* 5 添加account信息 */
                    ConnectInfo accountInfo = new ConnectInfo();
                    accountInfo.mByClientConnect = true;
                    accountInfo.mClientSID = sid;
                    accountInfo.mAccountId = struct.myID;
                    //String nickNameByID = StringUtils.getNickNameByID(struct.myID);
                    String nickNameByID = struct.myID;
                    accountInfo.mNickName = nickNameByID == null ? struct.myID : nickNameByID;
                    accountInfo.mDeviceUID = struct.myUID;
                    App.sConnectList.add(accountInfo);

                    Log.e("--", "receiveIOCtrlDataInfo deviceId:" + accountInfo.mDeviceUID);

                    /* 7 如果自己闲置状态 则跳转页面 并且连接其它uid */
                    Intent intent = new Intent(mActivity, WaitCallActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(App.BUNDLE_INFO_LIST, struct.otherUID);
                    bundle.putInt(App.BUNDLE_SID, sid);
                    bundle.putString(App.BUNDLE_ACCOUNT_ID, struct.myID);
                    bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, struct.callType == 1);
                    Log.e("--", "  App.BUNDLE_ACCOUNT_ID:" + struct.myID + "   otheUid:" + struct.otherUID);
                    bundle.putBoolean(App.BUNDLE_IS_CALL_TO, false);
                    intent.putExtras(bundle);


                    //让intent result可以回到fragment处理
//                    MainActivity activity = (MainActivity) mActivity;
//                    BaseFragment fragment = FragmentFactory.createFragment(activity.mCurrentItem);
//                    fragment.startActivityForResult(intent, App.INTENT_REQUEST_CALL_BY);
                    mActivity.getApplicationContext().startActivity(intent);

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

                    ((Activity) mActivity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, mActivity.getString(R.string.text_user_exit), Toast.LENGTH_SHORT).show();
                        }
                    });

                    //当存在notification时 对方挂断了
                    NotificationUtils.stopScheduleTask(sid, true);

                }
                break;
                case CustomCommand.IOTYPE_USER_IPCAM_RESRESH_APP:
                    EventBus.getDefault().post(new UserChangeEvent());
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
    //OnP2PDeviceListener end =========//

    private OnP2PClientListener mOnP2PClientListener = new OnP2PClientListener() {
        @Override
        public void receiveConnectInfo(String uid, final int sid, final int state) {
            ((Activity) mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case TUTKP2P.CONNECTION_STATE_CONNECTED: {
                            //Toast.makeText(mActivity, "连接成功 sid " + sid, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case TUTKP2P.CONNECTION_STATE_CONNECT_FAILED: {
                            //Toast.makeText(mActivity, "连接失败 sid " + sid, Toast.LENGTH_SHORT).show();
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
            ((Activity) mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (state) {
                        case TUTKP2P.CONNECTION_STATE_CONNECTED: {
                            //Toast.makeText(mActivity, "连接成功 avIndex " + avIndex, Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case TUTKP2P.CONNECTION_STATE_CONNECT_FAILED: {
                            //Toast.makeText(mActivity, "连接失败 avIndex " + avIndex, Toast.LENGTH_SHORT).show();
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
            //TODO 8.9
            if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                    || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {

                TUTKP2P.TK_getInstance().TK_client_disConnect(uid);

                for (ConnectInfo accountInfo : App.sConnectList) {
                    if (!accountInfo.mByClientConnect && accountInfo.mDeviceUID.equalsIgnoreCase(uid)) {
                        App.sConnectList.remove(accountInfo);
                        break;
                    }
                }

                ((Activity) mActivity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, mActivity.getString(R.string.text_user_exit), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int i) {

        }

        @Override
        public void receiveIOCtrlDataInfo(final String uid, int avChannel, int avIOCtrlMsgType, byte[] data) {
            switch (avIOCtrlMsgType) {
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP: {
                    CustomCommand.SMsgAVIoctrlCallResp.Struct struct = CustomCommand.SMsgAVIoctrlCallResp.parseToStruct(data);
                    int answer = struct.answer;
                    if (answer == 1) {

                        ((Activity) mActivity).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(mActivity, " uid " + uid + " device 已接听 ", Toast.LENGTH_SHORT).show();
                            }
                        });

                        ConnectInfo accountInfo = new ConnectInfo();
                        accountInfo.mByClientConnect = false;
                        accountInfo.mDeviceUID = uid;
                        accountInfo.mDeviceChannel = TUTKP2P.DEFAULT_CHANNEL;
                        accountInfo.mAccountId = struct.myID;
                        ///String nickNameByID = StringUtils.getNickNameByID(struct.myID);
                        String nickNameByID = struct.myID;
                        accountInfo.mNickName = nickNameByID == null ? struct.myID : nickNameByID;

                        Log.e("--", "receiveIOCtrlDataInfo:" + accountInfo);
                        App.sConnectList.add(accountInfo);

                    } else {

                        ((Activity) mActivity).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mActivity, mActivity.getString(R.string.text_user_reject), Toast.LENGTH_SHORT).show();
                            }
                        });

                        TUTKP2P.TK_getInstance().TK_client_disConnect(uid);
                    }
                }
                break;
                case CustomCommand.IOTYPE_USER_IPCAM_CALL_END: {

                    TUTKP2P.TK_getInstance().TK_client_disConnect(uid);

                    for (ConnectInfo accountInfo : App.sConnectList) {
                        if (!accountInfo.mByClientConnect && accountInfo.mDeviceUID.equalsIgnoreCase(uid)) {
                            App.sConnectList.remove(accountInfo);
                            break;
                        }
                    }

                    ((Activity) mActivity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, mActivity.getString(R.string.text_user_exit), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                case CustomCommand.IOTYPE_USER_IPCAM_RESRESH_APP:
                    EventBus.getDefault().post(new UserChangeEvent());
                    break;
                default: {

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

    /**
     * 获取我的直播uid
     */
    public void getMyPeerUid() {
        LoginInfoBean bean = APPDataPersistent.getInstance().getLoginInfoBean();
        final EntityRequest request = new EntityRequest(Const.GET_MY_PEER_UID, Peer.class);
        long timestamp = System.currentTimeMillis();
        request.addHeader("timestamp", String.valueOf(timestamp));
        String str = bean.getUid() + "&" + bean.getToken() + "&" + timestamp;
        request.addHeader("sign", getSign(str));

        execute(request, new ResponseListener<Peer>() {
            @Override
            public void onSucceed(int what, Result<Peer> result) {
                super.onSucceed(what, result);
                if (result.getResult() != null && !TextUtils.isEmpty(result.getResult().getPeerUid())) {
                 //   App.sSelfUID = "DFYA95J4UTVCBN6GU1E1";//result.getResult().getPeerUid();
                    App.sSelfUID = result.getResult().getPeerUid();
                    App.sSelfAccountID = APPDataPersistent.getInstance().getLoginInfoBean().getUid();
                    if (mInit) {
                        unInitAll();
                    }
                    initAll();
                }
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast(error);
            }

        });
    }

    public void checkUpdate() {
        UpdateManager.setDebuggable(true);
        UpdateManager.setWifiOnly(false);
        UpdateManager.setUrl(Const.UPDATE_CHECK, "android");
        UpdateManager.check(mActivity);
    }


    public void initJPush() {
        String mUserid = APPDataPersistent.getInstance().getLoginInfoBean().getP2PUid();
        JPushInterface.setAlias(mActivity.getApplicationContext(), 1, mUserid);
        HashSet<String> set = new HashSet<String>();
        set.add(mUserid);
        JPushInterface.setTags(mActivity.getApplicationContext(), 1, set);
    }


    private void heabeat() {
        LoginInfoBean loginInfoBean = APPDataPersistent.getInstance().getLoginInfoBean();
        if (TextUtils.isEmpty(loginInfoBean.getToken())) {
            return;
        }

        StringReqeust request = new StringReqeust(Const.HEARBEAT);
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        //request.addHeader("token", loginInfoBean.getToken());
        request.addHeader("userId", loginInfoBean.getUid());
        String str = loginInfoBean.getUid() + "&" + loginInfoBean.getToken() + "&" + timestamp;
        Log.e("http", "heabeat sign str：" + str);
        request.addHeader("sign", getSign(str));
        Log.e("http", "heabeat sign：" + getSign(str));
        execute(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                if (!TextUtils.isEmpty(App.sSelfUID)) {
                    TUTKP2P.TK_getInstance().TK_device_checkOnLine(App.sSelfUID, 10 * 1000);
                }
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                LogUtil.debug("heabeat fail "+error);
            }
        });
    }


    public void login() {
        Log.e("--", "静默登录");
        LoginInfoBean loginInfoBean = APPDataPersistent.getInstance().getLoginInfoBean();
        final EntityRequest request = new EntityRequest(Const.URL_LOGIN, LoginInfoBean.class);
        request.add("mobile", loginInfoBean.getMobile());
        request.add("password", loginInfoBean.getPassword());
        request.add("clientVesion", PackageUtil.getAppVersionName(mActivity));
        request.add("osVesion", Build.VERSION.SDK_INT);
        request.add("imei", TelephoneUtil.getIMEI(mActivity));
        request.add("mobile_model", android.os.Build.MODEL);
        request.add("channelId", App.CHANNEL_ID);
        execute(request, new ResponseListener<LoginInfoBean>() {
            @Override
            public void onSucceed(int what, Result<LoginInfoBean> result) {
                super.onSucceed(what, result);
                APPDataPersistent.getInstance().setLoginInfoBean(result.getResult());
                EventBus.getDefault().post(new UserChangeEvent());
                unInitAll();
                getMyPeerUid();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast(error);
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
    }

    /**
     * 唤醒手机屏幕并解锁
     */
    public static void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) PluginAPP.getInstance().getApplicationContext()
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
        KeyguardManager keyguardManager = (KeyguardManager) PluginAPP.getInstance()
                .getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }

}
