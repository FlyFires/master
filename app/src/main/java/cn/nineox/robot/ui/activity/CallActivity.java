package cn.nineox.robot.ui.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityCallBinding;
import cn.nineox.robot.logic.CallLogic;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.utils.LogUtil;
import cn.nineox.robot.utils.SpeakerUtil;
import cn.nineox.xframework.core.common.utils.DisplayUtil;

/**
 * Created by me on 17/10/31.
 */

public class CallActivity extends BasicActivity<ActivityCallBinding> {

    private DeviceBean mDeviceBean;
    private MediaPlayer player;
    private long mStartTime;

    public CallLogic mLogic;

    private PowerManager.WakeLock mWakeLock;
    private static Activity activity;
    public static Activity getinstance(){
        return  activity ;
    }
    private final static int    COUNTS   = 3;//点击次数
    private final static long   DURATION = 1000;//规定有效时间
    private              long[] mHits    = new long[COUNTS];

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.btn_layout:
                    mViewDataBinding.btnLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.debug("call onCreate");
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        mLogic = new CallLogic(this, mViewDataBinding);

        mViewDataBinding.setClickListener(this);
        mDeviceBean = (DeviceBean) getIntent().getSerializableExtra(Const.EXTRA_DEVICE);
        //intP2PExtra();
        activity = this;

    }
    public  void showrtt(final  int rtt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewDataBinding.rttTv.setText("rtt :"+rtt);
            }
        });

    }

    public void disLoading(){
        if(mViewDataBinding.CallCenterLoading.getVisibility()==View.VISIBLE){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewDataBinding.CallCenterLoading.setVisibility(View.GONE);
                }
            });
        }
    }

    private void intP2PExtra() {
        mLogic = new CallLogic(this, mViewDataBinding);
        mViewDataBinding.setData(mDeviceBean);
        if (mDeviceBean != null) {
            mLogic.equipmentGet(mDeviceBean.getMid());
        }
    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);

        mStartTime = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(R.id.btn_layout, 5000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_call;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swith_audiocall:
                //mJoined = !mJoined;
                mLogic.switchAudioMode();
                break;
            case R.id.accpetCall:
//            case R.id.accpetCall_audio:
//                mLogic.answer();
//                break;
            case R.id.hangup_audio:
            case R.id.hangup:
                finish();
                break;

            case R.id.swith_camera:
                mLogic.switchCamera();
                break;
            case R.id.mute_btn:
                break;
            case R.id.mianti:
                mViewDataBinding.mianti.setSelected(!mViewDataBinding.mianti.isSelected());
                if (mViewDataBinding.mianti.isSelected()) {
                    SpeakerUtil.closeSpeaker(this);
                } else {
                    SpeakerUtil.openSpeaker(this);
                }

                break;
            case R.id.audio_layout:
            case R.id.root:
            case R.id.decode_preview_big:
                mHandler.removeMessages(R.id.btn_layout);
                mViewDataBinding.btnLayout.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(R.id.btn_layout, 5000);
                //显示rtt
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    if(LogUtil.show_DEBUG){
                        mViewDataBinding.rttTv.setVisibility(mViewDataBinding.rttTv.getVisibility() == View.VISIBLE ?
                                View.GONE : View.VISIBLE);
                    }

                }

                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock = DisplayUtil.wakeLock(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        mLogic.callOff();
        super.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLogic.unbind();
        activity = null;
        DisplayUtil.releaseWakeLock(mWakeLock);

    }


}
