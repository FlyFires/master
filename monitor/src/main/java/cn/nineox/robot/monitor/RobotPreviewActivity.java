package cn.nineox.robot.monitor;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.databinding.ActivityRobotPreviewBinding;
import cn.nineox.robot.monitor.logic.RobotPreviewLogic;
import cn.nineox.xframework.base.BaseActivity;

public class RobotPreviewActivity extends BaseActivity<ActivityRobotPreviewBinding>{

    public RobotPreviewLogic sLogic;
    @NonNull
    @Override
    protected void createViewBinding() {
        this.sendBroadcast(new Intent(Const.ACTION_MONITOR_START));
        Window window = getWindow();
        window.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 10;
        params.width = 10;
        window.setAttributes(params);
        sLogic = new RobotPreviewLogic(this,mViewDataBinding);
        sLogic.initData();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            sLogic.callOff();
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_robot_preview;
    }

    @Override
    protected void onDestroy() {
        this.sendBroadcast(new Intent(Const.ACTION_MONITOR_END));
        super.onDestroy();
    }
}
