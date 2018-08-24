package cn.nineox.robot.monitor;

import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

import cn.nineox.robot.monitor.databinding.ActivityWaitCallBinding;
import cn.nineox.robot.monitor.logic.WaitCallLogic;
import cn.nineox.robot.monitor.utils.LogUtil;
import cn.nineox.xframework.base.BaseActivity;
import cn.nineox.xframework.core.common.utils.DisplayUtil;

public class WaitCallActivity extends BaseActivity<ActivityWaitCallBinding>{
    public WaitCallLogic sLogic;
    private PowerManager.WakeLock mWakeLock;

    public static boolean mWaiting = false;

    @NonNull
    @Override
    protected void createViewBinding() {
        mWaiting = true;
        sLogic = new WaitCallLogic(this,mViewDataBinding);
        sLogic.initData();
        mViewDataBinding.setClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wait_call;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hangup:
                sLogic.callOff();
                finish();
                break;
            case R.id.accpetCall:
                sLogic.answer();
                break;
        }
    }



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
        mWakeLock = DisplayUtil.wakeLock(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.debug("waitactivity onDestroy");
        sLogic.unbind();
        DisplayUtil.releaseWakeLock(mWakeLock);
        mWaiting = false;
    }
}
