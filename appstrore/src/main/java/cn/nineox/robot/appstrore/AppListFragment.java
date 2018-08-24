package cn.nineox.robot.appstrore;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.nineox.robot.appstrore.databinding.FragmentApplistBinding;
import cn.nineox.robot.appstrore.logic.AppListLogic;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.DownloadEvent;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.weiget.decoration.HorizontalDividerItemDecoration;
import cn.nineox.xframework.core.weiget.decoration.VerticalDividerItemDecoration;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 17/11/18.
 */

public class AppListFragment extends BaseFragment<FragmentApplistBinding> implements OnRefreshListener,OnLoadmoreListener {
    private AppListLogic mLogic;

    private List<AppBean> mDatas;

    private int position;

    private String mCategory;

    public static Fragment newInstance(List<AppBean> datas){
        return new AppListFragment(datas);
    }


    public static SupportFragment newInstance(int position, String category){
        return new AppListFragment(position,category);
    }


    public AppListFragment(List<AppBean> datas){
        this.mDatas = datas;
    }



    public AppListFragment(int position,String category){
        this.position = position;
        this.mCategory = category;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_applist;
    }

    @Override
    protected void createViewBinding() {


    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mLogic = new AppListLogic(this,mViewDataBinding);
        mViewDataBinding.recyclerview.setLayoutManager(
                new GridLayoutManager(this.getActivity(),3));
        mViewDataBinding.recyclerview.addItemDecoration(
                new VerticalDividerItemDecoration.Builder(this.getContext()).color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.app_item_devider).build());
        mViewDataBinding.recyclerview.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this.getContext()).color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.app_item_devider).build());

        //mLogic.adapter(mDatas);
        mViewDataBinding.refreshLayout.setOnRefreshListener(this);
        mViewDataBinding.refreshLayout.setOnLoadmoreListener(this);
        mViewDataBinding.refreshLayout.setEnableLoadmore(true);
        //mLogic.applist(position,mCategory);
        mViewDataBinding.refreshLayout.autoRefresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadEvent event) {
        mLogic.notifyDataSetChanged();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if(mLogic != null && mLogic.mAdapter != null ){
            Log.e("--","onSupportVisible:"+ mCategory  + "  " + mLogic.mAdapter.getItemCount());
            mLogic.mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        this.position = position + 1;
        mLogic.applist(position,mCategory);

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        this.position = 1;
        mLogic.applist(position,mCategory);
    }


    public void postNotifyDataChanged() {
        mLogic.notifyDataSetChanged();
    }
}
