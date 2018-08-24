package cn.nineox.robot.monitor.logic;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.tutk.IOTC.St_AvStatus;
import com.tutk.libTUTKMedia.CameraDecodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview2;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.libTUTKMedia.inner.OnMediaListener;
import com.tutk.libTUTKMedia.utils.MediaCodecUtils;
import com.tutk.p2p.TUTKP2P;

import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.ConnectInfo;
import cn.nineox.robot.monitor.common.tutk.CustomAlertDialog;
import cn.nineox.robot.monitor.common.tutk.CustomArrayList;
import cn.nineox.robot.monitor.common.tutk.CustomCommand;
import cn.nineox.robot.monitor.common.tutk.ThreadTimerClock;
import cn.nineox.robot.monitor.databinding.ActivityRobotPreviewBinding;


public class RobotPreviewLogic extends TUTKP2PLogic<ActivityRobotPreviewBinding> {

    private ThreadTimerClock mThreadTotalTimer;

    public RobotPreviewLogic(Activity activity, ActivityRobotPreviewBinding binding) {
        super(activity, binding);
    }


    public void initData() {
        bind();
        App.sConnectList.setOnListDataChangeListener(mOnListDataChangeListener);
        //App.sConnectList.get(0)
        TUTKMedia.TK_getInstance().TK_registerListener(mOnMediaListener);

        //开始音频编码
        TUTKMedia.TK_getInstance().TK_audio_startCapture(
                App.AUDIO_SAMPLE_RATE,
                App.AUDIO_BIT,
                App.AUDIO_CODEC);
        //开始音频解码
        TUTKMedia.TK_getInstance().TK_decode_startDecodeAudio(
                App.sConnectList.get(0).mAccountId,
                App.AUDIO_SAMPLE_RATE,
                App.AUDIO_BIT,
                App.AUDIO_CODEC);
        //开始计时
        App.sConnectList.get(0).mThreadTimer = new ThreadTimerClock();
        App.sConnectList.get(0).mThreadTimer.start();

        mThreadTotalTimer = new ThreadTimerClock();
        mThreadTotalTimer.setThreadListener(mThreadListener);
        mThreadTotalTimer.start();
    }

    @Override
    public void receiveOnLineInfo(String s, int i) {

    }

    @Override
    public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {

    }

    @Override
    public void receiveAudioInfo(int sid, byte[] audioData, long timeStamp, int number) {
        //super.receiveAudioInfo(sid, audioData, timeStamp, number);
        for (ConnectInfo info : App.sConnectList) {
            if (info != null && info.mByClientConnect && info.mClientSID == sid) {
                //c->d 发送音频 4 device收到音频数据传给解码器
                TUTKMedia.TK_getInstance().TK_decode_onReceiveAndPlayAudio(info.mAccountId, audioData, audioData.length, timeStamp);
                break;
            }
        }
    }

    public void callOff() {

        /*  停止总计时器 */
        if (mThreadTotalTimer != null) {
            mThreadTotalTimer.setThreadListener(null);
            mThreadTotalTimer.stopThread();
            mThreadTotalTimer = null;
        }

        /* stop all media */
        //停止视频编码
        TUTKMedia.TK_getInstance().TK_encode_unBindView();
//		TUTKMedia.TK_getInstance().TK_preview_stopCapture();
        //停止音频编码
        TUTKMedia.TK_getInstance().TK_audio_stopCapture();
        //停止音频解码
        TUTKMedia.TK_getInstance().TK_decode_stopDecodeAllAudio();
        //取消监听
        TUTKMedia.TK_getInstance().TK_unRegisterListener(mOnMediaListener);

        /* stop all p2p */
        for (ConnectInfo accountInfo : App.sConnectList) {
            TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(
                    accountInfo.mClientSID,
                    CustomCommand.IOTYPE_USER_IPCAM_CALL_END,
                    CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));

            //添加历史记录 -- 别人打过来 接通后自己挂断
        }

        SystemClock.sleep(100);

        for (ConnectInfo accountInfo : App.sConnectList) {
            if (accountInfo.mByClientConnect) {
                TUTKP2P.TK_getInstance().TK_device_disConnect(accountInfo.mClientSID);
            } else {
                TUTKP2P.TK_getInstance().TK_client_disConnect(accountInfo.mDeviceUID);
            }
        }

//		TUTKP2P.TK_getInstance().TK_client_disConnectAllDevice();
//		TUTKP2P.TK_getInstance().TK_device_disConnectAllClient();;

