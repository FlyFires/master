package cn.nineox.robot.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import cn.nineox.robot.R;
import cn.nineox.robot.databinding.ActivityWaitCallBinding;
import cn.nineox.robot.logic.WaitCallLogic;
import cn.nineox.xframework.base.BaseActivity;
import cn.nineox.xframework.core.android.log.Log;

public class WaitCallActivity extends BaseActivity<ActivityWaitCallBinding> {
    public WaitCallLogic sLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        getWindow().addFlags(flags);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        getWindow().setAttributes(params);

        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        createViewBinding();
    }


    @Override
    protected void onCreateView() {

    }

    @NonNull
    @Override
    protected void createViewBinding() {
        sLogic = new WaitCallLogic(this, mViewDataBinding);
        sLogic.initData();
        mViewDataBinding.setClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wait_call;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
    protected void onDestroy() {
        super.onDestroy();
        Log.e("--", "onDestroy");
        sLogic.unbind();
    }
}
