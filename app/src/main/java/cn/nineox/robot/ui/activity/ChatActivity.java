package cn.nineox.robot.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityChatBinding;
import cn.nineox.robot.logic.ChatLogic;
import cn.nineox.robot.logic.bean.ChatRecordBean;
import cn.nineox.robot.logic.bean.DeviceBean;

/**
 * Created by Eval on 2017/11/16.
 */
public class ChatActivity extends BasicActivity<ActivityChatBinding> {

    private ChatLogic mLogic;

    private ChatRecordBean mChatRecord;

    private DeviceBean mDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDevice = (DeviceBean) getIntent().getSerializableExtra(Const.EXTRA_DEVICE);

        mChatRecord = (ChatRecordBean) getIntent().getSerializableExtra(Const.EXTRA_CHATRECORD);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mLogic = new ChatLogic(this,mViewDataBinding,mChatRecord);
        mLogic.initAdapter();
        mViewDataBinding.toolbarLayout.titleBar.setTitle("聊天记录");
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLogic.listByChat();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }
}
