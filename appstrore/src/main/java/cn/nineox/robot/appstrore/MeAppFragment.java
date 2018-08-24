package cn.nineox.robot.appstrore;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.nineox.robot.appstrore.databinding.FragmentApplistBinding;
import cn.nineox.robot.appstrore.logic.MeLogic;
import cn.nineox.robot.appstrore.logic.bean.DownloadEvent;
import cn.nineox.robot.appstrore.logic.bean.InstallEvent;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.core.weiget.decoration.HorizontalDividerItemDecoration;
import cn.nineox.xframework.core.weiget.decoration.VerticalDividerItemDecoration;
import me.yokeyword.fragmentation.SupportFragment;

public class MeAppFragment extends BaseFragment<FragmentApplistBinding> implements OnRefreshListener{


    private MeLogic mLogic;

    private String category;

    public static SupportFragment newInstance(int position, String category){
        return new MeAppFragment();
    }

    @NonNull
    @Override
    protected void createViewBinding() {


    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        EventBus.getDefault().register(this);
        mViewDataBinding.refreshLayout.setOnRefreshListener(this);
        mLogic = new MeLogic(this,mViewDataBinding);
        mViewDataBinding.recyclerview.setLayoutManager(
                new GridLayoutManager(this.getActivity(),3));
        mViewDataBinding.recyclerview.addItemDecoration(
                new VerticalDividerItemDecoration.Builder(this.getContext()).color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.app_item_devider).build());
        mViewDataBinding.recyclerview.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this.getContext()).color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.app_item_devider).build());

        mViewDataBinding.refreshLayout.autoRefresh();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_applist;
    }


    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mLogic.meApplist();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InstallEvent event) {
        mViewDataBinding.refreshLayout.autoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