        App.sConnectList.setOnListDataChangeListener(null);
        App.sConnectList.clear();
    }

    public void rejectByPosition(final int position) {

        if (position >= App.sConnectList.size()) {
            return;
        }

        final ConnectInfo accountInfo = App.sConnectList.get(position);

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(mActivity,
                null,
                mActivity.getString(R.string.tips_reject_id) + " " + accountInfo.mNickName,
                mActivity.getString(R.string.text_cancel),
                mActivity.getString(R.string.text_ok));
        customAlertDialog.setOnDialogClickListener(new CustomAlertDialog.OnDialogClickLister() {
            @Override
            public void leftClick(DialogInterface dialog) {

            }

            @Override
            public void rightClick(DialogInterface dialog) {
                if (accountInfo.mByClientConnect) {
                    TUTKP2P.TK_getInstance().TK_device_disConnect(accountInfo.mClientSID);
                }

                App.sConnectList.remove(accountInfo);
            }
        });
        customAlertDialog.show();
    }

    private OnMediaListener mOnMediaListener = new OnMediaListener() {

        //视频编码控件初始化完毕
        @Override
        public void onVideoEncodePreviewInit(CameraEncodePreview preview) {

            SharedPreferences sp = mActivity.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
            boolean encodeSoft = sp.getBoolean(App.SP_ENCODE_SOFT, false);

            //绑定编码器
            TUTKMedia.TK_getInstance().TK_encode_bindView(preview);

            //设置预览缩放类型
            TUTKMedia.TK_getInstance().TK_preview_setScaleType(MediaCodecUtils.SCALE_ASPECT_FILL);

            //开始视频编码
            TUTKMedia.TK_getInstance().TK_preview_startCapture(App.VIDEO_FORMAT, encodeSoft);

        }

        @Override
        public void onVideoEncodePreviewInit(CameraEncodePreview2 preview) {

        }

        @Override
        public void onVideoEncodeCallback(byte[] data, int width, int height, boolean isIFrame, long timeStamp) {
            for (ConnectInfo info : App.sConnectList) {
                if (info.mByClientConnect) {
                    //d->c 发送视频 3 device将编码数据传给client
                    TUTKP2P.TK_getInstance().TK_device_onSendVideoData(info.mClientSID, data, isIFrame, timeStamp);
                }
            }
        }

        @Override
        public void onVideoEncodeSnapshot(boolean b) {

        }


        @Override
        public void onVideoDecodePreviewInit(CameraDecodePreview preview) {

        }

        @Override
        public void onVideoDecodeCallback(Object tag, byte[] data, int width, int height) {

        }

        @Override
        public void onVideoDecodeSnapshot(Object o, boolean b) {

        }


        @Override
        public void onVideoDecodeRecord(Object tag, boolean success) {

        }

        @Override
        public void onVideoDecodeUnSupport(CameraDecodePreview preview) {

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
            for (ConnectInfo info : App.sConnectList) {
                if (info != null && info.mByClientConnect) {
                    //d->c 发送音频 3 device将编码数据传给client
                    TUTKP2P.TK_getInstance().TK_device_onSendAudioData(info.mClientSID, data, encodeLength, timeStamp);
                }
            }
        }

        @Override
        public void onAudioDecodeCallback(Object tag, byte[] data, int encodeLength, int audioID, int simpleRate, int bit) {

        }
    };

    /* addHistoryToServer/remove for App.sConnectList --> MainActivity 完成后回调于此*/
    private CustomArrayList.OnListDataChangeListener mOnListDataChangeListener = new CustomArrayList.OnListDataChangeListener() {
        @Override
        public void onAdd(int index, ConnectInfo accountInfo) {
            //mView.notifyItemAdd(index);
            //开始音频解码
            TUTKMedia.TK_getInstance().TK_decode_startDecodeAudio(
                    accountInfo.mAccountId,
                    App.AUDIO_SAMPLE_RATE,
                    App.AUDIO_BIT,
                    App.AUDIO_CODEC);
            //开始计时
            accountInfo.mThreadTimer = new ThreadTimerClock();
            accountInfo.mThreadTimer.start();
        }

        @Override
        public void onRemove(int index, ConnectInfo accountInfo) {
            //mView.notifyItemRemoved(index);

            //停止音频解码
            TUTKMedia.TK_getInstance().TK_decode_stopDecodeAudio(accountInfo.mAccountId);
            //添加历史记录 -- 别人打过来 通话后别人挂断
            long duringTime = 0;
            if (accountInfo.mThreadTimer != null) {
                duringTime = accountInfo.mThreadTimer.getTime();
                accountInfo.mThreadTimer.stopThread();
                accountInfo.mThreadTimer = null;
            }

            //如果没有连线 则退出页面
            if (App.sConnectList.size() == 0) {
                callOff();
                ((Activity) mActivity).finish();
            }
        }
    };

    ThreadTimerClock.OnThreadListener mThreadListener = new ThreadTimerClock.OnThreadListener() {

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


    @Override
    public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int i) {

    }

    @Override
    public void receiveAudioInfo(String s, int i, byte[] bytes, long l, int i1) {

    }
}
