package cn.nineox.robot.monitor;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.databinding.ActivityCallBinding;
import cn.nineox.robot.monitor.logic.CallLogic;
import cn.nineox.robot.monitor.utils.LogUtil;
import cn.nineox.xframework.base.BaseActivity;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.utils.DisplayUtil;

/**
 * Created by me on 17/10/31.OØ
 */

public class CallActivity extends BaseActivity<ActivityCallBinding> {

    private MediaPlayer player;

    private AlertDialog dialog;

    private long mStartTime;

    public CallLogic mLogic;

    private static Activity activity;
    public static Activity getinstance(){
        return  activity ;
    }
    private PowerManager.WakeLock mWakeLock;

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
        //intP2PExtra();
        activity=this;

    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);
        mStartTime = System.currentTimeMillis();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_call;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hangup:
                //mLogic.callOff();
                finish();
                break;
//            case R.id.accpetCall:
//                mLogic.answer();
//                break;

        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock = DisplayUtil.wakeLock(this);

    }


    @Override
    public void finish() {
        LogUtil.debug("callactivity finish");
        mLogic.callOff();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLogic.unbind();
        App.sHasInvitePermission = false;
        activity = null;
        //App.sConnectList.clear();
        mLogic.telRecordSave("", 1, mStartTime, System.currentTimeMillis());
        DisplayUtil.releaseWakeLock(mWakeLock);
    }


}
