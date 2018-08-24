package cn.nineox.robot.monitor.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.TextureView;
import android.view.View;

import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.libTUTKMedia.CameraDecodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview2;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.libTUTKMedia.inner.OnMediaListener;
import com.tutk.libTUTKMedia.inner.OnPreviewTextureListener;
import com.tutk.libTUTKMedia.utils.MediaCodecUtils;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.utils.P2PUtils;
import com.yanzhenjie.nohttp.RequestMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cn.nineox.robot.monitor.CallActivity;
import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.ConnectInfo;
import cn.nineox.robot.monitor.common.tutk.CustomArrayList;
import cn.nineox.robot.monitor.common.tutk.CustomCommand;
import cn.nineox.robot.monitor.common.tutk.ILiveViewActivityPresenter;
import cn.nineox.robot.monitor.common.tutk.ThreadAutoConnect;
import cn.nineox.robot.monitor.common.tutk.ThreadTimerClock;
import cn.nineox.robot.monitor.common.weiget.CustomLayoutManager;
import cn.nineox.robot.monitor.databinding.ActivityCallBinding;
import cn.nineox.robot.monitor.databinding.ItemOtherCallBinding;
import cn.nineox.robot.monitor.logic.bean.UserBean;
import cn.nineox.robot.monitor.utils.LogUtil;
import cn.nineox.robot.monitor.utils.StringUtils;
import cn.nineox.robot.monitor.utils.WindowUtils;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.utils.AndroidUtil;

/**
 * Created by me on 17/12/22.
 */

public class CallLogic extends TUTKP2PLogic<ActivityCallBinding> implements ILiveViewActivityPresenter {

    private String TAG = "CallLogic";

    private static final long BITRATE_DOWN_TIME = 10 * 1000;

    private int mCallBySID = -1;

    private String mAccountID;//对方id
    private String mCallUID;//对方UID

    private boolean mIsDoubleCall;//是否双向
    private boolean mIsCallTo;//是否主动打过去
    private int mRttcount;
    private ThreadAutoConnect mThreadAutoConnect;//自动连线线程

    private ArrayList<CustomCommand.StructAccountInfo> mOtherUIDList;//其它uid

    private MediaPlayer player;

    public boolean mIsListener = true;//是否监听
    public boolean mIsSpeak = true;//是否对讲
    public boolean mIsRecord = false;//是否录像
    public  boolean mIsSendVideo  = true;//是否传视频

    private long mBitrateDownTime;//降码流时间 避免降低过快

    private ThreadTimerClock mThreadTotalTimer;//通话总时间计时
    private ThreadTimerClock mThreadRecordTimer;//录像时间计时

    private ThreadAutoConnect mAutoConnect;//邀请线程

    private BaseBindingAdapter mAdapter;

    public CallLogic(Activity activity, ActivityCallBinding binding) {
        super(activity, binding);
        initData();
        if (mIsCallTo && App.sConnectList.size() == 0) {
            mDataBinding.accpetCall.setVisibility(View.GONE);
        } else {

        }

    }


    @Override
    public void initData() {
        if(App.sConnectList.size() == 0){
            //callOff();
            mActivity.finish();
        }
        ConnectInfo accountInfo = App.sConnectList.get(0);
        mDataBinding.decodePreviewBig.setTag(accountInfo.mAccountId);
        //设置大窗口状态监听
        mDataBinding.decodePreviewBig.setOnPreviewTextureListener(new OnPreviewTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(TextureView textureView, int i, int i1) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(TextureView textureView, int i, int i1) {

            }

            @Override
            public void onSurfaceTextureUpdated(TextureView textureView) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(TextureView textureView) {
                return false;
            }
        });
        bind();
        App.sConnectList.setOnListDataChangeListener(mOnListDataChangeListener);
        TUTKMedia.TK_getInstance().TK_registerListener(mOnMediaListener);
        intOtherPreview();

        Intent intent = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
        boolean isDoubleCall = false;
        if (bundle != null) {
            //mAccountID = bundle.getString(App.BUNDLE_ACCOUNT_ID);
            isDoubleCall = bundle.getBoolean(App.BUNDLE_IS_DOUBLE_CALL, false);
            mIsCallTo = bundle.getBoolean(App.BUNDLE_IS_CALL_TO, false);
        }

        App.sHasInvitePermission = mIsCallTo && isDoubleCall;

        /* 1 初始化右侧视图 如果是双向则开启视频编码 如果是主动call则拥有邀请权限 */
        //mView.initAdapter(isDoubleCall, mIsCallTo);
        //是否隐藏小计时器
        //mView.showItemTimeLayout(isDoubleCall);
        /* 2 开启会话的总计时 */
        mThreadTotalTimer = new ThreadTimerClock();
        mThreadTotalTimer.start();
        mThreadTotalTimer.setThreadListener(mTimeListener);

