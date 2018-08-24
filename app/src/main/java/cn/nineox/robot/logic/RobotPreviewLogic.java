package cn.nineox.robot.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;

import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.libTUTKMedia.CameraDecodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview2;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.libTUTKMedia.inner.OnMediaListener;
import com.tutk.libTUTKMedia.utils.MediaCodecUtils;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.utils.P2PUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import cn.nineox.robot.PluginAPP;
import cn.nineox.robot.R;
import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.tutk.ConnectInfo;
import cn.nineox.robot.common.tutk.CustomArrayList;
import cn.nineox.robot.common.tutk.CustomCommand;
import cn.nineox.robot.common.tutk.ILiveViewActivityPresenter;
import cn.nineox.robot.common.tutk.TUTKP2PLogic;
import cn.nineox.robot.common.tutk.ThreadAutoConnect;
import cn.nineox.robot.common.tutk.ThreadTimerClock;
import cn.nineox.robot.databinding.ActivityRobotPreviewBinding;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.EquipmentGet;
import cn.nineox.robot.ui.activity.CallActivity;
import cn.nineox.robot.ui.activity.ConversationDetailActivity;
import cn.nineox.robot.ui.activity.RobotPreviewActivity;
import cn.nineox.robot.ui.activity.ShareActivity;
import cn.nineox.robot.utils.StringUtils;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.AlbumNotifyHelper;
import cn.nineox.xframework.core.common.utils.DateUtil;

public class RobotPreviewLogic extends TUTKP2PLogic<ActivityRobotPreviewBinding> implements ILiveViewActivityPresenter {


    private String TAG = "RobotPreviewLogic";
    private int mCallBySID = -1;


    private static final long BITRATE_DOWN_TIME = 10 * 1000;


    private String mAccountID;//对方id
    private String mCallUID;//对方UID

    private boolean mIsDoubleCall;//是否双向

    private static MediaScannerConnection sMediaScannerConnection;

    private boolean mIsCallTo;//是否主动打过去

    private ThreadAutoConnect mThreadAutoConnect;//自动连线线程

    private ArrayList<CustomCommand.StructAccountInfo> mOtherUIDList;//其它uid

    public boolean mIsListener = true;//是否监听
    public boolean mIsSpeak = true;//是否对讲
    public boolean mIsRecord = false;//是否录像
    public  boolean mIsSendVideo  = true;//是否传视频

    private long mBitrateDownTime;//降码流时间 避免降低过快

    private ThreadTimerClock mThreadTotalTimer;//通话总时间计时
    private ThreadTimerClock mThreadRecordTimer;//录像时间计时

    private ThreadAutoConnect mAutoConnect;//邀请线程

    private BaseBindingAdapter mAdapter;

    private int mRecordTime;

    private Timer mTimer;

    public RobotPreviewLogic(Activity activity, ActivityRobotPreviewBinding binding) {
        super(activity, binding);
    }


