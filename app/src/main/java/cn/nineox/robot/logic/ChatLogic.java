package cn.nineox.robot.logic;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityChatBinding;
import cn.nineox.robot.databinding.ActivityChatRecordBinding;
import cn.nineox.robot.databinding.ItemChat0Binding;
import cn.nineox.robot.databinding.ItemConversationRecordBinding;
import cn.nineox.robot.databinding.ItemVideoRecordBinding;
import cn.nineox.robot.logic.bean.ChatBean;
import cn.nineox.robot.logic.bean.ChatListBean;
import cn.nineox.robot.logic.bean.ChatRecordBean;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.inf.BaseTypeBindingAdapter;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.weiget.decoration.HorizontalDividerItemDecoration;

/**
 * Created by me on 17/11/2.
 */

public class ChatLogic extends BasicLogic<ActivityChatBinding> implements OnRefreshListener,OnRefreshLoadmoreListener,BaseItemPresenter<ChatBean>{

    private BaseBindingAdapter mAdapter;

    private Activity mActivity;

    private List<ChatBean> mDatas;

    private int mCurrentPage = 0;


    private ChatRecordBean mChatRecord;

    public ChatLogic(Activity activity, ActivityChatBinding dataBinding,ChatRecordBean chatRecordBean) {
        super(dataBinding);
        mActivity = activity;
        this.mChatRecord = chatRecordBean;
    }


    public BaseBindingAdapter initAdapter(){
        mDataBinding.refreshLayout.setOnRefreshListener(this);
        mDataBinding.refreshLayout.setOnLoadmoreListener(this);
        mDataBinding.refreshLayout.setEnableAutoLoadmore(true);
        mDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mDataBinding.recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mActivity).color(Color.TRANSPARENT).sizeResId(R.dimen.row_spacing).build());
        mDatas = new ArrayList<>();
        mAdapter = new BaseTypeBindingAdapter(mActivity,mDatas);
        mDataBinding.recyclerView.setAdapter(mAdapter);
        mAdapter.setItemPresenter(this);
        return mAdapter;
    }


    public void listByChat(){
        EntityRequest request = new EntityRequest(Const.URL_CHATRECORD_LISTBYCHAT,ChatListBean.class);
        request.add("mid", mChatRecord.getMid());
        request.add("chatDate", mChatRecord.getChatDate());
        request.add("start",mCurrentPage);
        request.add("size", 20);
        execute(request,new ResponseListener<ChatListBean>(){

            @Override
            public void onSucceed(int what, Result<ChatListBean> result) {
                super.onSucceed(what, result);
                if(mCurrentPage == 0){
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
        listByChat();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mCurrentPage = 0;
        listByChat();
    }

    @Override
    public void onItemClick(ChatBean chatBean, int position) {

    }

    @Override
    public boolean onItemLongClick(ChatBean chatBean, int position) {
        return false;
    }
}
