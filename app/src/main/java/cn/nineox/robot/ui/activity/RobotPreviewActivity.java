package cn.nineox.robot.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tutk.libTUTKMedia.TUTKMedia;
import com.tutk.libTUTKMedia.utils.MediaCodecUtils;
import com.tutk.p2p.TUTKP2P;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import cn.nineox.robot.R;
import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.common.tutk.ConnectInfo;
import cn.nineox.robot.common.tutk.CustomCommand;
import cn.nineox.robot.databinding.ActivityRobotPreviewBinding;
import cn.nineox.robot.logic.RobotPreviewLogic;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.utils.DateUtil;
import cn.nineox.xframework.core.common.utils.SdCardUtil;
import cn.nineox.xframework.core.weiget.TitleBar;

/**
 * Created by me on 17/10/31.
 */

public class RobotPreviewActivity extends BasicActivity<ActivityRobotPreviewBinding> {


    private boolean mJoined = false;
    MediaPlayer player;

    private boolean mIsRecording;


    private AlertDialog dialog;

    private int mRecordTime;

    private Timer mTimer;

    private PowerManager.WakeLock wakeLock;

    private String mRecordViewPath;

    private RobotPreviewLogic sLogic;
    private static Activity activity;
    public static Activity getinstance(){
        return  activity ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        activity = this;
        sLogic = new RobotPreviewLogic(this, mViewDataBinding);
        sLogic.initData();
    }


    @NonNull
    @Override
    protected void createViewBinding() {

        //mViewDataBinding.titleBar.setTitle(TextUtils.isEmpty(mDeviceBean.getMidName())?"机器人":mDeviceBean.getMidName());
        mViewDataBinding.titleBar.setTitleColor(Color.WHITE);
        mViewDataBinding.titleBar.setLeftImageResource(R.drawable.ic_back2);
        mViewDataBinding.titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sLogic.callOff();
                finish();
            }
        });

 /*       mViewDataBinding.titleBar.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return "";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_share;
            }

            @Override
            public void performAction(View view) {
                sLogic.share();
            }
        });

        mViewDataBinding.titleBar.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return "";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_more2;
            }

            @Override
            public void performAction(View view) {
                sLogic.showMore();
            }
        });*/
        mViewDataBinding.setClickListener(this);
        mViewDataBinding.getRoot().setBackgroundColor(getResources().getColor(R.color.grgray));

        //mViewDataBinding.controllCenter.setOnTouchListener(onTouchListener);
        mViewDataBinding.headControllDown.setOnTouchListener(onTouchListener);
        mViewDataBinding.headControllUp.setOnTouchListener(onTouchListener);
        mViewDataBinding.headControllLeft.setOnTouchListener(onTouchListener);
        mViewDataBinding.headControllRight.setOnTouchListener(onTouchListener);
        mViewDataBinding.feetControllDown.setOnTouchListener(onTouchListener);
        mViewDataBinding.feetControllUp.setOnTouchListener(onTouchListener);
        mViewDataBinding.feetControllLeft.setOnTouchListener(onTouchListener);
        mViewDataBinding.feetControllRight.setOnTouchListener(onTouchListener);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_robot_preview;
    }

    public void disLoading(){

        if(mViewDataBinding.previewCenterLoading.getVisibility()==View.VISIBLE){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewDataBinding.previewCenterLoading.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_video_iv:
                if (sLogic.mIsRecord) {
                    sLogic.stopRecord(true);
                } else {
                    sLogic.startRecord();
                }
                break;
            case R.id.audio_call:
//                Intent intent = new Intent(RobotPreviewActivity.this, CallActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("userid", APPDataPersistent.getInstance().getLoginInfoBean().getP2PUid());
//                bundle.putString("callid", mCallid);
//                bundle.putBoolean("p2p_push", true);
//                bundle.putBoolean("is_host", true);
//                bundle.putInt("p2p_mode", 0);
//                bundle.putSerializable(Const.EXTRA_DEVICE, mDeviceBean);
//                intent.putExtras(bundle);
//                RobotPreviewActivity.this.startActivity(intent);
//                finish();
                final ConnectInfo accountInfo = App.sConnectList.get(0);
                sLogic.callOff();


                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(RobotPreviewActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                        .setTipWord("正在为您接通，请稍后..")
                        .create();

                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(RobotPreviewActivity.this, WaitCallActivity.class);
                                bundle.putString(App.BUNDLE_CALL_UID, accountInfo.getmDeviceUID());
                                bundle.putBoolean(App.BUNDLE_IS_CALL_TO, true);
                                bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, true);
                                DeviceBean d = new DeviceBean();
                                d.setMid(accountInfo.getmAccountId());
                                d.setMidName(accountInfo.getmNickName());
                                intent.putExtra(Const.EXTRA_DEVICE, d);
                                intent.putExtras(bundle);
                                RobotPreviewActivity.this.startActivityForResult(intent, App.INTENT_REQUEST_CALL_TO);
                                finish();
                                if (tipDialog != null) {
                                    tipDialog.dismiss();
                                }

                            }
                        }, 1500);


                break;
            case R.id.take_photo:
                if (SdCardUtil.getSDCardInfo().isExist) {
                    sLogic.snapshot();
                }
                break;

            case R.id.swith_btn:

                break;
