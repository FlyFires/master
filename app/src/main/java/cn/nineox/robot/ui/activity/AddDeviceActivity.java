package cn.nineox.robot.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.yanzhenjie.nohttp.tools.NetUtils;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityAddDeviceBinding;
import cn.nineox.robot.logic.AddDeviceLogic;

import static cn.nineox.robot.common.Const.EXTRA_MID;

/**
 * Created by me on 17/10/31.
 */

public class AddDeviceActivity extends BasicActivity<ActivityAddDeviceBinding> {

    private AddDeviceLogic mLogic;


    private String mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mid = getIntent().getStringExtra(EXTRA_MID);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);
        mLogic = new AddDeviceLogic(this,mViewDataBinding);
        //mLogic.equipmentGet(mid);
        mViewDataBinding.mid.setText(mid);
        mLogic.getUserTypes(mid);
        this.setFinishOnTouchOutside(true);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_device:
                mLogic.equipmentSave();
                //finish();
                break;
        }
    }
}
