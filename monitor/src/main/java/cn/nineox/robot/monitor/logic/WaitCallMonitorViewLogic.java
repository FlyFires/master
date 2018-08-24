package cn.nineox.robot.monitor.logic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.inner.OnP2PClientListener;
import com.tutk.p2p.inner.OnP2PDeviceListener;

import java.util.ArrayList;

import cn.nineox.robot.monitor.MonitorView;
import cn.nineox.robot.monitor.WaitCallMonitorView;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.CustomCommand;
import cn.nineox.robot.monitor.common.tutk.ThreadAutoConnect;
import cn.nineox.robot.monitor.common.tutk.ThreadTimerClock;
import cn.nineox.xframework.core.android.log.Log;


public class WaitCallMonitorViewLogic implements OnP2PClientListener, OnP2PDeviceListener {

    private ThreadTimerClock mThreadTotalTimer;

    private WaitCallMonitorView mWaitCallMonitorView;

    private Context mContext;

    private Bundle mBundle;

    private MonitorView mMonitorView;

    public WaitCallMonitorViewLogic(WaitCallMonitorView view, Bundle bundle) {
        this.mWaitCallMonitorView = view;
        this.mContext = view.getContext();
        this.mBundle = bundle;

    }


    private int mCallBySID = -1;

    private String mAccountID;//对方id
    private String mCallUID;//对方UID

    private boolean mIsDoubleCall;//是否双向
    private boolean mIsCallTo;//是否主动打过去

    private ThreadAutoConnect mThreadAutoConnect;//自动连线线程

    private ArrayList<CustomCommand.StructAccountInfo> mOtherUIDList;//其它uid

    private MediaPlayer mMediaPlayer;

