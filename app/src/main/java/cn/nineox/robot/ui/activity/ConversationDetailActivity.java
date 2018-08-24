package cn.nineox.robot.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityConversationDetailBinding;
import cn.nineox.robot.logic.ConversationDetailLogic;
import cn.nineox.robot.logic.bean.DeviceBean;

/**
 * Created by me on 17/10/31.
 */

public class ConversationDetailActivity extends BasicActivity<ActivityConversationDetailBinding> {


    private DeviceBean mDeviceBean;

    private ConversationDetailLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDeviceBean = (DeviceBean) getIntent().getSerializableExtra(Const.EXTRA_DEVICE);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mLogic = new ConversationDetailLogic(this,mViewDataBinding);
        mViewDataBinding.toolbarLayout.titleBar.setTitle("通信详情");
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLogic.equipmentGet(mDeviceBean.getMid());
        initChatRecord();
    }

    private void initChatRecord(){
        mLogic.chatRecordList(this,mDeviceBean);
        mLogic.videoRecordList(this,mDeviceBean);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conversation_detail;
    }
}