        /* 3 如果是打过去 发送p2p视频／音频请求 */
        if (!accountInfo.mByClientConnect) {

            //d->c 发送视频 1 client发送请求 startReceiveVideo
            TUTKP2P.TK_getInstance().TK_client_startReceiveVideo(accountInfo.mDeviceUID, accountInfo.mDeviceChannel, false);

            //d->c 发送音频 1 client发送请求 startListener
            TUTKP2P.TK_getInstance().TK_client_startListener(accountInfo.mDeviceUID, accountInfo.mDeviceChannel);

            if (isDoubleCall) {//如果是双向
                //c->d 发送视频 1 client发送请求 startSendVideo
               TUTKP2P.TK_getInstance().TK_client_startSendVideo(accountInfo.mDeviceUID, accountInfo.mDeviceChannel);
            }

            //d->c 发送音频 1 client发送请求 startSpeaking
            TUTKP2P.TK_getInstance().TK_client_startSpeaking(accountInfo.mDeviceUID, accountInfo.mDeviceChannel);
        }

        //设置会议主讲人
        App.sInviteAccountID = (mIsCallTo && mIsDoubleCall) ? App.sSelfAccountID : accountInfo.mAccountId;
        /* 4 如果是被邀请 则还需要连接其它uid并且 发送p2p视频／音频请求 */
        assert bundle != null;
        ArrayList<CustomCommand.StructAccountInfo> otherUID = bundle.getParcelableArrayList(App.BUNDLE_INFO_LIST);
        if (otherUID != null) {
            for (CustomCommand.StructAccountInfo info : otherUID) {
                TUTKP2P.TK_getInstance().TK_client_connect(
                        info.UID, App.DEVICE_PASSWORD, App.DEVICE_CHANNEL);
                TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(info.UID, App.DEVICE_CHANNEL,
                        CustomCommand.IOTYPE_USER_IPCAM_CALL_REQ,
                        CustomCommand.SMsgAVIoctrlCallReq.parseContent(
                                App.sSelfAccountID,
                                App.sSelfUID,
                                1,
                                1,
                                null));
            }
        }

        /* 6 开启自己的音频编码 */
        //开始音频编码 单向屏蔽对讲
        if (isDoubleCall) {
            TUTKMedia.TK_getInstance().TK_audio_startCapture(
                    App.AUDIO_SAMPLE_RATE,
                    App.AUDIO_BIT,
                    App.AUDIO_CODEC);
        }
        //开始音频解码
        TUTKMedia.TK_getInstance().TK_decode_startDecodeAudio(accountInfo.mAccountId,
                App.AUDIO_SAMPLE_RATE,
                App.AUDIO_BIT,
                App.AUDIO_CODEC);
//        if (mIsDoubleCall || mIsCallTo) {
//            intOtherPreview();
//        }
        mDataBinding.accpetCall.setVisibility(View.GONE);