/*            case R.id.fullscreen_btn:
                if (mViewDataBinding.controlPanel.getVisibility() == View.VISIBLE) {
                    mViewDataBinding.controlPanel.setVisibility(View.GONE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //TUTKMedia.TK_getInstance().TK_preview_setScaleType(MediaCodecUtils.SCALE_ASPECT);
                } else {
                    mViewDataBinding.controlPanel.setVisibility(View.VISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //TUTKMedia.TK_getInstance().TK_preview_setScaleType(MediaCodecUtils.SCALE_ASPECT_FILL);
                }


                break;*/
            default:
                break;
        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) { // 按下
                switch (view.getId()) {
                    case R.id.feet_controll_center:
                        sLogic.sendIOCtrl(CustomCommand.FEET_MOTOR_STOP);
                        //mP2PKit.SendUserMessage(mCallid,String.valueOf(MOTOR_STOP));
                        break;
                    case R.id.feet_controll_up:
                        sLogic.sendIOCtrl(CustomCommand.FEET_MOTOR_FORWARD);
                        //mP2PKit.SendUserMessage(mCallid,String.valueOf(MOTOR_FORWARD));
                        Log.e("--", "SendUserMessage MOTOR_FORWARD");
                        break;
                    case R.id.feet_controll_down:
                        sLogic.sendIOCtrl(CustomCommand.FEET_MOTOR_BACK);
                        //mP2PKit.SendUserMessage(mCallid,String.valueOf(MOTOR_BACK));
                        Log.e("--", "SendUserMessage MOTOR_STOP");
                        break;
                    case R.id.feet_controll_left:
                        sLogic.sendIOCtrl(CustomCommand.FEET_MOTOR_LEFT);
                        //mP2PKit.SendUserMessage(mCallid,String.valueOf(MOTOR_LEFT));
                        break;
                    case R.id.feet_controll_right:
                        sLogic.sendIOCtrl(CustomCommand.FEET_MOTOR_RIGHT);
                        //mP2PKit.SendUserMessage(mCallid,String.valueOf(MOTOR_RIGHT));
                        break;
                    case R.id.head_controll_center:
                        sLogic.sendIOCtrl(CustomCommand.HEAD_MOTOR_STOP);
                        break;
                    case R.id.head_controll_up:
                        sLogic.sendIOCtrl(CustomCommand.HEAD_MOTOR_FORWARD);
                        break;
                    case R.id.head_controll_down:
                        sLogic.sendIOCtrl(CustomCommand.HEAD_MOTOR_BACK);
                        break;
                    case R.id.head_controll_left:
                        sLogic.sendIOCtrl(CustomCommand.HEAD_MOTOR_LEFT);
                        break;
                    case R.id.head_controll_right:
                        sLogic.sendIOCtrl(CustomCommand.HEAD_MOTOR_RIGHT);
                        break;
                }
                Log.e("--", "onTouchListener  ACTION_DOWN id:" + view.getId());
            } else if (action == MotionEvent.ACTION_UP) { // 松开
                if (view.getId() == R.id.feet_controll_up || view.getId() == R.id.feet_controll_down || view.getId() == R.id.feet_controll_left || view.getId() == R.id.feet_controll_right) {
                    Log.d("-------=======", "我能停止脚上的电机了");
                    sLogic.sendIOCtrl(CustomCommand.FEET_MOTOR_STOP);
                }
                if (view.getId() == R.id.head_controll_up || view.getId() == R.id.head_controll_down || view.getId() == R.id.head_controll_left || view.getId() == R.id.head_controll_right) {
                    Log.d("======------", "wo");
                    sLogic.sendIOCtrl(CustomCommand.HEAD_MOTOR_STOP);
                }
                Log.e("--", "onTouchListener  ACTION_UP id:" + view.getId());
            }
            return false;

        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            sLogic.callOff();
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).
                newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ON_AFTER_RELEASE, "Robot");
        wakeLock.acquire();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //设置预览缩放类型
        //TUTKMedia.TK_getInstance().TK_preview_setScaleType(MediaCodecUtils.SCALE_ASPECT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 停止播放铃音
         */
        stopRing();
        sLogic.unbind();
        sLogic.onDestory();
        mJoined = false;
        if (mTimer != null) {
            mTimer.cancel();
        }
        activity=null;
    }


    /**
     * 播放铃音
     */
    private void startRing() {
        try {
            player = MediaPlayer.create(this, R.raw.video_request);
            player.setLooping(true);//循环播放
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放铃音
     */
    private void stopRing() {
        try {
            if (null != player) {
                player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
