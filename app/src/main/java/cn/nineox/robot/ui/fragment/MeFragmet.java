package cn.nineox.robot.ui.fragment;

import android.content.Intent;
import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicFragment;
import cn.nineox.robot.databinding.FragmentMeBinding;
import cn.nineox.robot.logic.MeLogic;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.activity.FeedbackActivity;
import cn.nineox.robot.ui.activity.PersonalActivity;
import cn.nineox.robot.utils.GlideUtils;
import cn.nineox.xframework.base.BaseFragment;

/**
 * 我的
 * Created by me on 17/10/16.
 */

public class MeFragmet extends BasicFragment<FragmentMeBinding> {

    private MeLogic mLogic;

    @Override
    protected void createViewBinding() {
        mLogic = new MeLogic(mViewDataBinding);
        mViewDataBinding.setClickListener(this);
        mViewDataBinding.toolbarLayout.titleBar.setTitle("个人中心");
        mLogic.userGet();

        GlideUtils.loadRoundImageView(this.getActivity(),APPDataPersistent.getInstance().getUserBean().getHeadPic(), mViewDataBinding.headIv,R.drawable.ic_portrait,R.drawable.ic_portrait);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.help:
                startActivity(new Intent(MeFragmet.this.getContext(),FeedbackActivity.class));
                break;
            case R.id.head:
                startActivity(new Intent(MeFragmet.this.getContext(),PersonalActivity.class));
                break;

        }
    }

}
