package cn.nineox.robot.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityChatBinding;
import cn.nineox.robot.logic.VideoDetailLogic;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.VideoRecordBean;

/**
 * Created by Eval on 2017/11/16.
 */
public class VideoDetailActivity extends BasicActivity<ActivityChatBinding> {

    private VideoDetailLogic mLogic;

    private VideoRecordBean mChatRecord;

    private DeviceBean mDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDevice = (DeviceBean) getIntent().getSerializableExtra(Const.EXTRA_DEVICE);

        mChatRecord = (VideoRecordBean) getIntent().getSerializableExtra(Const.EXTRA_CHATRECORD);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mLogic = new VideoDetailLogic(this,mViewDataBinding,mChatRecord);
        mLogic.initAdapter();
        mViewDataBinding.toolbarLayout.titleBar.setTitle(TextUtils.isEmpty(mDevice.getMidName())? "视频记录":mDevice.getMidName());
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewDataBinding.recyclerView.setPadding(0,getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin),0,0);
        mLogic.listByVideo();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }
}
