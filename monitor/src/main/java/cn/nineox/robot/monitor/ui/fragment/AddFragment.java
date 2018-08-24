package cn.nineox.robot.monitor.ui.fragment;

import android.view.View;

import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.databinding.FragmentAddBinding;
import cn.nineox.robot.monitor.logic.AddLogic;
import cn.nineox.xframework.base.BaseFragment;

/**
 * Created by me on 18/4/22.
 */

public class AddFragment extends BaseFragment<FragmentAddBinding> {


    private AddLogic sLogic;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void createViewBinding() {
        sLogic = new AddLogic(this,mViewDataBinding);
        sLogic.getApkUrl();
        mViewDataBinding.setClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_iv:
                pop();
                break;
        }
    }
}
