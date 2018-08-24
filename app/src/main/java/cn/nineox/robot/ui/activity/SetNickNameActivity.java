package cn.nineox.robot.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivitySetNameBinding;
import cn.nineox.robot.databinding.ActivitySetShenfenBinding;
import cn.nineox.robot.logic.SetNickNameLogic;
import cn.nineox.robot.logic.SetShenfenogic;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;

/**
 * Created by me on 17/10/31.
 */

public class SetNickNameActivity extends BasicActivity<ActivitySetNameBinding> {

    private SetNickNameLogic mLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);
        mLogic = new SetNickNameLogic(this, mViewDataBinding);
        this.setFinishOnTouchOutside(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_name;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                mLogic.save();
                break;
        }
    }
}
