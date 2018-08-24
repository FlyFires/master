package cn.nineox.robot.monitor.logic;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.p2p.TUTKP2P;
import com.yanzhenjie.nohttp.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;

import cn.nineox.robot.monitor.CallActivity;
import cn.nineox.robot.monitor.MonitorView;
import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.CustomCommand;
import cn.nineox.robot.monitor.common.tutk.ThreadAutoConnect;
import cn.nineox.robot.monitor.databinding.ActivityWaitCallBinding;
import cn.nineox.robot.monitor.logic.bean.Caller;
import cn.nineox.robot.monitor.utils.GlideUtils;
import cn.nineox.robot.monitor.utils.LogUtil;
import cn.nineox.robot.monitor.utils.SharePrefUtil;
import cn.nineox.robot.monitor.utils.SignUtil;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.AndroidUtil;

import static android.app.Activity.RESULT_OK;

public class WaitCallLogic extends TUTKP2PLogic<ActivityWaitCallBinding> {


    private MonitorView mMonitorView;

    private HashMap<String, Integer> mRingHashmap = new HashMap<>();

    public WaitCallLogic(Activity activity, ActivityWaitCallBinding binding) {
        super(activity, binding);

        mRingHashmap.put("dad", R.raw.babalaidianhuala);
        mRingHashmap.put("mother", R.raw.mamalaidianhuala);
        mRingHashmap.put("grandpa", R.raw.yeyelaidianhuala);
        mRingHashmap.put("grandma", R.raw.nainaidianhualaila);
        mRingHashmap.put("grandfather", R.raw.waigonglaidianhua);
        mRingHashmap.put("grandmother", R.raw.waipolaidianhua);
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
        Intent intent = ((Activity) mActivity).getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mOtherUIDList = bundle.getParcelableArrayList(App.BUNDLE_INFO_LIST);//may be null
            mAccountID = bundle.getString(App.BUNDLE_ACCOUNT_ID);
            mCallUID = bundle.getString(App.BUNDLE_CALL_UID);//may be null
            mCallBySID = bundle.getInt(App.BUNDLE_SID, -1);//may be -1
            mIsCallTo = bundle.getBoolean(App.BUNDLE_IS_CALL_TO);
            mIsDoubleCall = bundle.getBoolean(App.BUNDLE_IS_DOUBLE_CALL);
            Log.e("--", "mAccountID:" + mAccountID + "  mCallUID:" + mCallUID);
            //显示接听按钮
            if (!mIsCallTo) {
                mDataBinding.accpetCall.setVisibility(View.VISIBLE);
                mDataBinding.hangup.setVisibility(View.VISIBLE);
                getCaller(mAccountID);
            }
            mDataBinding.nameTx.setText(mAccountID);

            if (mIsCallTo) {
                getCaller(mAccountID);
                mDataBinding.yaoqingTips.setVisibility(View.GONE);
                //TODO 根据mAccountID id查找相应uid
                //mCallUID = getUIDBean.getMsg().getUID();
                //bundle.putString(App.BUNDLE_ACCOUNT_ID, "366757");
                //bundle.putString(App.BUNDLE_CALL_UID, "DXPA8D5WPX742G6GUHDJ");
                mDataBinding.accpetCall.setVisibility(View.GONE);
                mDataBinding.hangup.setVisibility(View.VISIBLE);
                callTo();
            }


            if (!mIsCallTo && !mIsDoubleCall) {
//                Window window = mActivity.getWindow();
//                window.setGravity(Gravity.LEFT|Gravity.TOP);
//                WindowManager.LayoutParams params = window.getAttributes();
//                params.x = 0;
//                params.y = 0;
//                params.height = 1;
//                params.width = 1;
//                window.setAttributes(params);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        answer();
//                    }
//                },1500);

            } else {
                startRing();
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
        LogUtil.debug("mUID ="+mCallUID+" mSelfID="+App.sSelfAccountID+" mSelfUID="+App.sSelfUID);
        mThreadAutoConnect.start();
        mThreadAutoConnect.setThreadListener(new ThreadAutoConnect.OnThreadListener() {
            @Override
            public void timeOut() {
                /* 3 添加历史记录 -- 主动打过去 对方未接 */
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Toastor(mActivity).showSingletonToast(mActivity.getString(R.string.text_history_missed));
                        ((Activity) mActivity).finish();
                    }
                });

            }
        });
    }

    @Override
    public void receiveIOCtrlDataInfo(final String uid, int avChannel, int avIOCtrlMsgType, byte[] data) {
        super.receiveIOCtrlDataInfo(uid, avChannel, avIOCtrlMsgType, data);

        switch (avIOCtrlMsgType) {
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP: {
                LogUtil.debug("CALL_RESP");
                CustomCommand.SMsgAVIoctrlCallResp.Struct struct = CustomCommand.SMsgAVIoctrlCallResp.parseToStruct(data);
                int answer = struct.answer;

                if (answer == 1) {
                    //Intent intent = new Intent(mActivity, LandscapeLiveViewActivity.class);
                    //TODO
                    Intent intent = new Intent(mActivity, CallActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, mIsDoubleCall);
                    bundle.putBoolean(App.BUNDLE_IS_CALL_TO, mIsCallTo);
                    intent.putExtras(bundle);
//					mActivity.startActivity(intent);
                    mActivity.startActivityForResult(intent, App.INTENT_REQUEST_CALL_TO);
                    mActivity.setResult(RESULT_OK);
                } else {
                    //添加历史记录 -- 主动打过去 对方拒接
                }
                 mActivity.finish();
                LogUtil.debug("mActivity).finish()");

            }
            break;
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_ING:
                CustomCommand.SMsgAVIoctrlCallEnd.Struct struct = CustomCommand.SMsgAVIoctrlCallEnd.parseToStruct(data);

                android.util.Log.e("--", "receiveIOCtrlDataInfo:" + "struct.myID:" + struct.myID + "  mAccountID:" + mAccountID);
                //if (struct.myID.equals(mAccountID)) {
                //添加历史记录 -- 别人打过来 自己还未接 别人挂断
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callOff();
                        new Toastor(mActivity).showSingletonToast("对方正在通话中");
                        mActivity.finish();
                    }
                });
                break;
            default: {

            }
        }
    }

    @Override
    public void receiveAudioInfo(String s, int i, byte[] bytes, long l, int i1) {

    }

    @Override
    public void receiveIOCtrlDataInfo(int sid, int avIOCtrlMsgType, byte[] data) {
        super.receiveIOCtrlDataInfo(sid, avIOCtrlMsgType, data);
        switch (avIOCtrlMsgType) {
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_END: {

                CustomCommand.SMsgAVIoctrlCallEnd.Struct struct = CustomCommand.SMsgAVIoctrlCallEnd.parseToStruct(data);
                if (struct.myID.equals(mAccountID)) {
                    //添加历史记录 -- 别人打过来 自己还未接 别人挂断
                    ((Activity) mActivity).finish();
                }
            }
            break;
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_ING:
                CustomCommand.SMsgAVIoctrlCallEnd.Struct struct = CustomCommand.SMsgAVIoctrlCallEnd.parseToStruct(data);

                android.util.Log.e("--", "receiveIOCtrlDataInfo:" + "struct.myID:" + struct.myID + "  mAccountID:" + mAccountID);
                //if (struct.myID.equals(mAccountID)) {
                //添加历史记录 -- 别人打过来 自己还未接 别人挂断
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callOff();
                        new Toastor(mActivity).showSingletonToast("对方正在通话中");
                        ((Activity) mActivity).finish();
                    }
                });
                break;
            default: {

            }
        }
    }

    @Override
    public void receiveAudioInfo(int i, byte[] bytes, long l, int i1) {

    }

    @Override
    public void receiveOnLineInfo(String s, int i) {

    }

    @Override
    public void receiveSessionCheckInfo(final int sid, St_SInfo info, int result) {
        super.receiveSessionCheckInfo(sid, info, result);
        if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {
            //添加历史记录 -- 别人打过来 自己还未接 别人挂断
            ((Activity) mActivity).finish();
        }
    }

    @Override
    public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {
        LogUtil.debug("rtt == "+st_avStatus.RoundTripTime);
    }

    @Override
    public void receiveSessionCheckInfo(final String uid, St_SInfo info, int result) {
        super.receiveSessionCheckInfo(uid, info, result);
        if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {
            //添加历史记录 -- 主动打过去 对方掉线
            ((Activity) mActivity).finish();
        }
    }

    @Override
    public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int i) {
        LogUtil.debug("rtt == "+st_avStatus.RoundTripTime);
    }

    public void callOff() {
        Log.e("CallLogic","WaitCallLogic callOff");
        if (mThreadAutoConnect != null) {
            mThreadAutoConnect.setThreadListener(null);
            mThreadAutoConnect.stopThread();
            mThreadAutoConnect = null;
        }
        if (mIsCallTo) {
            //发送挂断command
            if (mCallUID != null) {
                TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(
                        mCallUID,
                        TUTKP2P.DEFAULT_CHANNEL,
                        CustomCommand.IOTYPE_USER_IPCAM_CALL_END,
                        CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
                SystemClock.sleep(100);
                TUTKP2P.TK_getInstance().TK_client_disConnect(mCallUID);
            }
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
    public void answer() {

        TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(mCallBySID,
                CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                CustomCommand.SMsgAVIoctrlCallResp.parseContent(1, App.sSelfAccountID));

        Intent intent = null;

        Bundle bundle = new Bundle();
        bundle.putString(App.BUNDLE_ACCOUNT_ID, mAccountID);
        bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, mIsDoubleCall);
        bundle.putParcelableArrayList(App.BUNDLE_INFO_LIST, mOtherUIDList);
        Log.e("--", "mIsDoubleCall:" + mIsDoubleCall);
        if (mIsDoubleCall) {
            intent = new Intent(mActivity, CallActivity.class);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
            mActivity.finish();
        } else {
            //intent = new Intent(mActivity, RobotPreviewActivity.class);
            mMonitorView = new MonitorView(mActivity);
            mMonitorView.show();
            mActivity.finish();

        }


    }

    public void unbind() {
        super.unbind();
        if (mThreadAutoConnect != null) {
            mThreadAutoConnect.setThreadListener(null);
            mThreadAutoConnect.stopThread();
            mThreadAutoConnect = null;
        }

        TUTKP2P.TK_getInstance().TK_unRegisterClientListener(this);
        TUTKP2P.TK_getInstance().TK_unRegisterDeviceListener(this);

        stopRing();
    }


    public void getCaller(String id) {
        String mid = AndroidUtil.getAndroidId(mActivity.getApplicationContext());
        EntityRequest reqeust = new EntityRequest(Const.GET_CALLER, RequestMethod.GET, Caller.class);
        reqeust.add("id", id);
        reqeust.add("type", "app");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String str = mid + "&" + App.getToken() + "&" + timestamp + "&" + id + "&app";
        reqeust.addHeader("deviceId", mid);
        reqeust.addHeader("timestamp", timestamp);
        reqeust.addHeader("token", App.getToken());
        reqeust.addHeader("sign", SignUtil.getSign(str));
        Log.e("http", "getCaller:" + str);
        execute(reqeust, new ResponseListener<Caller>() {


            @Override
            public void onSucceed(int what, Result<Caller> result) {
                super.onSucceed(what, result);
                Log.e("--", "getCaller:" + result.getResult());
                Caller caller = result.getResult();
                if (caller != null && caller.getUser() != null) {
                    mDataBinding.nameTx.setText(caller.getUser().getUserType().getDesc());
                    GlideUtils.displayCircleImageView(mDataBinding.headIv, caller.getUser().getHeadPicture(), mActivity.getResources().getDrawable(R.drawable.ic_head_round));
                    startRing2(caller.getUser().getUserType().key);
                }

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast(error);
            }
        });
    }


    /**
     * 播放铃音
     */
    public void startRing() {
        try {
            mMediaPlayer = MediaPlayer.create(mActivity, R.raw.video_request);
            mMediaPlayer.setLooping(true);//循环播放
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放铃音
     */
    public void startRing2(String userType) {
        Log.e("--", "startRing2:" + userType);
        if (mIsCallTo) {
            return;
        }
        try {
            if (mRingHashmap.containsKey(userType)) {
                int raw = mRingHashmap.get(userType);
                MediaPlayer mMediaPlayer = MediaPlayer.create(mActivity, raw);
                mMediaPlayer.setLooping(false);//循环播放
                mMediaPlayer.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放铃音
     */
    public void stopRing() {
        try {
            if (null != mMediaPlayer) {
                mMediaPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
