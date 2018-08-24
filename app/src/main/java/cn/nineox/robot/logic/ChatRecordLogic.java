package cn.nineox.robot.logic;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityChatRecordBinding;
import cn.nineox.robot.databinding.ItemConversationRecordBinding;
import cn.nineox.robot.databinding.ItemVideoRecordBinding;
import cn.nineox.robot.logic.bean.ChatRecordBean;
import cn.nineox.robot.logic.bean.ChatRecordListBean;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.PageBean;
import cn.nineox.robot.logic.bean.VideoRecordListBean;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;

/**
 * Created by me on 17/11/2.
 */

public class ChatRecordLogic extends BasicLogic<ActivityChatRecordBinding> implements OnRefreshListener,OnRefreshLoadmoreListener {

    private BaseBindingAdapter mAdapter;


    private Activity mActivity;

    private List<ChatRecordBean> mDatas;

    private int mType;

    private PageBean mPageBean;

    private DeviceBean mDeviceBean;


    public ChatRecordLogic(Activity activity, ActivityChatRecordBinding dataBinding, DeviceBean deviceBean) {
        super( dataBinding);
        mActivity = activity;
        this.mDeviceBean = deviceBean;
    }


    public BaseBindingAdapter initAdapter(int type){
        mType = type;
        mDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mDatas = new ArrayList<>();
        if(type == 0){
            mAdapter = new BaseBindingAdapter<ChatRecordBean, ItemVideoRecordBinding>(mActivity, mDatas, R.layout.item_video_record);
        }else{
            mAdapter = new BaseBindingAdapter<ChatRecordBean,ItemConversationRecordBinding>(mActivity, mDatas, R.layout.item_conversation_record);
        }

        mDataBinding.recyclerView.setAdapter(mAdapter);
        mDataBinding.refreshLayout.setOnRefreshListener(this);
        mDataBinding.refreshLayout.setOnLoadmoreListener(this);
        mDataBinding.refreshLayout.setEnableAutoLoadmore(true);
        return mAdapter;
    }

    public void chatRecordList(String mid){
        final EntityRequest request = new EntityRequest(Const.URL_CHATRECORD_LIST,ChatRecordListBean.class);
        int nextPage =  mPageBean == null ? 0:mPageBean.getNextPage();
        request.add("mid", mid);
        request.add("size", 15);
        request.add("start",nextPage);
        execute(request,new ResponseListener<ChatRecordListBean>(){

            @Override
            public void onSucceed(int what, Result<ChatRecordListBean> result) {
                super.onSucceed(what, result);
                mPageBean = result.getResult();
                if(mPageBean.getPageCount() <= 1){
                    mAdapter.removeAllData();
                }
                mPageBean.setCurrentPage(mPageBean.getPageCount() -1);
                mAdapter.addDatas(result.getResult().getList());
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
                mDataBinding.emptyView.success(mAdapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDataBinding.refreshLayout.autoRefresh();
                    }
                });
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
                //new Toastor(mActivity).showSingletonToast(error);
                mDataBinding.emptyView.error(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDataBinding.refreshLayout.autoRefresh();
                    }
                });
            }
        });
    }

    public void videoRecordList(String mid){
        EntityRequest request = new EntityRequest(Const.URL_VIDEORECORD_LIST,VideoRecordListBean.class);
        request.add("mid", mid);
        request.add("size", 15);
        //request.add("start", 10 * mPageBean.getPageCount());
        request.add("start", mPageBean == null ? 0:mPageBean.getNextPage());
        execute(request,new ResponseListener<VideoRecordListBean>(){

            @Override
            public void onSucceed(int what, Result<VideoRecordListBean> result) {
                super.onSucceed(what, result);
                mPageBean = result.getResult();
                if(mPageBean.getPageCount() <= 1){
                    mAdapter.removeAllData();
                }
                mPageBean.setCurrentPage(mPageBean.getPageCount() -1);

                mAdapter.addDatas(result.getResult().getList());
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
                mDataBinding.emptyView.success(mAdapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDataBinding.refreshLayout.autoRefresh();
                    }
                });
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
                //new Toastor(mActivity).showSingletonToast(error);
                mDataBinding.emptyView.error(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDataBinding.refreshLayout.autoRefresh();
                    }
                });
            }
        });
    }


    public void listByChat(String mid,String chatDate){
        EntityRequest request = new EntityRequest(Const.URL_CHATRECORD_LISTBYCHAT,VideoRecordListBean.class);
        request.add("mid", mid);
        request.add("chatDate", chatDate);
        request.add("pageId", mPageBean == null ? 0:mPageBean.getNextPage());
        request.add("size", 10);
        execute(request,new ResponseListener<VideoRecordListBean>(){

            @Override
            public void onSucceed(int what, Result<VideoRecordListBean> result) {
                super.onSucceed(what, result);
                mPageBean = result.getResult();
                if(mPageBean.getCurrentPage() <= 1){
                    mAdapter.removeAllData();
                }
                mAdapter.addDatas(result.getResult().getList());
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
            }
        });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if(mType == 0){
            videoRecordList(mDeviceBean.getMid());
        }else{
            chatRecordList(mDeviceBean.getMid());
        }

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if(mPageBean == null){
            mPageBean = new PageBean();
        }
        mPageBean.setCurrentPage(-1);
        if(mType == 0){
            videoRecordList(mDeviceBean.getMid());
        }else{
            chatRecordList(mDeviceBean.getMid());
        }

    }
}