        //getUserByCallId();
    }

    private void intOtherPreview() {
        CustomArrayList sConnectList = new CustomArrayList();
        sConnectList.add(0,new ConnectInfo());
        mAdapter = new BaseBindingAdapter<ConnectInfo, ItemOtherCallBinding>(mActivity,sConnectList, R.layout.item_other_call) {

            @Override
            public void onBindViewHolder(BaseBindingVH<ItemOtherCallBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                if (position == 0) {
                    //holder.getBinding().nameTx.setText(getItem(position).mNickName);
                }

                //holder.getBinding().decodePreview.setTag(getItem(position).mAccountId);//设置标志位
            }
        };
        CustomLayoutManager manager = new CustomLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(manager);
        mDataBinding.recyclerView.setAdapter(mAdapter);

    }

    public void telRecordSave(String mobile, int type, long startTime, long endTime) {
        StringReqeust reqeust = new StringReqeust(Const.TELRECORD_SAVE);
        reqeust.add("mobile", mCallUID);
        reqeust.add("type", type);
        reqeust.add("startTime", startTime);
        reqeust.add("endTime", endTime);
        reqeust.add("mid", AndroidUtil.getAndroidId(mActivity));
        execute(reqeust, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
            }
        });

    }


    @Override
    public void receiveIOCtrlDataInfo(int sid, final int avIOCtrlMsgType, byte[] data) {
        super.receiveIOCtrlDataInfo(sid, avIOCtrlMsgType, data);
        switch (avIOCtrlMsgType) {
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_END: {
                Log.e("--","CallLogic2 receiveIOCtrlDataInfo:" + avIOCtrlMsgType  + " mAccountID:" + App.sSelfAccountID );
                CustomCommand.SMsgAVIoctrlCallEnd.Struct struct = CustomCommand.SMsgAVIoctrlCallEnd.parseToStruct(data);
//                if (struct.myID.equals(mAccountID)) {
//                    //添加历史记录 -- 别人打过来 自己还未接 别人挂断
//                    mActivity.finish();
//                }
            }
            break;
            case CustomCommand.IOTYPE_USER_IPCAM_SWITCH_AUDIO:
                ConnectInfo accountInfo = App.sConnectList.get(0);
                //停止视频编码
                TUTKMedia.TK_getInstance().TK_encode_unBindView();
                TUTKP2P.TK_getInstance().TK_client_stopSendVideo(accountInfo.mDeviceUID, accountInfo.mDeviceChannel);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("--","receiveIOCtrlDataInfo IOTYPE_USER_IPCAM_SWITCH_AUDIO:" + avIOCtrlMsgType );
                        mDataBinding.recyclerView.setVisibility(View.GONE);
                        mDataBinding.decodePreviewBig.setVisibility(View.GONE);
                    }
                });

                break;

            default: {

            }
        }
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
            //mActivity.finish();
        }
    }

    @Override
    public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int result) {
        changeDASA(st_avStatus.RoundTripTime);
        LogUtil.debug("rtt device = "+st_avStatus.RoundTripTime);
    }

    @Override
    public void receiveSessionCheckInfo(final String uid, St_SInfo info, int result) {
        super.receiveSessionCheckInfo(uid, info, result);
        if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
                || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {

            //添加历史记录 -- 主动打过去 对方掉线
            mActivity.finish();
        }
    }

    @Override
    public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int result) {
        changeDASA(result);
        LogUtil.debug("rtt client== "+st_avStatus.RoundTripTime);
//        if (App.sConnectList.size() > 0 && uid.equalsIgnoreCase(App.sConnectList.get(0).mDeviceUID)) {
//            mView.debugP2PRtt(status.RoundTripTime);
//        }
    }


    @Override
    public void startListener() {
        mIsListener = true;
        for (ConnectInfo info : App.sConnectList) {
            TUTKMedia.TK_getInstance().TK_decode_startDecodeAudio(info.mAccountId,
                    App.AUDIO_SAMPLE_RATE,
                    App.AUDIO_BIT,
                    App.AUDIO_CODEC);
        }
    }

    @Override
    public void stopListener() {
        mIsListener = false;
        TUTKMedia.TK_getInstance().TK_decode_stopDecodeAllAudio();
    }


    @Override
    public void startSpeak() {
        mIsSpeak = true;
    }

    @Override
    public void stopSpeak() {
        mIsSpeak = false;
    }

    @Override
    public void snapshot() {

        if (App.sConnectList.size() > 0) {
            String accountID = App.sConnectList.get(0).mAccountId;
            String path = Environment.getExternalStorageDirectory().getPath()
                    + App.APP_PATH_SNAPSHOT + accountID;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            TUTKMedia.TK_getInstance().TK_video_snapShot(
                    accountID,
                    path + File.separator + StringUtils.getFileNameWithTime() + ".png");
        }
    }

    @Override
    public void startRecord() {
        if (App.sConnectList.size() > 0) {
            mIsRecord = true;
            String accountID = App.sConnectList.get(0).mAccountId;
            String path = Environment.getExternalStorageDirectory().getPath()
                    + App.APP_PATH_RECORD + accountID;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            TUTKMedia.TK_getInstance().TK_video_startRecord(
                    accountID,path + File.separator + StringUtils.getFileNameWithTime() + ".mp4",
                    mIsListener
                    );
        }
    }

    @Override
    public void stopRecord(boolean isRecordSuccess) {
        if (App.sConnectList.size() > 0) {
            TUTKMedia.TK_getInstance().TK_video_stopRecord(App.sConnectList.get(0).mAccountId);
            mIsRecord = false;
            //mView.recordComplete(isRecordSuccess);
        }
    }

    @Override
    public void switchCamera() {
        int position = TUTKMedia.TK_getInstance().TK_preview_getCamera();
        if (position == CameraEncodePreview2.CAMERA_BACK) {
            TUTKMedia.TK_getInstance().TK_preview_switchCamera(CameraEncodePreview2.CAMERA_FRONT);
        } else {
            TUTKMedia.TK_getInstance().TK_preview_switchCamera(CameraEncodePreview2.CAMERA_BACK);
        }

    }

    @Override
    public void callOff() {
        Log.e(TAG,"callOff");
        long duringTime = 0;

        //stop record
        if (mIsRecord) {
            stopRecord(false);
        }

        //stop ThreadTimer
        if (mThreadTotalTimer != null) {
            duringTime = mThreadTotalTimer.getTime();
            mThreadTotalTimer.setThreadListener(null);
            mThreadTotalTimer.stopThread();
            mThreadTotalTimer = null;
        }


		/* 3 停止Media模块的编码／解码 */
        //取消监听
        TUTKMedia.TK_getInstance().TK_unRegisterListener(mOnMediaListener);
        //停止视频编码
        TUTKMedia.TK_getInstance().TK_encode_unBindView();
//		TUTKMedia.TK_getInstance().TK_preview_stopCapture();
        //停止音频编码
        TUTKMedia.TK_getInstance().TK_audio_stopCapture();
        //停止视频解码
        TUTKMedia.TK_getInstance().TK_decode_stopDecodeAllVideo();
        //停止音频解码
        TUTKMedia.TK_getInstance().TK_decode_stopDecodeAllAudio();

		/* 4 发送挂断command 并且 停止P2P模块的所有连线 */
        for (ConnectInfo accountInfo : App.sConnectList) {
            if (accountInfo.mByClientConnect) {
                TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(
                        accountInfo.mClientSID,
                        CustomCommand.IOTYPE_USER_IPCAM_CALL_END,
                        CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
            } else {
                TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(
                        accountInfo.mDeviceUID,
                        accountInfo.mDeviceChannel,
                        CustomCommand.IOTYPE_USER_IPCAM_CALL_END,
                        CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
            }
        }

        SystemClock.sleep(200);
        TUTKP2P.TK_getInstance().TK_client_disConnectAll();
        TUTKP2P.TK_getInstance().TK_device_disConnectAll();
        for (int i = 0; i < App.sConnectList.size(); i++) {
            ConnectInfo info = App.sConnectList.get(i);
         /*   if (info.mByClientConnect) {
                TUTKP2P.TK_getInstance().TK_device_disConnect(info.mClientSID);
            } else {
                TUTKP2P.TK_getInstance().TK_client_disConnect(info.mDeviceUID);
            }*/
            //停止计时
            if (info.mThreadTimer != null) {
                info.mThreadTimer.setThreadListener(null);
                info.mThreadTimer.stopThread();
                info.mThreadTimer = null;
            }
        }

        App.sConnectList.setOnListDataChangeListener(null);
        App.sConnectList.clear();

        App.sInviteAccountID = null;
    }


    /**
     * device接听
     */
    public void answer() {
        mDataBinding.accpetCall.setVisibility(View.GONE);
        TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(mCallBySID,
                CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                CustomCommand.SMsgAVIoctrlCallResp.parseContent(1, App.sSelfAccountID));
        Intent intent = null;
//        if (mIsDoubleCall) {
//            intent = new Intent(mActivity, LandscapeLiveViewActivity.class);
//        } else {
//            intent = new Intent(mActivity, PortraitLiveViewActivity.class);
//        }
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, mIsDoubleCall);
//        bundle.putParcelableArrayList(App.BUNDLE_INFO_LIST, mOtherUIDList);
//        intent.putExtras(bundle);
//        mActivity.startActivity(intent);
//        mActivity.finish();
    }

    @Override
    public void unbind() {
        super.unbind();
        if (mThreadAutoConnect != null) {
            mThreadAutoConnect.setThreadListener(null);
            mThreadAutoConnect.stopThread();
            mThreadAutoConnect = null;
        }
    }

    @Override
    public void addOtherAccount() {

    }

    @Override
    public void changePosition(int pos) {
        if (mIsRecord) {
            new Toastor(mActivity).showSingleLongToast("正在录制中");
            return;
        }
        ConnectInfo bigInfo = App.sConnectList.get(0);
        ConnectInfo smallInfo = App.sConnectList.get(pos);

        /* 1 找到两者视频设置的tag值 */
        String bigTag = bigInfo.mAccountId;
        String smallTag = smallInfo.mAccountId;

        /* 2 更换视频的tag */
        String tempTag = "temp";
        TUTKMedia.TK_getInstance().TK_decode_setDecodeVideoTag(bigTag, tempTag);
        TUTKMedia.TK_getInstance().TK_decode_setDecodeVideoTag(smallTag, bigTag);
        TUTKMedia.TK_getInstance().TK_decode_setDecodeVideoTag(tempTag, smallTag);

        /* 3 更换ui */
//        mView.setNickName(smallInfo.mNickName);
//        mView.setItemNickName(pos, bigInfo.mNickName);
        bigInfo.mThreadTimer.setThreadListener(null);
        smallInfo.mThreadTimer.setThreadListener(mItemTimeListener);

        /* 4 更换集合顺序 */
        Collections.swap(App.sConnectList, 0, pos);
    }

    @Override
    public void changeDASA(int rttResult) {
        Log.e("--","changeDASA:" + rttResult);
		/* 动态码流 */
        if(rttResult > 500){
            mRttcount++;
        }else{
            mRttcount = 0;
        }
        if(mRttcount >=10){
            new Toastor(mActivity).showSingleLongToast(R.string.tips_network_bad);
            mRttcount = 0;
        }
       /* if (rttResult == P2PUtils.STATUS_CHECK_UNKNOWN) {
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
                new Toastor(mActivity).showSingleLongToast(R.string.tips_network_block);
                mIsSendVideo = false;
            }

            if (bitrateDown != -1) {
                new Toastor(mActivity).showSingleLongToast(R.string.tips_network_low);
                TUTKMedia.TK_getInstance().TK_preview_changeQuality(
                        TUTKMedia.TK_getInstance().TK_preview_getResolution(),
                        bitrateDown,
                        TUTKMedia.TK_getInstance().TK_preview_getFPS());
            }
        } else if (!mIsSendVideo && rttResult == P2PUtils.STATUS_CHECK_NORMAL) {
            //网络正常恢复视频
            mIsSendVideo = true;
            new Toastor(mActivity).showSingleLongToast(R.string.tips_network_normal);
        }*/
    }


    private ThreadTimerClock.OnThreadListener mTimeListener = new ThreadTimerClock.OnThreadListener() {
        StringBuilder mStringBuilder = new StringBuilder();

        @Override
        public void onTime(String time) {
            //是否被回收
            if (mActivity == null) {
                return;
            }
            mStringBuilder.delete(0, mStringBuilder.length());
            mStringBuilder.append(" ")
                    .append(mActivity.getString(R.string.text_time_duration))
                    .append(" ")
                    .append(time);
            //mView.setTotalTime(mStringBuilder.toString());
        }
    };

    private ThreadTimerClock.OnThreadListener mItemTimeListener = new ThreadTimerClock.OnThreadListener() {

        StringBuilder mStringBuilder = new StringBuilder();

        @Override
        public void onTime(String time) {
            //是否被回收
            if (mActivity == null) {
                return;
            }
            mStringBuilder.delete(0, mStringBuilder.length());
            mStringBuilder.append(mActivity.getString(R.string.text_time_duration))
                    .append(" ")
                    .append(time);
            //mView.setItemTime(mStringBuilder.toString());
        }
    };

    private ThreadTimerClock.OnThreadListener mRecordTimeListener = new ThreadTimerClock.OnThreadListener() {

        @Override
        public void onTime(String time) {
            //是否被回收
            if (mActivity == null) {
                return;
            }
            //mView.recordContinue(time);
        }
    };

    private OnMediaListener mOnMediaListener = new OnMediaListener() {

        //视频编码控件初始化完毕
        @Override
        public void onVideoEncodePreviewInit(CameraEncodePreview preview) {
            SharedPreferences sp = mActivity.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
            boolean encodeSoft = sp.getBoolean(App.SP_ENCODE_SOFT, false);
            //绑定编码器
            TUTKMedia.TK_getInstance().TK_encode_bindView(preview);
            TUTKMedia.TK_getInstance().TK_preview_changeQuality(
                    TUTKMedia.TK_getInstance().TK_preview_getResolution(),
                    MediaCodecUtils.PREVIEW_BITRATE_MIDDLE,
                    TUTKMedia.TK_getInstance().TK_preview_getFPS(),
                    TUTKMedia.TK_getInstance().TK_preview_getGOP());
            //开始视频编码
            TUTKMedia.TK_getInstance().TK_preview_startCapture(App.VIDEO_FORMAT,true);
            //设置预览缩放类型
            TUTKMedia.TK_getInstance().TK_preview_setScaleType(MediaCodecUtils.SCALE_ASPECT_FILL);
}

        @Override
        public void onVideoEncodePreviewInit(CameraEncodePreview2 preview) {

        }


        private FileOutputStream mDecodeOutputSteam2;
        @Override
        public void onVideoEncodeCallback(byte[] data, int width, int height, boolean isIFrame, long timeStamp) {
            if (!mIsSendVideo) {
                return;
            }
            for (ConnectInfo info : App.sConnectList) {
                if (info != null) {
                    if (info.mByClientConnect) {
                        //d->c 发送视频 3 device将编码数据传给client
                        TUTKP2P.TK_getInstance().TK_device_onSendVideoData(info.mClientSID, data, isIFrame, timeStamp);
                    } else {
                        //c->d 发送视频 3 client将编码数据传给device
                        TUTKP2P.TK_getInstance().TK_client_onSendVideoData(info.mDeviceUID, info.mDeviceChannel, data, isIFrame, timeStamp);
                    }
                }
            }
            //Log.e("--","onVideoEncodeCallback:" + this);
//            Log.e("--","onVideoEncodeCallback:" + mOnMediaListener );
//
//            if(mDecodeOutputSteam2 == null){
//                String path = Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/encode.h264";
//                if(!new File(Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/").exists()){
//                    new File(Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/").mkdirs();
//                }
//                try{
//                    mDecodeOutputSteam2 = new FileOutputStream(path);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            try{
//                mDecodeOutputSteam2.write(data);
//                mDecodeOutputSteam2.flush();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }

        @Override
        public void onVideoEncodeSnapshot(boolean b) {

        }


        //视频解码控件初始化完毕
        @Override
        public void onVideoDecodePreviewInit(CameraDecodePreview preview) {

            /* 第一个为大窗口 其余的是小窗口的 */

            for (int i = 0; i < App.sConnectList.size(); i++) {

                ConnectInfo info = App.sConnectList.get(i);

                if (preview.getTag() == info.mAccountId) {

                    //开始计时
                    if (info.mThreadTimer == null) {
                        info.mThreadTimer = new ThreadTimerClock();
                        info.mThreadTimer.start();
                    }

                    //开始视频解码
                    SharedPreferences sp = mActivity.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
                    boolean isSoftDecode = sp.getBoolean(App.SP_DECODE_SOFT, false);
                    TUTKMedia.TK_getInstance().TK_decode_startDecodeVideo(preview, preview.getTag(), App.VIDEO_FORMAT, isSoftDecode);

                    //设置缩放类型
                    if (i == 0) {
                        //mView.setNickName(info.mNickName);
                        info.mThreadTimer.setThreadListener(mItemTimeListener);
                        TUTKMedia.TK_getInstance().TK_video_setScaleType(preview.getTag(), MediaCodecUtils.SCALE_ASPECT_FILL);
                    } else {
                        TUTKMedia.TK_getInstance().TK_video_setScaleType(preview.getTag(), MediaCodecUtils.SCALE_ASPECT_FILL);
                    }

                    break;
                }
            }
        }

        @Override
        public synchronized void onVideoDecodeCallback(Object tag, byte[] data, int width, int height) {
        }

        @Override
        public void onVideoDecodeSnapshot(Object o, boolean b) {

        }


        @Override
        public void onVideoDecodeRecord(Object tag, boolean success) {
            mIsRecord = success;

            if (success) {
                //mView.recordStart();
                mThreadRecordTimer = new ThreadTimerClock();
                mThreadRecordTimer.setThreadListener(mRecordTimeListener);
                mThreadRecordTimer.start();
            } else {
                stopRecord(false);
            }

        }

        @Override
        public void onVideoDecodeUnSupport(final CameraDecodePreview preview) {
            SharedPreferences sp = mActivity.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
            sp.edit().putBoolean(App.SP_DECODE_SOFT, true).apply();
            ((Activity) mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TUTKMedia.TK_getInstance().TK_decode_stopDecodeVideo(preview.getTag());
                    TUTKMedia.TK_getInstance().TK_decode_startDecodeVideo(preview, preview.getTag(), App.VIDEO_FORMAT, true);
                }
            });
        }

        @Override
        public void onVideoEncodeUnSupport() {
            SharedPreferences sp = mActivity.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
            sp.edit().putBoolean(App.SP_ENCODE_SOFT, true).apply();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TUTKMedia.TK_getInstance().TK_preview_stopCapture();
                    TUTKMedia.TK_getInstance().TK_preview_startCapture(App.VIDEO_FORMAT, true);
                }
            });
        }

        @Override
        public void onAudioEncodeCallback(byte[] data, int encodeLength, long timeStamp) {
            if (!mIsSpeak) {
                return;
            }
            for (int i = 0; i < App.sConnectList.size(); i++) {
                ConnectInfo info = App.sConnectList.get(i);
                if (info != null) {
                    if (info.mByClientConnect) {
                        //d->c 发送音频 3 device将编码数据传给client
                        TUTKP2P.TK_getInstance().TK_device_onSendAudioData(info.mClientSID, data, encodeLength, timeStamp);
                    } else {
                        //c->d 发送音频 3 client将编码数据传给device
                        TUTKP2P.TK_getInstance().TK_client_onSendAudioData(info.mDeviceUID, info.mDeviceChannel, data, encodeLength, timeStamp);
                    }
                }
            }
        }

        @Override
        public void onAudioDecodeCallback(Object tag, byte[] data, int encodeLength, int audioID, int simpleRate, int bit) {

        }
    };

    /**
     * 此监听器是由于App.sAccountList长度变化获取到的回调
     */
    private CustomArrayList.OnListDataChangeListener mOnListDataChangeListener = new CustomArrayList.OnListDataChangeListener() {

        @Override
        public void onAdd(final int index, ConnectInfo accountInfo) {
            final String nickName = accountInfo.mNickName;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Toastor(mActivity).showSingletonToast(nickName + " " + mActivity.getString(R.string.text_history_incoming));
                }
            });

