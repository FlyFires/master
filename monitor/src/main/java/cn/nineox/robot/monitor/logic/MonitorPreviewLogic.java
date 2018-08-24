package cn.nineox.robot.monitor.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.libTUTKMedia.CameraDecodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview;
import com.tutk.libTUTKMedia.CameraEncodePreview2;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.libTUTKMedia.inner.OnMediaListener;
import com.tutk.libTUTKMedia.utils.MediaCodecUtils;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.inner.OnP2PClientListener;
import com.tutk.p2p.inner.OnP2PDeviceListener;

import java.io.File;
import java.io.FileOutputStream;

import cn.nineox.robot.monitor.APP;
import cn.nineox.robot.monitor.MonitorView;
import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.ConnectInfo;
import cn.nineox.robot.monitor.common.tutk.CustomArrayList;
import cn.nineox.robot.monitor.common.tutk.CustomCommand;
import cn.nineox.robot.monitor.common.tutk.ThreadTimerClock;
import cn.nineox.xframework.core.android.log.Log;


public class MonitorPreviewLogic implements OnP2PClientListener, OnP2PDeviceListener {

    private ThreadTimerClock mThreadTotalTimer;

    private Context mContext;

    private MonitorView mContentView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //开始音频编码
                    TUTKMedia.TK_getInstance().TK_audio_startCapture(
                            App.AUDIO_SAMPLE_RATE,
                            App.AUDIO_BIT,
                            App.AUDIO_CODEC);
                    break;
            }

        }
    };

    public MonitorPreviewLogic(MonitorView view) {
        this.mContentView = view;
        this.mContext = mContentView.getContext();

    }


    /**
     * Bind.
     */
    public void bind() {
        TUTKP2P.TK_getInstance().TK_registerClientListener(this);
        TUTKP2P.TK_getInstance().TK_registerDeviceListener(this);
    }

    /**
     * Unbind.
     */
    public void unbind() {
        TUTKP2P.TK_getInstance().TK_unRegisterClientListener(this);
        TUTKP2P.TK_getInstance().TK_unRegisterDeviceListener(this);
    }

    public void initData() {
        bind();
        App.sConnectList.setOnListDataChangeListener(mOnListDataChangeListener);
        //App.sConnectList.get(0)
        TUTKMedia.TK_getInstance().TK_registerListener(mOnMediaListener);

//        //开始音频编码
//        TUTKMedia.TK_getInstance().TK_audio_startCapture(
//                App.AUDIO_SAMPLE_RATE,
//                App.AUDIO_BIT,
//                App.AUDIO_CODEC);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 2000);
        //开始音频解码
//        TUTKMedia.TK_getInstance().TK_decode_startDecodeAudio(
//                App.sConnectList.get(0).mAccountId,
//                App.AUDIO_SAMPLE_RATE,
//                App.AUDIO_BIT,
//                App.AUDIO_CODEC);
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
    public void receiveIOTCListenInfo(int i) {

    }

    @Override
    public void receiveAvServerStart(int i, int i1, int i2) {

    }

    @Override
    public void receiveSessionCheckInfo(int i, St_SInfo st_sInfo, int i1) {

    }

    @Override
    public void receiveStatusCheckInfo(int i, St_AvStatus st_avStatus, int i1) {

    }

    @Override
    public void receiveIOCtrlDataInfo(int i, int i1, byte[] bytes) {

    }

    @Override
    public void sendIOCtrlDataInfo(int i, int i1, int i2, byte[] bytes) {

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

    @Override
    public void receiveVideoInfo(int i, byte[] bytes, long l, int i1, int i2, boolean b) {

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
        Log.e("--", "TK_unRegisterListener:" + mOnMediaListener);
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

    private static OnMediaListener mOnMediaListener = new OnMediaListener() {

        //视频编码控件初始化完毕
        @Override
        public void onVideoEncodePreviewInit(CameraEncodePreview preview) {

            SharedPreferences sp = APP.instance.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
            boolean encodeSoft = sp.getBoolean(App.SP_ENCODE_SOFT, false);

            //绑定编码器
            TUTKMedia.TK_getInstance().TK_encode_bindView(preview);

            //设置预览缩放类型
            TUTKMedia.TK_getInstance().TK_preview_setScaleType(MediaCodecUtils.SCALE_ASPECT);

            //开始视频编码
            TUTKMedia.TK_getInstance().TK_preview_startCapture(MediaCodecUtils.MEDIA_CODEC_H264, false);

        }

        @Override
        public void onVideoEncodePreviewInit(CameraEncodePreview2 preview) {

        }


        private FileOutputStream mDecodeOutputSteam2;

        @Override
        public void onVideoEncodeCallback(byte[] data, int width, int height, boolean isIFrame, long timeStamp) {
            for (ConnectInfo info : App.sConnectList) {
                if (info.mByClientConnect) {
                    //d->c 发送视频 3 device将编码数据传给client
                    TUTKP2P.TK_getInstance().TK_device_onSendVideoData(info.mClientSID, data, isIFrame, timeStamp);
                }
            }

            //Log.e("--","onVideoEncodeCallback:" + mOnMediaListener  + "   " + App.sConnectList.size() );
//
//            if(mDecodeOutputSteam2 == null){
//                String path = Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/encode.h264";
//                if(!new File(Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/").exists()){
//                    new File( Environment.getExternalStorageDirectory().getPath() + "/MediaSDK/").mkdirs();
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
            SharedPreferences sp = APP.instance.getSharedPreferences(App.SP_SETTINGS, Context.MODE_PRIVATE);
            sp.edit().putBoolean(App.SP_ENCODE_SOFT, true).apply();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
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
                mContentView.destroy();
            }
        }
    };

    ThreadTimerClock.OnThreadListener mThreadListener = new ThreadTimerClock.OnThreadListener() {

        StringBuilder mStringBuilder = new StringBuilder();

        @Override
        public void onTime(String time) {
            //是否被回收
            if (mContentView == null) {
                return;
            }
            mStringBuilder.delete(0, mStringBuilder.length());
            mStringBuilder.append(" ")
                    .append(mContext.getString(R.string.text_time_duration))
                    .append(" ")
                    .append(time);
            //mView.setTotalTime(mStringBuilder.toString());
        }
    };


    @Override
    public void receiveConnectInfo(String s, int i, int i1) {

    }

    @Override
    public void receiveClientStartInfo(String s, int i, int i1) {

    }

    @Override
    public void receiveSessionCheckInfo(String s, St_SInfo st_sInfo, int i) {

    }

    @Override
    public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int i) {

    }

    @Override
    public void receiveIOCtrlDataInfo(String s, int i, int i1, byte[] bytes) {

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


    public void onDestory() {
        mHandler.removeMessages(0);
    }
}
