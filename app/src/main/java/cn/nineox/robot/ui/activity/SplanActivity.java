package cn.nineox.robot.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivitySplanBinding;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.MainActivity;

/**
 * Created by me on 17/11/27.
 */

public class SplanActivity extends BasicActivity<ActivitySplanBinding>{
    @NonNull
    @Override
    protected void createViewBinding() {
        final LoginInfoBean loginInfoBean = APPDataPersistent.getInstance().getLoginInfoBean();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(loginInfoBean.hasLogin()){
                    intent = new Intent(SplanActivity.this,MainActivity.class);
                }else{
                    intent = new Intent(SplanActivity.this,LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },1500);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splan;
    }
}