//            if (index == 0) {
//                SharedPreferences sp = mActivity.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
//                boolean isSoftDecode = sp.getBoolean(App.SP_DECODE_SOFT, false);
//                TUTKMedia.TK_getInstance().TK_decode_startDecodeVideo(mDataBinding.decodePreviewBig, accountInfo.mAccountId, App.VIDEO_FORMAT, isSoftDecode);
//
//            }
            /* 1 开始音频解码 */
            TUTKMedia.TK_getInstance().TK_decode_startDecodeAudio(accountInfo.mAccountId,
                    App.AUDIO_SAMPLE_RATE,
                    App.AUDIO_BIT,
                    App.AUDIO_CODEC);

            /* 2 添加小窗口创建视频解码器 */
            //if (index != 0) {
                notifyAdapterData();
                //mView.notifyItemAdd(index);
            //}

            /* 3 如果开启了自动连线 则停止 */
//            if (mAutoConnect != null) {
//                //更新转圈ui
//                //mView.stopInvite();
//                mAutoConnect.setThreadListener(null);
//                mAutoConnect.stopThread();
//                mAutoConnect = null;
//            }

        }

        @Override
        public void onRemove(int index, ConnectInfo accountInfo) {

            try{
                /* 1 停止计时线程 */
                if (accountInfo.mThreadTimer != null) {
                    accountInfo.mThreadTimer.setThreadListener(null);
                    accountInfo.mThreadTimer.stopThread();
                    accountInfo.mThreadTimer = null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            Log.i(TAG, "onRemove index " + index +  "  siez:" + App.sConnectList.size());

//            /* 3 如果没有连线 则退出页面 */
//            if (App.sConnectList.size() == 0) {
//                callOff();
//                mActivity.finish();
//                return;
//            }

			/* 3 停止Media的解码 */

            String offlineID = accountInfo.mAccountId;

            TUTKMedia.TK_getInstance().TK_decode_stopDecodeAudio(offlineID);

            //如果是主讲人掉线 或者没有连线 则直接退出页面
            if (App.sConnectList.size() == 0 || offlineID.equals(App.sInviteAccountID)) {
                TUTKMedia.TK_getInstance().TK_decode_stopDecodeVideo(offlineID);
                mActivity.finish();
            } else if (index != 0) {//如果是小窗口掉线 则只停止小窗口的解码
                TUTKMedia.TK_getInstance().TK_decode_stopDecodeVideo(offlineID);
                notifyAdapterData();
            } else {//如果是大窗口掉线了 则将第一个小窗口移到大窗口
                String connectID = App.sConnectList.get(0).mAccountId;

                App.sConnectList.get(0).mThreadTimer.setThreadListener(mItemTimeListener);
                //停止小窗口的解码
                TUTKMedia.TK_getInstance().TK_decode_stopDecodeVideo(connectID);
                //重新设置设置大窗口的tag
                TUTKMedia.TK_getInstance().TK_decode_setDecodeVideoTag(offlineID, connectID);
                notifyAdapterData();

            }

            accountInfo = null;
        }
    };

    FileOutputStream mDecodeOutputSteam;
    @Override
    public void receiveVideoInfo(int sid, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {
        super.receiveVideoInfo(sid, videoData, timeStamp, number, onlineNum, isIFrame);
        if(CallActivity.getinstance()!=null){
            ((CallActivity) CallActivity.getinstance()).disLoading();
        }
        for (int i = 0; i < App.sConnectList.size(); i++) {
            ConnectInfo info = App.sConnectList.get(i);
            if (info != null && info.mByClientConnect && info.mClientSID == sid) {
                //c->d 发送视频 4 device收到视频数据传给解码器
                TUTKMedia.TK_getInstance().TK_decode_onReceiveVideoData(info.mAccountId, videoData, timeStamp, number, isIFrame);
                break;
            }
        }
       // LogUtil.debug("recvideoData ="+ videoData );
        //Log.e("--","receiveVideoInfo:" + this + "size:" + App.sConnectList.size());

//        if(mDecodeOutputSteam == null){
//            String path = Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/decode.h264";
//            if(!new File(Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/").exists()){
//                new File(Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/").mkdirs();
//            }
//            try{
//                mDecodeOutputSteam = new FileOutputStream(path);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//        try{
//            mDecodeOutputSteam.write(videoData);
//            mDecodeOutputSteam.flush();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }


    @Override
    public void receiveAudioInfo(int sid, byte[] audioData, long timeStamp, int number) {
        super.receiveAudioInfo(sid, audioData, timeStamp, number);
        for (int i = 0; i < App.sConnectList.size(); i++) {
            ConnectInfo info = App.sConnectList.get(i);
            if (info != null && info.mByClientConnect && info.mClientSID == sid && mIsListener) {
                //c->d 发送音频 4 device收到音频数据传给解码器
                TUTKMedia.TK_getInstance().TK_decode_onReceiveAndPlayAudio(info.mAccountId, audioData, audioData.length, timeStamp);
                break;
            }
        }
    }


    @Override
    public void receiveVideoInfo(String uid, int avChannel, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {
        super.receiveVideoInfo(uid, avChannel, videoData, timeStamp, number, onlineNum, isIFrame);
        if(CallActivity.getinstance()!=null){
            ((CallActivity) CallActivity.getinstance()).disLoading();
        }
        for (int i = 0; i < App.sConnectList.size(); i++) {
            ConnectInfo info = App.sConnectList.get(i);
            if (info != null && !info.mByClientConnect && info.mDeviceUID.equalsIgnoreCase(uid)) {
                //d->c 发送视频 4  client收到视频数据传给解码器
                TUTKMedia.TK_getInstance().TK_decode_onReceiveVideoData(info.mAccountId, videoData, timeStamp, number, isIFrame);
                break;
            }
        }

    }


    @Override
    public void receiveAudioInfo(String uid, int avChannel, byte[] audioData, long timeStamp, int number) {
        super.receiveAudioInfo(uid, avChannel, audioData, timeStamp, number);
        for (int i = 0; i < App.sConnectList.size(); i++) {
            ConnectInfo info = App.sConnectList.get(i);
            if (info != null && !info.mByClientConnect && info.mDeviceUID.equalsIgnoreCase(uid) && mIsListener) {
                //d->c 发送音频 4 client收到音频数据传给解码层
                TUTKMedia.TK_getInstance().TK_decode_onReceiveAndPlayAudio(info.mAccountId, audioData, audioData.length, timeStamp);
                break;
            }
        }
    }


    @Override
    public void receiveIOCtrlDataInfo(String uid, int avChannel, final int avIOCtrlMsgType, byte[] data) {
        super.receiveIOCtrlDataInfo(uid, avChannel, avIOCtrlMsgType, data);
        Log.e("--","CallLogic receiveIOCtrlDataInfo:" + avIOCtrlMsgType );
        switch (avIOCtrlMsgType) {
            //在此页面call device 成功之后 开启双向
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP: {
                //在此页面call device 成功之后 开启双向
                CustomCommand.SMsgAVIoctrlCallResp.Struct struct = CustomCommand.SMsgAVIoctrlCallResp.parseToStruct(data);
                int answer = struct.answer;
                if (data[0] == 1) {
                    TUTKP2P.TK_getInstance().TK_client_startReceiveVideo(uid, avChannel, false);
                    TUTKP2P.TK_getInstance().TK_client_startListener(uid, avChannel);
                    TUTKP2P.TK_getInstance().TK_client_startSendVideo(uid, avChannel);
                    TUTKP2P.TK_getInstance().TK_client_startSpeaking(uid, avChannel);
                } else {
                    //添加历史记录 -- 主动打过去 对方拒接
                }
                if (answer == 1) {
                    //TODO
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDataBinding.userInfo.setVisibility(View.GONE);
                        }
                    });
                } else {
                }

            }
            break;
            case CustomCommand.IOTYPE_USER_IPCAM_SWITCH_AUDIO:
                ConnectInfo accountInfo = App.sConnectList.get(0);
                //停止视频编码
                TUTKMedia.TK_getInstance().TK_encode_unBindView();
                TUTKP2P.TK_getInstance().TK_client_stopSendVideo(accountInfo.mDeviceUID, accountInfo.mDeviceChannel);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("--","receiveIOCtrlDataInfo IOTYPE_USER_IPCAM_SWITCH_AUDIO:" + avIOCtrlMsgType );
                        mDataBinding.recyclerView.setVisibility(View.GONE);
                        mDataBinding.decodePreviewBig.setVisibility(View.GONE);
                    }
                });
                break;
            default: {

            }
        }
    }


    private void notifyAdapterData() {
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 播放铃音
     */
    public void startRing() {
        try {
            player = MediaPlayer.create(mActivity, R.raw.video_request);
            player.setLooping(true);//循环播放
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放铃音
     */
    public void stopRing() {
        try {
            if (null != player) {
                player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
