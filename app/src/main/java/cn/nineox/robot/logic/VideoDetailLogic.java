package cn.nineox.robot.logic;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityChatBinding;
import cn.nineox.robot.logic.bean.ChatBean;
import cn.nineox.robot.logic.bean.VideoDetailBean;
import cn.nineox.robot.logic.bean.VideoDetailListBean;
import cn.nineox.robot.logic.bean.VideoRecordBean;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;

/**
 * Created by me on 17/11/2.
 */

public class VideoDetailLogic extends BasicLogic<ActivityChatBinding> implements OnRefreshListener, OnRefreshLoadmoreListener, BaseItemPresenter<ChatBean> {

    private BaseBindingAdapter mAdapter;

    private Activity mActivity;

    private List<VideoDetailBean> mDatas;

    private int mCurrentPage = 0;


    private VideoRecordBean mChatRecord;

    public VideoDetailLogic(Activity activity, ActivityChatBinding dataBinding, VideoRecordBean chatRecordBean) {
        super(dataBinding);
        mActivity = activity;
        this.mChatRecord = chatRecordBean;
    }


    public BaseBindingAdapter initAdapter() {
        mDataBinding.refreshLayout.setOnRefreshListener(this);
        mDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mDatas = new ArrayList<>();
        mAdapter = new BaseBindingAdapter(mActivity, mDatas, R.layout.item_video_detail);
        mDataBinding.recyclerView.setAdapter(mAdapter);
        mAdapter.setItemPresenter(this);
        return mAdapter;
    }


    public void listByVideo() {
        EntityRequest request = new EntityRequest(Const.URL_VIDEORECORD_LISTVIDEO, VideoDetailListBean.class);
        request.add("mid", mChatRecord.getMid());
        request.add("chatDate", mChatRecord.getVideoDate());
        request.add("start", mCurrentPage);
        request.add("size", 10);
        execute(request, new ResponseListener<VideoDetailListBean>() {

            @Override
            public void onSucceed(int what, Result<VideoDetailListBean> result) {
                super.onSucceed(what, result);
                if (mCurrentPage == 0) {
                    mAdapter.removeAllData();
                }
                mAdapter.addDatas(result.getResult().getList());
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
            }
        });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        mCurrentPage += 1;
        listByVideo();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mCurrentPage = 0;
        listByVideo();
    }

    @Override
    public void onItemClick(ChatBean chatBean, int position) {

    }

    @Override
    public boolean onItemLongClick(ChatBean chatBean, int position) {
        return false;
    }
}
