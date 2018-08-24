package cn.nineox.robot.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityChatRecordBinding;
import cn.nineox.robot.databinding.ItemConversationRecordBinding;
import cn.nineox.robot.databinding.ItemVideoRecordBinding;
import cn.nineox.robot.logic.ChatRecordLogic;
import cn.nineox.robot.logic.bean.ChatRecordBean;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.VideoRecordBean;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;

/**
 * Created by me on 17/10/29.
 */

public class ChatRecordActivity extends BasicActivity<ActivityChatRecordBinding> implements BaseItemPresenter<ChatRecordBean>{




    private List<ChatRecordBean> mDatas;

    private ChatRecordLogic mLogic;

    private DeviceBean mDeviceBean;

    private int mType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle = getIntent().getExtras();
        mDeviceBean = (DeviceBean) bundle.getSerializable(Const.EXTRA_DEVICE);
        mType = bundle.getInt(Const.EXTRA_TYPE);
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.toolbarLayout.titleBar.setTitle(mType == 0 ? "全部视频记录":"全部交流记录");
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLogic = new ChatRecordLogic(this,mViewDataBinding,mDeviceBean);
        BaseBindingAdapter mAdapter = mLogic.initAdapter(mType);
        mAdapter.setItemPresenter(this);
        mViewDataBinding.refreshLayout.autoRefresh();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_record;
    }



    @Override
    public void onItemClick(ChatRecordBean chatRecordBean, int position) {
        if(mType == 0){
            Intent intent = new Intent(this,VideoDetailActivity.class);
            intent.putExtra(Const.EXTRA_CHATRECORD,(VideoRecordBean)chatRecordBean);
            intent.putExtra(Const.EXTRA_DEVICE,mDeviceBean);
            this.startActivity(intent);
        }else{
            Intent intent = new Intent(this,ChatActivity.class);
            intent.putExtra(Const.EXTRA_CHATRECORD,chatRecordBean);
            intent.putExtra(Const.EXTRA_DEVICE,mDeviceBean);
            this.startActivity(intent);
        }

    }

    @Override
    public boolean onItemLongClick(ChatRecordBean chatRecordBean, int position) {
        return false;
    }
}
