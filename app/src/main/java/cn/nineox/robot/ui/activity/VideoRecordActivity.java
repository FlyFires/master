package cn.nineox.robot.ui.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityVideoRecordBinding;
import cn.nineox.robot.databinding.ItemConversationRecordBinding;
import cn.nineox.robot.logic.bean.ChatRecordBean;
import cn.nineox.robot.logic.bean.VideoRecordBean;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;

/**
 * Created by me on 17/10/29.
 */

public class VideoRecordActivity extends BasicActivity<ActivityVideoRecordBinding> implements BaseItemPresenter<ChatRecordBean>{


    private BaseBindingAdapter mAdapter;


    private List<VideoRecordBean> mDatas;


    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.toolbarLayout.titleBar.setTitle("全部视频记录");
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        intData();
        mViewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // ★泛型D:是Bean类型，如果有就传。  泛型B:是对应的xml Layout的Binding类
        mAdapter = new BaseBindingAdapter<VideoRecordBean, ItemConversationRecordBinding>(this, mDatas, R.layout.item_conversation_record) {
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemConversationRecordBinding> holder, int position) {
                //★super一定不要删除
                super.onBindViewHolder(holder, position);
            }
        };
        mAdapter.setItemPresenter(this);
        mViewDataBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_record;
    }

    private void intData(){
        int i = 1;
//        mDatas = new ArrayList<>();
//        mDatas.add(new VideoRecordBean("2017/10/"+ i++));
//        mDatas.add(new VideoRecordBean("2017/10/"+ i++));
//        mDatas.add(new VideoRecordBean("2017/10/"+ i++));
//        mDatas.add(new VideoRecordBean("2017/10/"+ i++));
//        mDatas.add(new VideoRecordBean("2017/10/"+ i++));
//        mDatas.add(new VideoRecordBean("2017/10/"+ i++));
    }



    @Override
    public void onItemClick(ChatRecordBean chatRecordBean, int position) {

    }

    @Override
    public boolean onItemLongClick(ChatRecordBean chatRecordBean, int position) {
        return false;
    }
}