    public void initData() {
        bind();
        Bundle bundle = mBundle;
        if (bundle != null) {
            mOtherUIDList = bundle.getParcelableArrayList(App.BUNDLE_INFO_LIST);//may be null
            mAccountID = bundle.getString(App.BUNDLE_ACCOUNT_ID);
            mCallUID = bundle.getString(App.BUNDLE_CALL_UID);//may be null
            mCallBySID = bundle.getInt(App.BUNDLE_SID, -1);//may be -1
            mIsCallTo = bundle.getBoolean(App.BUNDLE_IS_CALL_TO);
            mIsDoubleCall = bundle.getBoolean(App.BUNDLE_IS_DOUBLE_CALL);
            Log.e("--", "mAccountID:" + mAccountID + "  mCallUID:" + mCallUID);
            if (mIsCallTo) {
                callTo();
            }


            if (!mIsCallTo && !mIsDoubleCall) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        answer();
                    }
                }, 1500);

            } else {
                //startRing();
            }


        }
    }

    public void bind() {
        TUTKP2P.TK_getInstance().TK_registerClientListener(this);
        TUTKP2P.TK_getInstance().TK_registerDeviceListener(this);
    }


    private void callTo() {
        /* 2 30s之内不断重连设备 */
        mThreadAutoConnect = new ThreadAutoConnect(mCallUID, App.sSelfAccountID, App.sSelfUID,
                mIsDoubleCall, null);
        mThreadAutoConnect.start();
        mThreadAutoConnect.setThreadListener(new ThreadAutoConnect.OnThreadListener() {
            @Override
            public void timeOut() {
                /* 3 添加历史记录 -- 主动打过去 对方未接 */
                mWaitCallMonitorView.post(new Runnable() {
                    @Override
                    public void run() {
                        //new Toastor(mActivity).showSingletonToast(mActivity.getString(R.string.text_history_missed));
                        //((Activity) mActivity).finish();
                        mWaitCallMonitorView.destroy();
                    }
                });

            }
        });
    }

    @Override
    public void receiveIOCtrlDataInfo(final String uid, int avChannel, int avIOCtrlMsgType, byte[] data) {

        switch (avIOCtrlMsgType) {
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP: {
                CustomCommand.SMsgAVIoctrlCallResp.Struct struct = CustomCommand.SMsgAVIoctrlCallResp.parseToStruct(data);
                int answer = struct.answer;

                if (answer == 1) {
                    if(mMonitorView == null){
                        mMonitorView = new MonitorView(mContext);
                    }
                    mMonitorView.show();
                } else {
                    //添加历史记录 -- 主动打过去 对方拒接
                }
                mWaitCallMonitorView.destroy();

            }
            break;
            default: {

            }
        }
    }

    @Override
    public void sendIOCtrlDataInfo(String s, int i, int i1, int i2, byte[] bytes) {

    }

    @Override
    public void receiveVideoInfo(String s, int i, byte[] bytes, long l, int i1, int i2, boolean b) {

    }

    @Override
    public void receiveAudioInfo(String s, int i, byte[] bytes, long l, int i1) {

    }

    @Override
    public void receiveIOCtrlDataInfo(int sid, int avIOCtrlMsgType, byte[] data) {
        switch (avIOCtrlMsgType) {
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_END: {

                CustomCommand.SMsgAVIoctrlCallEnd.Struct struct = CustomCommand.SMsgAVIoctrlCallEnd.parseToStruct(data);
                if (struct.myID.equals(mAccountID)) {
                    //添加历史记录 -- 别人打过来 自己还未接 别人挂断
                    mWaitCallMonitorView.destroy();
                }
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
    public void receiveVideoInfo(int i, byte[] bytes, long l, int i1, int i2, boolean b) {

    }

    @Override
    public void receiveAudioInfo(int i, byte[] bytes, long l, int i1) {

    }

    @Override
    public void receiveOnLineInfo(String s, int i) {

    }

    @Override
    public void receiveIOTCListenInfo(int i) {

    }

    @Override
    public void receiveAvServerStart(int i, int i1, int i2) {

    }

    @Override
    public void receiveSessionCheckInfo(final int sid, St_SInfo info, int result) {
        if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {
            //添加历史记录 -- 别人打过来 自己还未接 别人挂断
            mWaitCallMonitorView.destroy();
        }
    }

    @Override
    public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {

    }

    @Override
    public void receiveConnectInfo(String s, int i, int i1) {

    }

    @Override
    public void receiveClientStartInfo(String s, int i, int i1) {

    }

    @Override
    public void receiveSessionCheckInfo(final String uid, St_SInfo info, int result) {
        if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {
            //添加历史记录 -- 主动打过去 对方掉线
            mWaitCallMonitorView.destroy();
        }
    }

    @Override
    public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int i) {

    }

    public void callOff() {

        if (mThreadAutoConnect != null) {
            mThreadAutoConnect.setThreadListener(null);
            mThreadAutoConnect.stopThread();
            mThreadAutoConnect = null;
        }
        if (mIsCallTo) {
            //发送挂断command
            TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(
                    mCallUID,
                    TUTKP2P.DEFAULT_CHANNEL,
                    CustomCommand.IOTYPE_USER_IPCAM_CALL_END,
                    CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
            SystemClock.sleep(100);
            TUTKP2P.TK_getInstance().TK_client_disConnect(mCallUID);
        } else {
            //发送拒绝command
            TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(mCallBySID,
                    CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                    CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));
            SystemClock.sleep(100);
            TUTKP2P.TK_getInstance().TK_device_disConnect(mCallBySID);
            App.sConnectList.remove(0);

        }

    }

    /**
     * device接听
     */
    public void answer(Bundle bundle) {
        mBundle = bundle;
        initData();
        answer();
    }

    /**
     * device接听
     */
    public void answer() {

        TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(mCallBySID,
                CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                CustomCommand.SMsgAVIoctrlCallResp.parseContent(1, App.sSelfAccountID));

        Intent intent = null;

        Bundle bundle = new Bundle();
        bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, mIsDoubleCall);
        bundle.putParcelableArrayList(App.BUNDLE_INFO_LIST, mOtherUIDList);

//        if (mIsDoubleCall) {
//        } else {
//            if(mMonitorView == null){
//                mMonitorView = new MonitorView(mContext);
//            }
//            mMonitorView.show();
//        }

        if(mMonitorView == null){
            mMonitorView = new MonitorView(mContext.getApplicationContext());
        }

        mMonitorView.show();
        mWaitCallMonitorView.hide();
    }

    public void unbind() {
        if (mThreadAutoConnect != null) {
            mThreadAutoConnect.setThreadListener(null);
            mThreadAutoConnect.stopThread();
            mThreadAutoConnect = null;
        }
    }
}
