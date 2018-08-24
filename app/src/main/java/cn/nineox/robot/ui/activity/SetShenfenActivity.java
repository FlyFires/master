package cn.nineox.robot.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivitySetShenfenBinding;
import cn.nineox.robot.logic.SetShenfenogic;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.xframework.core.common.assist.Toastor;

/**
 * Created by me on 17/10/31.
 */

public class SetShenfenActivity extends BasicActivity<ActivitySetShenfenBinding> {

    private SetShenfenogic mLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);
        mLogic = new SetShenfenogic(this, mViewDataBinding);
        DeviceBean deviceBean = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
        if(deviceBean == null || TextUtils.isEmpty(deviceBean.getMid())){
            new Toastor(this).showSingletonToast("请先绑定设备");
            finish();
            return;
        }
        mLogic.getUserTypes(deviceBean == null ? "" :deviceBean.getMid());
        mViewDataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.setFinishOnTouchOutside(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_shenfen;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_device:
                mLogic.save();
                //finish();
                break;
            case R.id.root:
                break;
            case R.id.close:
                finish();
                break;
        }
    }
}