    public void initData() {
        bind();
        App.sConnectList.setOnListDataChangeListener(mOnListDataChangeListener);
        TUTKMedia.TK_getInstance().TK_registerListener(mOnMediaListener);

        Intent intent = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
        boolean isDoubleCall = false;
        if (bundle != null) {
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

        if (App.sConnectList.size() == 0) {
            mActivity.finish();
        }
        /* 3 如果是打过去 发送p2p视频／音频请求 */
        ConnectInfo accountInfo = App.sConnectList.get(0);
        Log.e("--", "ConnectInfo:" + accountInfo);
        mDataBinding.decodePreview.setTag(accountInfo.mAccountId);
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

        /* 4 如果是被邀请 则还需要连接其它uid并且 发送p2p视频／音频请求 */
        assert bundle != null;
        ArrayList<CustomCommand.StructAccountInfo> otherUID = bundle.getParcelableArrayList(App.BUNDLE_INFO_LIST);
        if (otherUID != null) {
            for (CustomCommand.StructAccountInfo info : otherUID) {
                TUTKP2P.TK_getInstance().TK_client_connect(info.UID, App.DEVICE_PASSWORD, App.DEVICE_CHANNEL);
                TUTKP2P.TK_getInstance().TK_client_connect(
                        info.UID, App.DEVICE_PASSWORD, App.DEVICE_CHANNEL);
                TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(info.UID, App.DEVICE_CHANNEL,
                        CustomCommand.IOTYPE_USER_IPCAM_CALL_REQ,
                        CustomCommand.SMsgAVIoctrlCallReq.parseContent(
                                App.sSelfAccountID,
                                App.sSelfUID,
                                2,
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
    }


    @Override
    public void receiveIOCtrlDataInfo(int sid, int avIOCtrlMsgType, byte[] data) {
        super.receiveIOCtrlDataInfo(sid, avIOCtrlMsgType, data);
        switch (avIOCtrlMsgType) {
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_END: {

                CustomCommand.SMsgAVIoctrlCallEnd.Struct struct = CustomCommand.SMsgAVIoctrlCallEnd.parseToStruct(data);
                if (struct.myID.equals(mAccountID)) {
                    //添加历史记录 -- 别人打过来 自己还未接 别人挂断
                    mActivity.finish();
                }
            }
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
            mActivity.finish();
        }
    }

    @Override
    public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {

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

        //changeDASA(result);
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
            //File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(),App.APP_PATH_SNAPSHOT + accountID);
//            if (!path.exists()) {
//                path.mkdir();
//            }
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filepath = path + File.separator + StringUtils.getFileNameWithTime() + ".JPG";
            Log.e("--", "snapshot:" + filepath);
            TUTKMedia.TK_getInstance().TK_video_snapShot(
                    accountID,
                    filepath);

            File imFile = new File(filepath);
            if (imFile.exists()) {
                insertToSystem(mActivity, imFile.getAbsolutePath(), "image/*");
                new Toastor(mActivity).showSingletonToast("照片已保存.请到相册查看");
            }
        }
    }

    @Override
    public void startRecord() {
        if (App.sConnectList.size() > 0) {
            mIsRecord = true;
            String accountID = App.sConnectList.get(0).mAccountId;
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mDataBinding.recordVideoIv.setImageResource(R.drawable.ic_video);
            mTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecordTime++;
                            mDataBinding.recordTimeTx.setText(DateUtil.secToHHmmss(mRecordTime));
                        }
                    });

                }
            };
            mTimer.schedule(timerTask, 0, 1000);
            mDataBinding.recordTimeLayout.setVisibility(View.VISIBLE);


            String filepath = path + File.separator + StringUtils.getFileNameWithTime() + ".mp4";
            mDataBinding.recordVideoIv.setTag(filepath);
            TUTKMedia.TK_getInstance().TK_video_startRecord(
                    accountID,filepath,
                    mIsListener
                    );
        }
    }

    @Override
    public void stopRecord(boolean isRecordSuccess) {
        if (App.sConnectList.size() > 0) {

            TUTKMedia.TK_getInstance().TK_video_stopRecord(App.sConnectList.get(0).mAccountId);
            mIsRecord = false;

            mDataBinding.recordVideoIv.setImageResource(R.drawable.ic_video);
            mTimer.cancel();
            //mP2PKit.StopRecordPeerVideo();
            mRecordTime = 0;
            mDataBinding.recordTimeLayout.setVisibility(View.GONE);

            String filepath = (String) mDataBinding.recordVideoIv.getTag();
            new Toastor(mActivity).showSingletonToast("视频已保存.请到" + filepath + "下查看");
            insertToSystem(mActivity, filepath, "video/*");
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

    public void callOff() {

        /* 1 停止大计时线程／自动连线线程 */

        long duringTime = 0;
        sendIOCtrl(CustomCommand.FEET_MOTOR_STOP);
        sendIOCtrl(CustomCommand.HEAD_MOTOR_STOP);
        //stop record
        if (mIsRecord) {
            //stopRecord(true);
            mIsRecord = false;
        }

        if (mThreadRecordTimer != null) {
            mThreadRecordTimer.setThreadListener(null);
            mThreadRecordTimer.stopThread();
            mThreadRecordTimer = null;
        }

        //stop ThreadTimer
        if (mThreadTotalTimer != null) {
            duringTime = mThreadTotalTimer.getTime();
            mThreadTotalTimer.setThreadListener(null);
            mThreadTotalTimer.stopThread();
            mThreadTotalTimer = null;
        }

        //stop ThreadAutoConnect
        if (mAutoConnect != null) {
            mAutoConnect.setThreadListener(null);
            mAutoConnect.stopThread();
            mAutoConnect = null;
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

        SystemClock.sleep(100);
        for (ConnectInfo accountInfo : App.sConnectList) {
            if (accountInfo.mByClientConnect) {
                TUTKP2P.TK_getInstance().TK_device_disConnect(accountInfo.mClientSID);
            } else {
                TUTKP2P.TK_getInstance().TK_client_disConnect(accountInfo.mDeviceUID);
            }
        }

        /* 5 停止列表的小计时线程 移除列表数据监听 清空列表 */
        for (ConnectInfo info : App.sConnectList) {
            //停止计时
            if (info.mThreadTimer != null) {
                info.mThreadTimer.setThreadListener(null);
                info.mThreadTimer.stopThread();
                info.mThreadTimer = null;
            }
            info = null;
        }

        App.sConnectList.setOnListDataChangeListener(null);
        App.sConnectList.clear();

        App.sHasInvitePermission = false;

        mActivity.finish();

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
		/* 动态码流 */

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
            //开始视频编码
            TUTKMedia.TK_getInstance().TK_preview_startCapture(App.VIDEO_FORMAT, encodeSoft);
            //设置预览缩放类型
            TUTKMedia.TK_getInstance().TK_preview_setScaleType(MediaCodecUtils.SCALE_ASPECT_FILL);
        }

        @Override
        public void onVideoEncodePreviewInit(CameraEncodePreview2 preview) {

        }

        @Override
        public void onVideoEncodeCallback(byte[] data, int width, int height, boolean isIFrame, long timeStamp) {
            for (ConnectInfo info : App.sConnectList) {
//                if (info != null) {
//                    if (info.mByClientConnect) {
//                        //d->c 发送视频 3 device将编码数据传给client
//                        TUTKP2P.TK_getInstance().TK_device_onSendVideoData(info.mClientSID, data, isIFrame, timeStamp);
//                    } else {
//                        //c->d 发送视频 3 client将编码数据传给device
//                        TUTKP2P.TK_getInstance().TK_client_onSendVideoData(info.mDeviceUID, info.mDeviceChannel, data, isIFrame, timeStamp);
//                    }
//                }
            }
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
                //stopRecord(false);
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
            for (ConnectInfo info : App.sConnectList) {
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
                    //new Toastor(mActivity).showSingletonToast(nickName + " " + mActivity.getString(R.string.text_history_incoming));
                }
            });

            if (index == 0) {
                SharedPreferences sp = mActivity.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
                boolean isSoftDecode = sp.getBoolean(App.SP_DECODE_SOFT, false);
                TUTKMedia.TK_getInstance().TK_decode_startDecodeVideo(mDataBinding.decodePreview, accountInfo.mAccountId, App.VIDEO_FORMAT, isSoftDecode);

            }
            /* 1 开始音频解码 */
            TUTKMedia.TK_getInstance().TK_decode_startDecodeAudio(accountInfo.mAccountId,
                    App.AUDIO_SAMPLE_RATE,
                    App.AUDIO_BIT,
                    App.AUDIO_CODEC);

            /* 2 添加小窗口创建视频解码器 */
            Log.e("--", "onAdd:" + App.sConnectList.size());
            if (index != 0) {
                //mView.notifyItemAdd(index);
                notifyAdapterData();
            }

            /* 3 如果开启了自动连线 则停止 */
            if (mAutoConnect != null) {
                //更新转圈ui
                //mView.stopInvite();
                mAutoConnect.setThreadListener(null);
                mAutoConnect.stopThread();
                mAutoConnect = null;
            }

        }

        @Override
        public void onRemove(int index, ConnectInfo accountInfo) {

            /* 1 停止计时线程 */
            if (accountInfo.mThreadTimer != null) {
                accountInfo.mThreadTimer.setThreadListener(null);
                accountInfo.mThreadTimer.stopThread();
                accountInfo.mThreadTimer = null;
            }

            /* 2 停止Media的解码 */
            TUTKMedia.TK_getInstance().TK_decode_stopDecodeAudio(accountInfo.mAccountId);
            TUTKMedia.TK_getInstance().TK_decode_stopDecodeVideo(accountInfo.mAccountId);
            TUTKMedia.TK_getInstance().TK_video_stopRecord(accountInfo.mAccountId);

            Log.i(TAG, "onRemove index " + index);

            /* 3 如果没有连线 则退出页面 */
            if (App.sConnectList.size() == 0) {
                callOff();
                mActivity.finish();
                return;
            }

            /* 4 如果不是第一个 则移除小窗口 如果是第一个 则选择第一个切换到大窗口 */
            if (index != 0) {
                //mView.notifyItemRemoved(index);
                notifyAdapterData();
            } else {
                //mView.notifyItemRemoved(1);
                TUTKMedia.TK_getInstance().TK_decode_setDecodeVideoTag(accountInfo.mAccountId, App.sConnectList.get(0).mAccountId);
                //mView.setNickName(App.sConnectList.get(0).mNickName);
                App.sConnectList.get(0).mThreadTimer.setThreadListener(mItemTimeListener);
            }

            accountInfo = null;
        }
    };


    @Override
    public void receiveVideoInfo(int sid, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {
        super.receiveVideoInfo(sid, videoData, timeStamp, number, onlineNum, isIFrame);

        for (ConnectInfo info : App.sConnectList) {
            if (info != null && info.mByClientConnect && info.mClientSID == sid) {
                //c->d 发送视频 4 device收到视频数据传给解码器
                TUTKMedia.TK_getInstance().TK_decode_onReceiveVideoData(info.mAccountId, videoData, timeStamp, number, isIFrame);
                break;
            }
        }
    }

    @Override
    public void receiveAudioInfo(int sid, byte[] audioData, long timeStamp, int number) {
        for (ConnectInfo info : App.sConnectList) {
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
        if(RobotPreviewActivity.getinstance()!=null){
            ((RobotPreviewActivity) RobotPreviewActivity.getinstance()).disLoading();
        }
        for (ConnectInfo info : App.sConnectList) {
            if (info != null && !info.mByClientConnect && info.mDeviceUID.equalsIgnoreCase(uid)) {
                //d->c 发送视频 4  client收到视频数据传给解码器
                TUTKMedia.TK_getInstance().TK_decode_onReceiveVideoData(info.mAccountId, videoData, timeStamp, number, isIFrame);
                break;
            }
        }
    }


    @Override
    public void receiveAudioInfo(String uid, int avChannel, byte[] audioData, long timeStamp, int number) {
        for (ConnectInfo info : App.sConnectList) {
            if (info != null && !info.mByClientConnect && info.mDeviceUID.equalsIgnoreCase(uid) && mIsListener) {
                //d->c 发送音频 4 client收到音频数据传给解码层
                TUTKMedia.TK_getInstance().TK_decode_onReceiveAndPlayAudio(info.mAccountId, audioData, audioData.length, timeStamp);


                break;
            }
        }
    }

    @Override
    public void receiveIOCtrlDataInfo(String uid, int avChannel, int avIOCtrlMsgType, byte[] data) {
        super.receiveIOCtrlDataInfo(uid, avChannel, avIOCtrlMsgType, data);
        switch (avIOCtrlMsgType) {
            //在此页面call device 成功之后 开启双向
            case CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP: {
                //在此页面call device 成功之后 开启双向
                CustomCommand.SMsgAVIoctrlCallResp.Struct struct = CustomCommand.SMsgAVIoctrlCallResp.parseToStruct(data);
                int answer = struct.answer;
                if (data[0] == 1) {
                    TUTKP2P.TK_getInstance().TK_client_startReceiveVideo(uid, avChannel, false);
                    TUTKP2P.TK_getInstance().TK_client_startListener(uid, avChannel);
                    SystemClock.sleep(100);
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
                            //mDataBinding.topPanel.setVisibility(View.GONE);
                        }
                    });

//                    Intent intent = new Intent(mActivity, LandscapeLiveViewActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, mIsDoubleCall);
//                    bundle.putBoolean(App.BUNDLE_IS_CALL_TO, mIsCallTo);
//                    intent.putExtras(bundle);
////					mActivity.startActivity(intent);
//                    ((Activity) mActivity).startActivityForResult(intent, App.INTENT_REQUEST_CALL_TO);
//                    ((Activity) mActivity).setResult(RESULT_OK);
                } else {
                    //添加历史记录 -- 主动打过去 对方拒接
                    mActivity.finish();
                }
                //mActivity.finish();

            }
            break;
            default: {

            }
        }
    }


    public void sendIOCtrl(int command) {

        Log.e("--", "sendIOCtrl:" + command);
        if(App.sConnectList.size() == 0){
            return;
        }
        ConnectInfo accountInfo = App.sConnectList.get(0);

        if (accountInfo.mByClientConnect) {
            TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(
                    accountInfo.mClientSID,
                    command,
                    CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
        } else {
            TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(
                    accountInfo.mDeviceUID,
                    accountInfo.mDeviceChannel,
                    command,
                    CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
        }
//        TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(
//                accountInfo.mClientSID,
//                command,
//                CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
    }


    private void notifyAdapterData() {
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }

    }


    public void equipmentGet(String mid) {
        EntityRequest request = new EntityRequest(Const.URL_EQUIPMENT_GET, EquipmentGet.class);
        request.add("mid", mid);
        execute(request, new ResponseListener<EquipmentGet>() {

            @Override
            public void onSucceed(int what, Result<EquipmentGet> result) {
                super.onSucceed(what, result);
                //mDataBinding.setEquipment(result.getResult());
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast("获取设备信息失败," + error);
                //dialog.dismiss();
            }
        });
    }


    /**
     * 免提
     *
     * @param context
     */
    private void toggleSpeaker(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setSpeakerphoneOn(!am.isSpeakerphoneOn());
    }


    /**
     * 图片插入MediaProvider,通知图库更新
     *
     * @param context  context
     * @param filePath 文件路径
     */
    public static String insertToSystem(Context context, final String filePath, final String type) {
        String result = null;
        try {
            if (type.equals("image/*")) {
                result = MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, new File(filePath).getName(), null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                } else {
                    //4.4开始不允许发送"Intent.ACTION_MEDIA_MOUNTED"广播, 否则会出现: Permission Denial: not allowed to send broadcast android.intent.action.MEDIA_MOUNTED from pid=15410, uid=10135
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    PluginAPP.getInstance().sendBroadcast(intent);
                }

            } else if (type.equals("video/*")) {
//                File videoFile = new File(filePath);
//                // 将视频加入到媒体库
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(MediaStore.Video.Media.DATA, System.currentTimeMillis());
//                contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis());
//                contentValues.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis());
//                contentValues.put(MediaStore.Video.Media.TITLE, videoFile.getName());
//                contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.getName());
//                Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
//                android.util.Log.e("RobotPreView", " insertToSystem video uri: " + uri.toString());
                AlbumNotifyHelper.insertVideoToMediaStore(context, filePath, System.currentTimeMillis(), 0);
            }


            // 通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(filePath));
            intent.setData(uri);
            context.sendBroadcast(intent);

            sMediaScannerConnection = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    sMediaScannerConnection.scanFile(filePath, type);
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    sMediaScannerConnection.disconnect();
                }
            });
            sMediaScannerConnection.connect();
        } catch (Exception e) {
        }


        return result;
    }

    public void showMore() {
        if (App.sConnectList.size() > 0) {
            String accountID = App.sConnectList.get(0).mAccountId;
            Intent intent = new Intent(mActivity, ConversationDetailActivity.class);
            DeviceBean d = new DeviceBean();
            d.setMid(accountID);
            intent.putExtra(Const.EXTRA_DEVICE, d);
            mActivity.startActivity(intent);
        }
    }

    public void share() {
        mActivity.startActivity(new Intent(mActivity, ShareActivity.class));
    }


    public void onDestory() {
        if (sMediaScannerConnection != null) {
            sMediaScannerConnection.disconnect();
        }
    }

}
