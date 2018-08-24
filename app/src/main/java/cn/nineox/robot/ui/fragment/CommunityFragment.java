package cn.nineox.robot.ui.fragment;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicFragment;
import cn.nineox.robot.databinding.FragmentCommunityBinding;
import cn.nineox.xframework.base.BaseFragment;

/**
 *
 * 社区
 * Created by me on 17/10/29.
 */

public class CommunityFragment extends BasicFragment<FragmentCommunityBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_community;
    }

    @Override
    protected void createViewBinding() {
        mViewDataBinding.toolbarLayout.titleBar.setTitle("社区");
    }
}
