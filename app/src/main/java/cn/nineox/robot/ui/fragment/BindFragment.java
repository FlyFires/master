package cn.nineox.robot.ui.fragment;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicFragment;
import cn.nineox.robot.databinding.FragmentBindBinding;
import cn.nineox.xframework.base.BaseFragment;

/**
 * Created by me on 17/10/22.
 */

public class BindFragment extends BasicFragment<FragmentBindBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bind;
    }

    @Override
    protected void createViewBinding() {
        mViewDataBinding.toolbarLayout.titleBar.setTitle(R.string.my_device);
    }
}
